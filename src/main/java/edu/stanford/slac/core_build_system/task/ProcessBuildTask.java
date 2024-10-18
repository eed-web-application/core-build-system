package edu.stanford.slac.core_build_system.task;

import edu.stanford.slac.core_build_system.api.v1.dto.BuildStatusDTO;
import edu.stanford.slac.core_build_system.api.v1.dto.ComponentBranchBuildDTO;
import edu.stanford.slac.core_build_system.api.v1.dto.ComponentDTO;
import edu.stanford.slac.core_build_system.api.v1.mapper.ComponentMapper;
import edu.stanford.slac.core_build_system.config.CoreBuildProperties;
import edu.stanford.slac.core_build_system.model.BuildInfo;
import edu.stanford.slac.core_build_system.model.K8SPodBuilder;
import edu.stanford.slac.core_build_system.model.LogEntry;
import edu.stanford.slac.core_build_system.repository.GitServerRepository;
import edu.stanford.slac.core_build_system.repository.KubernetesRepository;
import edu.stanford.slac.core_build_system.repository.LogEntryRepository;
import edu.stanford.slac.core_build_system.service.ComponentBuildService;
import edu.stanford.slac.core_build_system.service.ComponentService;
import io.fabric8.kubernetes.api.model.ContainerStatus;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.dsl.LogWatch;
import io.fabric8.kubernetes.client.dsl.PodResource;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.*;

import static com.google.common.collect.ImmutableList.of;
import static edu.stanford.slac.ad.eed.baselib.exception.Utility.wrapCatch;
import static edu.stanford.slac.core_build_system.api.v1.dto.BuildStatusDTO.*;

@Log4j2
@Component
@RequiredArgsConstructor
public class ProcessBuildTask {
    private final ComponentMapper componentMapper;
    private final CoreBuildProperties coreBuildProperties;
    private final KubernetesRepository kubernetesRepository;
    private final ComponentService componentService;
    private final ComponentBuildService componentBuildService;
    private final LogEntryRepository logEntryRepository;
    private final GitServerRepository gitServerRepository;
    private final Stack<Pod> loggingPod = new Stack<>();


    @Scheduled(fixedDelay = 2000)  // Runs every 5 seconds
    @Transactional
    public void performTask() {
        log.debug("Start processing build task");
        Optional<ComponentBranchBuildDTO> documentToProcess = componentBuildService.getNextBuildToProcess();
        if (documentToProcess.isEmpty()) {
            log.debug("No build to process");
            return;
        }
        // manage the build in the executor service
        processBuild(documentToProcess.get());
    }

