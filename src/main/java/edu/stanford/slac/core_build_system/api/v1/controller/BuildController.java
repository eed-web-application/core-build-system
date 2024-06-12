package edu.stanford.slac.core_build_system.api.v1.controller;

import edu.stanford.slac.ad.eed.baselib.api.v1.dto.ApiResultResponse;
import edu.stanford.slac.core_build_system.api.v1.dto.ComponentBranchBuildDTO;
import edu.stanford.slac.core_build_system.api.v1.dto.ComponentBranchBuildSummaryDTO;
import edu.stanford.slac.core_build_system.api.v1.dto.LogEntryDTO;
import edu.stanford.slac.core_build_system.service.ComponentBuildService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController()
@RequestMapping("/v1/build")
@AllArgsConstructor
@Schema(description = "Api set for the component management")
public class BuildController {
    ComponentBuildService componentBuildService;

    @PostMapping(
            path = "/component/{componentName}/branch/{branchName}",
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    @Operation(
            summary = "Start a new build",
            description = "Start a new build for a component/branch and return the IDs of the started builds"
    )
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResultResponse<List<String>> createNewBuild(
            @PathVariable @NotEmpty String componentName,
            @PathVariable @NotEmpty String branchName
    ) {
        return ApiResultResponse.of(
                componentBuildService.startBuild(componentName, branchName)
        );
    }

    @GetMapping(
            path = "/component/{componentName}/branch/{branchName}",
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    @Operation(
            summary = "Return all builds for a component/branch"
    )
    @ResponseStatus(HttpStatus.OK)
    public ApiResultResponse<List<ComponentBranchBuildSummaryDTO>> findByComponentNameAndBranch(
            @PathVariable @NotEmpty String componentName,
            @PathVariable @NotEmpty String branchName
    ) {
        return ApiResultResponse.of(
                componentBuildService.findAllByComponentNameBranchName(componentName, branchName)
        );
    }

    @GetMapping(
            path = "/{buildId}",
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    @Operation(
            summary = "Find a build by his id"
    )
    @ResponseStatus(HttpStatus.OK)
    public ApiResultResponse<ComponentBranchBuildDTO> findBuildById(
            @PathVariable @NotEmpty String buildId
    ) throws Exception {

        return ApiResultResponse.of(componentBuildService.findBuildById(buildId));
    }

    @DeleteMapping(
            path = "/{buildId}",
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    @Operation(
            summary = "Delete a build by his id"
    )
    @ResponseStatus(HttpStatus.OK)
    public ApiResultResponse<Boolean> deleteComponentById(
            @PathVariable @NotEmpty String buildId
    ) throws Exception {
        componentBuildService.deleteBuild(buildId);
        return ApiResultResponse.of(true);
    }

    @GetMapping(
            path = "/{buildId}/log",
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    @Operation(
            summary = "Log for a build"
    )
    @ResponseStatus(HttpStatus.OK)
    public ApiResultResponse<List<LogEntryDTO>> findLogByBuildId(
            @PathVariable @NotEmpty String buildId
    ) {
        return ApiResultResponse.of(
                componentBuildService.findLogForBuild(buildId)
        );
    }
}
