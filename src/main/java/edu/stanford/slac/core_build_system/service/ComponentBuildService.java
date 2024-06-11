package edu.stanford.slac.core_build_system.service;

import edu.stanford.slac.core_build_system.api.v1.dto.BuildStatusDTO;
import edu.stanford.slac.core_build_system.api.v1.dto.ComponentBranchBuildDTO;
import edu.stanford.slac.core_build_system.api.v1.dto.LogEntryDTO;
import edu.stanford.slac.core_build_system.api.v1.mapper.ComponentBranchBuildMapper;
import edu.stanford.slac.core_build_system.api.v1.mapper.LogEntryMapper;
import edu.stanford.slac.core_build_system.config.CoreBuildProperties;
import edu.stanford.slac.core_build_system.exception.*;
import edu.stanford.slac.core_build_system.model.*;
import edu.stanford.slac.core_build_system.repository.*;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static edu.stanford.slac.ad.eed.baselib.exception.Utility.assertion;
import static edu.stanford.slac.ad.eed.baselib.exception.Utility.wrapCatch;
import static edu.stanford.slac.core_build_system.utility.RetryableOperationUtility.retry;

@Log4j2
@Service
@AllArgsConstructor
public class ComponentBuildService {
    private final BuildImageRepository buildImageRepository;
    private final LogEntryMapper logEntryMapper;
    private final LogEntryRepository logEntryRepository;
    private final KubernetesRepository kubernetesRepository;
    private final CoreBuildProperties coreBuildProperties;
    private final GitServerRepository gitServerRepository;
    private final ComponentRepository componentRepository;
    private final ComponentBranchBuildRepository componentBranchBuildRepository;
    private final ComponentBranchBuildMapper componentBranchBuildMapper;

    /**
     * Start a new build for a component/branch
     *
     * @param componentName The name of the component
     * @param branchName    The name of the branch
     */
    @Transactional
    public List<String> startBuild(String componentName, String branchName) {
        List<String> buildIds = new ArrayList<>();
        Component comp = wrapCatch(
                () -> componentRepository.findByName(componentName)
                        .orElseThrow(
                                () ->
                                        ComponentNotFoundByName.byName()
                                                .errorCode(-1)
                                                .name(componentName)
                                                .build()

                        ),
                -2
        );
        log.info("[StartBuild {}/{}] check if component has an url", componentName, branchName);
        // check that the component has a repository URL
        assertion(
                RepositoryNotFound.byName()
                        .errorCode(-2)
                        .name(componentName)
                        .build(),
                () -> comp.getUrl() != null && !comp.getUrl().isBlank()
        );
        log.info("[StartBuild {}/{}] check if component has an build os configured", componentName, branchName);
        // check the build os list
        assertion(
                BuildOSMissing.byName()
                        .errorCode(-3)
                        .name(componentName)
                        .build(),
                () -> comp.getBuildOs() != null && !comp.getBuildOs().isEmpty()
        );
        log.info("[StartBuild {}/{}] check if the branch is present", componentName, branchName);
        // check if branch is present
        Branch branch = comp.getBranches().stream().filter(b -> b.getBranchName().compareToIgnoreCase(branchName) == 0)
                .findAny()
                .orElseThrow(
                        () -> BranchNotFound.byName()
                                .errorCode(-4)
                                .branchName(branchName)
                                .build()
                );

        // fetch image and create hashmap with build Os and image url
        Map<BuildOS, String> osBuildImageUrl = buildImageRepository.findAll().stream()
                .filter(bi -> comp.getBuildOs().contains(bi.getOs()))
                .collect(
                        Collectors.toMap(
                                BuildImage::getOs,
                                BuildImage::getDockerImageUrl
                        )
                );

        for (var entry : osBuildImageUrl.entrySet()) {
            log.info("[StartBuild {}/{}] using image {}", componentName, branchName, entry.getValue());
            // save the build
            var savedBuild = wrapCatch(
                    () -> componentBranchBuildRepository.save(
                            ComponentBranchBuild.builder()
                                    .componentId(comp.getId())
                                    .branchName(branchName)
                                    .buildOs(entry.getKey())
                                    .buildImageUrl(entry.getValue())
                                    .build()
                    ),
                    -4
            );
            log.info("[StartBuild {}/{}] build submitted with id: {}", componentName, branchName, savedBuild.getId());
            buildIds.add(savedBuild.getId());
        }
        return buildIds;
    }