    /**
     * Process the build
     *
     * @param buildToProcess The build to process
     */
    @Transactional
    public void processBuild(ComponentBranchBuildDTO buildToProcess) {
        ComponentDTO component = wrapCatch(
                () -> componentService.findById(buildToProcess.componentId()),
                -2
        );
        BuildStatusDTO newStatus = PENDING;
        String logPrefix = "[%s-%s-%s-%s]".formatted(buildToProcess.componentId(), component.name(), buildToProcess.branchName(), buildToProcess.buildOs());
        log.info("{} Start processing", logPrefix);
        try {
            BuildStatusDTO buildStatus = buildToProcess.buildStatus();
            switch (buildStatus) {
                case PENDING:
                    log.info("{} Build is pending", logPrefix);
                    BuildInfo buildInfo = spinPodForBuild(logPrefix, component, buildToProcess);
                    componentBuildService.updateBuildInfo(buildToProcess.id(), buildInfo);
                    // spin-up the pod
                    newStatus = IN_PROGRESS;
                    break;
                case IN_PROGRESS: {
                    log.info("{} Build is in progress", logPrefix);
                    BuildStatusDTO podStatus = getPodStatus(logPrefix, buildToProcess);
                    if (podStatus == IN_PROGRESS) {
                        newStatus = IN_PROGRESS;
                        PodResource foundPod = kubernetesRepository.getPod(
                                coreBuildProperties.getK8sBuildNamespace(),
                                buildToProcess.buildInfo().builderName()
                        );
                        List<ContainerStatus> containerStatus = new ArrayList<>();
                        String containerLog = "";
                        try {
                            containerStatus = foundPod.get().getStatus().getContainerStatuses();
                            containerLog = foundPod.getLog();
                        } catch (Exception e) {
                            log.error("{} Error getting container status or log: {}", logPrefix, e);
                        }
                        log.info("{} Build is still in progress ->\n{}\n{}", logPrefix, containerStatus, containerLog);
                        return;
                    } else {
                        // store the log before switching the status
                        storeLog(logPrefix, buildToProcess);
                        newStatus = podStatus;
                        if (podStatus == BuildStatusDTO.SUCCESS) {
                            log.info("{} Build is completed", logPrefix);
                        } else {
                            log.info("{} Build failed", logPrefix);
                        }
                    }
                    break;
                }
                case SUCCESS:
                    log.info("{} Already completed", logPrefix);
                    break;
                case FAILED:
                    log.info("{} Build Already failed", logPrefix);
                    break;
                case STOP_REQUESTED:  {
                    log.info("{} Stop requested", logPrefix);
                    BuildStatusDTO podStatus = getPodStatus(logPrefix, buildToProcess);
                    if (podStatus == IN_PROGRESS) {
                        stopPod(logPrefix, buildToProcess);
                        newStatus = STOPPED;
                    } else if(podStatus == PENDING) {
                        newStatus = STOPPED;
                    }else {
                        newStatus = podStatus;
                    }
                }
                break;
                case STOPPED:
                    log.info("{} Build stopped", logPrefix);
                    break;
                default:
                    log.error("{} Unknown build status", logPrefix);
                    break;
            }
        } catch (Exception e) {
            log.error("{} Error processing build", logPrefix, e);
        } finally {
            // release lock on build
            boolean lockReleased = componentBuildService.releaseLock(buildToProcess.id(), newStatus);
            if (lockReleased) {
                log.info("{} Lock released", logPrefix);
            } else {
                log.error("{} Lock not released", logPrefix);
            }
        }

    }

    /**
     * Get the status of the pod
     *
     * @param buildToProcess The build to process
     * @return The status of the pod
     */
    private BuildStatusDTO getPodStatus(String logPrefix, ComponentBranchBuildDTO buildToProcess) {
        if(buildToProcess.buildInfo() == null) {
            return PENDING;
        }
        log.info("{} Getting pod status", logPrefix);
        PodResource foundPod = kubernetesRepository.getPod(
                coreBuildProperties.getK8sBuildNamespace(),
                buildToProcess.buildInfo().builderName()
        );
        boolean terminated = foundPod.get().getStatus().getContainerStatuses().size() == 1 &&
                !foundPod.get().getStatus().getContainerStatuses().isEmpty() &&
                foundPod.get().getStatus().getContainerStatuses().getFirst().getState().getTerminated() != null;
        boolean success = terminated && foundPod.get().getStatus().getContainerStatuses().getFirst().getState().getTerminated().getReason().compareToIgnoreCase("Completed") == 0;
        return terminated && success ? BuildStatusDTO.SUCCESS : (terminated ? BuildStatusDTO.FAILED : IN_PROGRESS);
    }

    /**
     * Stop the pod
     *
     * @param buildToProcess The build to process
     */
    private void stopPod(String logPrefix, ComponentBranchBuildDTO buildToProcess) {
        log.info("{} Stopping pod", logPrefix);
        var stopPodResult = kubernetesRepository.stopPod(
                coreBuildProperties.getK8sBuildNamespace(),
                buildToProcess.buildInfo().builderName()
        );
        log.info("{} Pod stopped: {}", logPrefix, stopPodResult);
    }

