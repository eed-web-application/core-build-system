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
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.dsl.LogWatch;
import io.fabric8.kubernetes.client.dsl.PodResource;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Profile;
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
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;
import java.util.Stack;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.collect.ImmutableList.of;
import static edu.stanford.slac.ad.eed.baselib.exception.Utility.wrapCatch;
import static edu.stanford.slac.core_build_system.api.v1.dto.BuildStatusDTO.IN_PROGRESS;
import static edu.stanford.slac.core_build_system.api.v1.dto.BuildStatusDTO.PENDING;

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
     * @param documentToProcess The document to process
     */
    @Transactional
    public void processBuild(ComponentBranchBuildDTO buildToProcess) {
        ComponentDTO component = wrapCatch(
                () -> componentService.findById(buildToProcess.componentId()),
                -2
        );
        BuildStatusDTO newStatus = PENDING;
        String uniqueBuildIdentification = "[%s-%s-%s]".formatted(buildToProcess.componentId(), component.name(), buildToProcess.branchName());
        log.info("[{}] Start processing", uniqueBuildIdentification);
        try{
            BuildStatusDTO buildStatus = buildToProcess.buildStatus();
            switch (buildStatus) {
                case PENDING:
                    log.info("[{}] Build is pending", uniqueBuildIdentification);
                    BuildInfo buildInfo = spinPodForBuild(component, buildToProcess);
                    componentBuildService.updateBuildInfo(buildToProcess.id(), buildInfo);
                    // spin-up the pod
                    newStatus = IN_PROGRESS;
                    break;
                case IN_PROGRESS:
                    log.info("[{}] Build is in progress", uniqueBuildIdentification);
                    BuildStatusDTO podStatus =  getPodStatus(buildToProcess);
                    if(podStatus == IN_PROGRESS) {
                        newStatus = IN_PROGRESS;
                        log.info("[{}] Build is still in progress", uniqueBuildIdentification);
                        return;
                    } else {
                        // store the log before switching the status
                        storeLog(buildToProcess);
                        newStatus = podStatus;
                        if (podStatus == BuildStatusDTO.SUCCESS) {
                            log.info("[{}] Build is completed", uniqueBuildIdentification);
                        } else {
                            log.info("[{}] Build failed", uniqueBuildIdentification);
                        }
                    }
                    break;
                case SUCCESS:
                    log.info("[{}] Already completed", uniqueBuildIdentification);
                    break;
                case FAILED:
                    log.info("[{}] Build Already failed", uniqueBuildIdentification);
                    break;
                default:
                    log.error("[{}] Unknown build status", uniqueBuildIdentification);
                    break;
            }
        }catch (Exception e) {
            log.error("[{}] Error processing build", uniqueBuildIdentification, e);
        } finally {
            // release lock on build
            boolean lockReleased = componentBuildService.releaseLock(buildToProcess.id(), newStatus);
            if (lockReleased) {
                log.info("[{}] Lock released", uniqueBuildIdentification);
            } else {
                log.error("[{}] Lock not released", uniqueBuildIdentification);
            }
        }

    }

    /**
     * Get the status of the pod
     *
     * @param buildToProcess The build to process
     * @return The status of the pod
     */
    private BuildStatusDTO getPodStatus(ComponentBranchBuildDTO buildToProcess) {
        PodResource foundPod = kubernetesRepository.getPod(
                coreBuildProperties.getK8sBuildNamespace(),
                buildToProcess.buildInfo().builderName()
        );
        boolean terminated = foundPod.get().getStatus().getContainerStatuses().size() == 1 &&
                !foundPod.get().getStatus().getContainerStatuses().isEmpty() &&
                foundPod.get().getStatus().getContainerStatuses().getFirst().getState().getTerminated() != null;
        boolean success  =   terminated && foundPod.get().getStatus().getContainerStatuses().getFirst().getState().getTerminated().getReason().compareToIgnoreCase("Completed") == 0;
        return terminated && success ? BuildStatusDTO.SUCCESS : (terminated ? BuildStatusDTO.FAILED : IN_PROGRESS);
    }

    /**
     * Store the log of the build
     *
     * @param buildToProcess The build to process
     */
    private void storeLog(ComponentBranchBuildDTO buildToProcess) throws IOException, ParseException {
        log.info("Storing log for build {}", buildToProcess);
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
                logEntryRepository.save(entry);
            }
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
     * @param comp       The component to build
     * @param branchName The name of the branchl
     * @return The name of the pod
     */
    public BuildInfo spinPodForBuild(ComponentDTO comp, ComponentBranchBuildDTO componentBranchBuildDTO) throws Exception {
        // ensure scratch directory nd download  the source code
        // it return the relative path from root scratch directory setting
        String scratchLocation = downloadRepository(comp, componentBranchBuildDTO);

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
                                .envVars(
                                        Map.of(
                                                "ADBS_COMPONENT", comp.name(),
                                                "ADBS_BRANCH", componentBranchBuildDTO.branchName(),
                                                "ADBS_LINUX_USER", "",
                                                "ADBS_GH_USER", "",
                                                "ADBS_SOURCE", "/mnt/%s".formatted(scratchLocation),
                                                "ADBS_BUILD_COMMAND", (comp.buildInstructions()==null?"":comp.buildInstructions())
                                        )
                                )
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
     * Download the repository, using the real fs path
     *
     * @param comp       The component
     * @param branchName The branch name
     * @throws Exception if there is an error
     */
    private String downloadRepository(ComponentDTO comp, ComponentBranchBuildDTO componentBranchBuildDTO) throws Exception {
        log.info("[Scratch creation for {}/{}] Composing scratch directory", comp.name(), componentBranchBuildDTO.branchName());
        String scratchFSDirectory = coreBuildProperties.getBuildFsRootDirectory();
        String scratchBuildFolder = "%s/%s-%s-%s-%s".formatted(
                coreBuildProperties.getBuildScratchRootDirectory(),
                UUID.randomUUID().toString().substring(0, 8),
                componentBranchBuildDTO.buildOs(),
                comp.name(),
                componentBranchBuildDTO.branchName());
        String uniqueBuildDirectory = "%s/%s".formatted(
                scratchFSDirectory,
                scratchBuildFolder);
        Path path = Paths.get(uniqueBuildDirectory);
        log.info("[Scratch creation for {}/{}] Using {} directory to build component", comp.name(), componentBranchBuildDTO.branchName(), uniqueBuildDirectory);
        if (Files.notExists(path)) {
            log.info("Creating directory: {}", path);
            Files.createDirectories(path);
        } else {
            log.info("[Scratch creation for {}/{}] Directory already exists, content will be deleted: {}", comp.name(), componentBranchBuildDTO.branchName(), path);
            deleteDirectoryContents(path);
        }
        log.info("[Scratch creation for {}/{}] Downloading repository for component into {}", comp.name(), componentBranchBuildDTO.branchName(), path);
        gitServerRepository.downLoadRepository(componentMapper.toModel(comp), componentBranchBuildDTO.branchName(), path.toString());
        return scratchBuildFolder;
    }

    /**
     * Delete the contents of a directory
     *
     * @param path The path to the directory
     * @throws IOException if there is an error
     */
    private static void deleteDirectoryContents(Path path) throws IOException {
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path)) {
            for (Path entry : directoryStream) {
                if (Files.isDirectory(entry)) {
                    deleteDirectoryContents(entry);
                } else {
                    Files.delete(entry);
                }
            }
        }
    }
}