    /**
     * Delete a build
     *
     * @param buildId The identifier of the build
     */
    @Transactional
    public void deleteBuild(String buildId) {
        log.info("Deleting build {}", buildId);
        ComponentBranchBuild cbb = wrapCatch(
                () -> componentBranchBuildRepository.findById(buildId)
                        .orElseThrow(
                                () -> BuildNotFound.byId().id(buildId).errorCode(-1).build()
                        ),
                -1
        );
        /// delete the log entries
        log.info("Deleting log entries for build {}", buildId);
        wrapCatch(
                () -> retry(() -> {
                    logEntryRepository.deleteAllByBuildId(buildId);
                    return null;
                }, 3), // Retry up to 3 times
                -1
        );
        // delete the build
        log.info("Deleting build {}", buildId);
        wrapCatch(
                () -> retry(() -> {
                    componentBranchBuildRepository.deleteById(buildId);
                    return null;
                }, 3), // Retry up to 3 times
                -3
        );

        // delete pod associated with the build
        log.info("Deleting pod for build {}", buildId);
        wrapCatch(
                () -> retry(() -> {
                    kubernetesRepository.deletePod(
                            coreBuildProperties.getK8sBuildNamespace(),
                            cbb.getBuildInfo().getBuilderName()
                    );
                    return null;
                }, 3), // Retry up to 3 times
                -2
        );
    }

    /**
     * Update the builder name
     *
     * @param id          The identifier of the build
     * @param builderName The new name of the builder
     * @return true if the update was successful
     */
    public boolean updateBuildInfo(String id, BuildInfo buildInfo) {
        // update the builder name
        return wrapCatch(
                () -> componentBranchBuildRepository.updateBuildInfo(
                        id,
                        buildInfo
                ),
                -1
        );
    }

    /**
     * Update the status of a build
     *
     * @param id     The identifier of the build
     * @param status The new status of the build
     */
    public void updateStatus(String id, BuildStatusDTO status) {
        ComponentBranchBuild cbb = wrapCatch(
                () -> componentBranchBuildRepository.findById(id)
                        .orElseThrow(
                                () -> BuildNotFound.byId().id(id).errorCode(-1).build()
                        ),
                -1
        );
        cbb.setBuildStatus(componentBranchBuildMapper.toModel(status));
        wrapCatch(
                () -> componentBranchBuildRepository.save(cbb),
                -2
        );
    }

    /**
     * Get the next build to process
     *
     * @return The next build to process
     */
    public Optional<ComponentBranchBuildDTO> getNextBuildToProcess() {
        return wrapCatch(
                () -> componentBranchBuildRepository.findAndLockNextDocument(Instant.now().minus(1, ChronoUnit.MINUTES)),
                -1
        ).map(componentBranchBuildMapper::toDTO);
    }

    public Boolean releaseLock(String id, BuildStatusDTO newStatus) {
        return wrapCatch(
                () -> componentBranchBuildRepository.releaseLock(id, componentBranchBuildMapper.toModel(newStatus)),
                -1
        );
    }

    /**
     * Find a build by its identifier
     *
     * @param buildId The identifier of the build
     * @return The build
     */
    public ComponentBranchBuildDTO findBuildById(String buildId) {
        return wrapCatch(
                () -> componentBranchBuildRepository.findById(buildId)
                        .map(componentBranchBuildMapper::toDTO)
                        .orElseThrow(
                                () -> BuildNotFound.byId().id(buildId).errorCode(-1).build()
                        ),
                -2
        );
    }

    /**
     * Get the log for a build
     *
     * @param buildId The identifier of the build
     * @return The log for the build
     */
    public List<LogEntryDTO> findLogForBuild(String buildId) {
        var foundLogs = wrapCatch(
                () -> logEntryRepository.findByBuildId(buildId),
                -1
        );
        return foundLogs.stream()
                .map(logEntryMapper::toDTO)
                .toList();
    }

    /**
     * Find all builds for a component
     *
     * @param componentName The name of the component
     * @return The list of builds
     */
    public List<ComponentBranchBuildDTO> findAllByComponentNameBranchName(String componentName, String branchName) {
        Component component = wrapCatch(
                () -> componentRepository.findByName(componentName)
                        .orElseThrow(
                                () -> ComponentNotFoundByName.byName().name(componentName).errorCode(-1).build()
                        ),
                -1
        );
        return componentBranchBuildRepository.findByComponentIdAndBranchName(component.getId(), branchName)
                .stream()
                .map(componentBranchBuildMapper::toDTO)
                .toList();
    }
}