    /**
     * Get the container status of the pod
     *
     * @param buildToProcess The build to process
     * @return The container status of the pod
     */
    private List<ContainerStatus> getPodContainerStatus(String logPrefix, ComponentBranchBuildDTO buildToProcess) {
        log.info("{} Getting pod container status", logPrefix);
        PodResource foundPod = kubernetesRepository.getPod(
                coreBuildProperties.getK8sBuildNamespace(),
                buildToProcess.buildInfo().builderName()
        );
        return foundPod.get().getStatus().getContainerStatuses();
    }

    /**
     * Store the log of the build
     *
     * @param buildToProcess The build to process
     */
    private void storeLog(String logPrefix, ComponentBranchBuildDTO buildToProcess) {
        log.info("{} Storing log for build {}", logPrefix, buildToProcess);
        PodResource foundPod = kubernetesRepository.getPod(
                coreBuildProperties.getK8sBuildNamespace(),
                buildToProcess.buildInfo().builderName()
        );

        try (LogWatch logWatch = foundPod.watchLog()) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(logWatch.getOutput()));
            String line;
            while ((line = reader.readLine()) != null) {
                LocalDateTime timestamp = extractTimestamp(line);
                String logLine = removeTimestamp(line);
                LogEntry entry = LogEntry.builder().buildId(buildToProcess.id()).timestamp(timestamp).log(logLine).build();
                log.debug("{} Storing log entry: {}", logPrefix, entry);
                logEntryRepository.save(entry);
            }
        } catch (IOException | ParseException e) {
            log.error("{} Error storing log: {}", logPrefix, e);
        }
    }

    /**
     * Extract the timestamp from the log line
     *
     * @param logLine The log line
     * @return The timestamp
     */
    private LocalDateTime extractTimestamp(String logLine) throws ParseException {
//        Pattern pattern = Pattern.compile("^\\[(.*?)\\]");
//        Matcher matcher = pattern.matcher(logLine);
//        if (matcher.find()) {
//            String timestampStr = matcher.group(1);
//            // Adjust the date format to match your log's timestamp format
//            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
//            return LocalDateTime.parse(timestampStr, formatter);
//        } else {
        return LocalDateTime.now();
//        }
    }

    /**
     * Remove the timestamp from the log line
     *
     * @param logLine The log line
     * @return The log line without the timestamp
     */
    private static String removeTimestamp(String logLine) {
        return logLine.replaceFirst("^\\[.*?\\]\\s*", "");
    }

    /**
     * Spin up a pod for the build
     *
     * @param comp                    The component to build
     * @param componentBranchBuildDTO The branch to build
     * @return The name of the pod
     */
    public BuildInfo spinPodForBuild(String logPrefix, ComponentDTO comp, ComponentBranchBuildDTO componentBranchBuildDTO) throws Exception {
        // ensure scratch directory nd download  the source code
        // it return the relative path from root scratch directory setting
        log.info("{} Downloaded spin-up pod", logPrefix);
        String scratchLocation = downloadRepository(logPrefix, comp, componentBranchBuildDTO);
        log.info("{} Spinning up pod for build", logPrefix);

        // we have branch
        Pod newlyCretedPod = wrapCatch(
                () -> kubernetesRepository.spinUpBuildPod(
                        K8SPodBuilder.builder()
                                .namespace(coreBuildProperties.getK8sBuildNamespace())
                                .dockerImage(componentBranchBuildDTO.buildImageUrl())
                                .buildCommand(of("sh", "-c"))
                                .buildArgs(of("printenv; ls -la $ADBS_SOURCE;python3 /build/start_build.py"))
                                .builderName
                                        (
                                                "%s-%s-%s-%s".formatted(
                                                        UUID.randomUUID().toString().substring(0, 8),
                                                        comp.name(),
                                                        componentBranchBuildDTO.buildOs(),
                                                        componentBranchBuildDTO.branchName())

                                        )
                                .mountLocation("/mnt")
                                .label(
                                        Map.of(
                                                "cbs-build-d", componentBranchBuildDTO.id()
                                        )
                                )
                                .envVars(getVariables(comp, componentBranchBuildDTO, scratchLocation))
                                .build()
                ),
                -5
        );
        return BuildInfo.builder()
                .builderName(newlyCretedPod.getMetadata().getName())
                .scratchLocation(scratchLocation)
                .build();
    }

    /**
     * Get the variables for the build
     *
     * @param comp                    The component
     * @param componentBranchBuildDTO The branch to build
     * @param scratchLocation         The scratch location
     * @return The variables for the build
     */
    private static Map<String, String> getVariables(ComponentDTO comp, ComponentBranchBuildDTO componentBranchBuildDTO, String scratchLocation) {
        Map<String, String> customVariables = Objects.requireNonNullElse
                (
                        componentBranchBuildDTO.buildCustomVariables(),
                        new HashMap<>()
                );
        // add the default variables
        customVariables.put("ADBS_COMPONENT", comp.name());
        customVariables.put("ADBS_BRANCH", componentBranchBuildDTO.branchName());
        customVariables.put("ADBS_OS_ENVIRONMENT", componentBranchBuildDTO.buildOs().name());
        customVariables.put("ADBS_LINUX_USER", "");
        customVariables.put("ADBS_GH_USER", "");
        customVariables.put("ADBS_SOURCE", "/mnt%s".formatted(scratchLocation));
        customVariables.put("ADBS_BUILD_COMMAND", (comp.buildInstructions() == null ? "" : comp.buildInstructions()));
        return customVariables;
    }

    /**
     * Download the repository, using the real fs path
     *
     * @param logPrefix
     * @param comp                    The component
     * @param componentBranchBuildDTO The branch to build
     * @throws Exception if there is an error
     */
    private String downloadRepository(String logPrefix, ComponentDTO comp, ComponentBranchBuildDTO componentBranchBuildDTO) throws Exception {
        log.info("{} Composing scratch directory", logPrefix);
        String scratchFSDirectory = coreBuildProperties.getBuildFsRootDirectory();
        String scratchBuildFolderName = "%s/%s-%s-%s-%s/%s".formatted(
                coreBuildProperties.getBuildScratchRootDirectory(),
                comp.name(),
                componentBranchBuildDTO.branchName(),
                componentBranchBuildDTO.buildOs(),
                componentBranchBuildDTO.id(),
                comp.name());
        String sourceBuildAbsolutePath = "%s/%s".formatted(
                scratchFSDirectory,
                scratchBuildFolderName);
        Path path = Paths.get(sourceBuildAbsolutePath);
        log.info("{} Using '{}' directory to build component", logPrefix, sourceBuildAbsolutePath);
        if (Files.notExists(path)) {
            log.info("{} Directory does not exist, creating: {}", logPrefix, path);
            Files.createDirectories(path);
        } else {
            log.info("{} Directory exists, deleting contents: {}", logPrefix, path);
            deleteDirectoryAndContents(path);
            Files.createDirectories(path);
        }
        log.info("{} Downloading repository", logPrefix);
        String repositoryPath = gitServerRepository.downLoadRepository(componentMapper.toModel(comp), componentBranchBuildDTO.branchName(), path.toString());
        log.info("{} Repository downloaded to {}", logPrefix, repositoryPath);
        return repositoryPath.substring(scratchFSDirectory.length());
    }

    /**
     * Delete the contents of a directory
     *
     * @param path The path to the directory
     * @throws IOException if there is an error
     */
    private static void deleteDirectoryAndContents(Path path) throws IOException {
        if (Files.exists(path)) {
            try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path)) {
                for (Path entry : directoryStream) {
                    if (Files.isDirectory(entry)) {
                        deleteDirectoryAndContents(entry);
                    } else {
                        Files.delete(entry);
                    }
                }
            }
            Files.delete(path); // Delete the directory itself
        }
    }
}
