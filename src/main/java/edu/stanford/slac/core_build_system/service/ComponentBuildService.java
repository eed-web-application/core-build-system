package edu.stanford.slac.core_build_system.service;

import edu.stanford.slac.ad.eed.baselib.exception.ControllerLogicException;
import edu.stanford.slac.core_build_system.api.v1.dto.BuildStatusDTO;
import edu.stanford.slac.core_build_system.api.v1.dto.ComponentBranchBuildDTO;
import edu.stanford.slac.core_build_system.api.v1.dto.LogEntryDTO;
import edu.stanford.slac.core_build_system.api.v1.mapper.ComponentBranchBuildMapper;
import edu.stanford.slac.core_build_system.api.v1.mapper.LogEntryMapper;
import edu.stanford.slac.core_build_system.config.CoreBuildProperties;
import edu.stanford.slac.core_build_system.exception.BranchNotFound;
import edu.stanford.slac.core_build_system.exception.BuildNotFound;
import edu.stanford.slac.core_build_system.exception.ComponentNotFoundByName;
import edu.stanford.slac.core_build_system.model.Branch;
import edu.stanford.slac.core_build_system.model.Component;
import edu.stanford.slac.core_build_system.model.ComponentBranchBuild;
import edu.stanford.slac.core_build_system.repository.*;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static edu.stanford.slac.ad.eed.baselib.exception.Utility.assertion;
import static edu.stanford.slac.ad.eed.baselib.exception.Utility.wrapCatch;

@Log4j2
@Service
@AllArgsConstructor
public class ComponentBuildService {
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
    public String startBuild(String componentName, String branchName) {
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
        // check that the component has a repository URL
        assertion(
                ControllerLogicException.builder().build(),
                () -> comp.getUrl() != null && !comp.getUrl().isBlank()
        );
        // check if branch is present
        Branch branch = comp.getBranches().stream().filter(b -> b.getBranchName().compareToIgnoreCase(branchName) == 0)
                .findAny()
                .orElseThrow(
                        () -> BranchNotFound.byName()
                                .errorCode(-3)
                                .branchName(branchName)
                                .build()
                );

        // save the build
        var savedBuild = wrapCatch(
                () -> componentBranchBuildRepository.save(
                        ComponentBranchBuild.builder()
                                .componentId(comp.getId())
                                .branchName(branchName)
                                .build()
                ),
                -4
        );
        return savedBuild.getId();
    }

    /**
     * Update the builder name
     *
     * @param id          The identifier of the build
     * @param builderName The new name of the builder
     * @return true if the update was successful
     */
    public boolean updateBuilderName(String id, String builderName) {
        // update the builder name
        return wrapCatch(
                () -> componentBranchBuildRepository.updateBuilderName(
                        id,
                        builderName
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

    public Boolean releaseLock(String id) {
        return wrapCatch(
                () -> componentBranchBuildRepository.releaseLock(id),
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
        return  foundLogs.stream()
                .map(logEntryMapper::toDTO)
                .toList();
    }
}
