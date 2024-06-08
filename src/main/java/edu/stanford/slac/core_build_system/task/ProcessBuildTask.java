package edu.stanford.slac.core_build_system.task;

import edu.stanford.slac.core_build_system.api.v1.dto.BuildStatusDTO;
import edu.stanford.slac.core_build_system.api.v1.dto.ComponentBranchBuildDTO;
import edu.stanford.slac.core_build_system.api.v1.dto.ComponentDTO;
import edu.stanford.slac.core_build_system.service.ComponentBuildService;
import edu.stanford.slac.core_build_system.service.ComponentService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static edu.stanford.slac.ad.eed.baselib.exception.Utility.wrapCatch;

@Log4j2
@Component
@AllArgsConstructor
@Profile("async-build-processing")
public class ProcessBuildTask {
    private final ComponentService componentService;
    private final ComponentBuildService componentBuildService;

    @Scheduled(fixedDelay = 5000)  // Runs every 5 seconds
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
    private void processBuild(ComponentBranchBuildDTO documentToProcess) {
        ComponentDTO component = wrapCatch(
                () -> componentService.findById(documentToProcess.componentId()),
                -2
        );
        String uniqueBuildIdentification = "[%s-%s-%s]".formatted(documentToProcess.componentId(), component.name(), documentToProcess.branchName());
        log.info("[{}] Start processing", uniqueBuildIdentification);

        switch(documentToProcess.buildStatus()) {
            case PENDING:
                log.info("[{}] Build is pending", uniqueBuildIdentification);
                componentBuildService.updateStatus(documentToProcess.id(), BuildStatusDTO.IN_PROGRESS);
                break;
            case IN_PROGRESS:
                log.info("[{}] Build is in progress", uniqueBuildIdentification);
                // execute the pod
                componentBuildService.updateStatus(documentToProcess.id(), BuildStatusDTO.SUCCESS);
                return;
            case SUCCESS:
                log.info("[{}] Build is completed", uniqueBuildIdentification);
                return;
            case FAILED:
                log.info("[{}] Build failed", uniqueBuildIdentification);
                break;
            default:
                log.error("[{}] Unknown build status", uniqueBuildIdentification);
                return;
        }


        // release lock on build
        boolean lockReleased = componentBuildService.releaseLock(documentToProcess.id());
        if (lockReleased) {
            log.info("[{}] Lock released", uniqueBuildIdentification);
        } else {
            log.error("[{}] Lock not released", uniqueBuildIdentification);
        }
    }
}
