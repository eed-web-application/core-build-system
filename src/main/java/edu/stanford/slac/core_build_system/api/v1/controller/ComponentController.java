package edu.stanford.slac.core_build_system.api.v1.controller;

import edu.stanford.slac.ad.eed.baselib.api.v1.dto.ApiResultResponse;
import edu.stanford.slac.core_build_system.api.v1.dto.*;
import edu.stanford.slac.core_build_system.service.ComponentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController()
@RequestMapping("/v1/component")
@AllArgsConstructor
@Schema(description = "Api set for the component management")
public class ComponentController {
    ComponentService componentService;

    @PostMapping(
            produces = {MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE}
    )
    @Operation(summary = "Create a new component")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResultResponse<String> createNewComponent(
            @RequestBody @Valid NewComponentDTO newComponentDTO
    ) throws Exception {
        return ApiResultResponse.of(
               componentService.create(newComponentDTO)
        );
    }

    @GetMapping(
            path = "/{id}",
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    @Operation(summary = "Find a component by an id")
    @ResponseStatus(HttpStatus.OK)
    public ApiResultResponse<ComponentDTO> findComponentById(
           @PathVariable @NotNull String id
    ) throws Exception {
        return ApiResultResponse.of(
                componentService.findById(id)
        );
    }

    @DeleteMapping(
            path = "/{id}",
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    @Operation(summary = "Delete a component by his id")
    @ResponseStatus(HttpStatus.OK)
    public ApiResultResponse<Boolean> deleteComponentById(
            @PathVariable @NotNull String id
    ) throws Exception {
        componentService.deleteById(id);
        return ApiResultResponse.of(true);
    }

    @PutMapping(
            path = "/{id}",
            produces = {MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE}
    )
    @Operation(summary = "Delete a component by his id")
    @ResponseStatus(HttpStatus.OK)
    public ApiResultResponse<Boolean> updateComponentById(
            @PathVariable @NotNull String id,
            @RequestBody @Valid UpdateComponentDTO updateComponentDTO
    ) throws Exception {
        componentService.updateById(id, updateComponentDTO);
        return ApiResultResponse.of(true);
    }

    @GetMapping(
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    @Operation(summary = "List all components")
    @ResponseStatus(HttpStatus.OK)
    public ApiResultResponse<List<ComponentSummaryDTO>> listAllComponent() throws Exception {
        return ApiResultResponse.of(
                componentService.findAll()
        );
    }

    @PutMapping(
            path = "/{componentName}/version",
            produces = {MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE}
    )
    @Operation(summary = "Create new version of a component")
    @ResponseStatus(HttpStatus.OK)
    public ApiResultResponse<Boolean> addNewVersion(
            @PathVariable @NotEmpty String componentName,
            @RequestBody @Valid NewVersionDTO newVersionDTO
    ) {
        return ApiResultResponse.of(componentService.addNewVersion(componentName, newVersionDTO));
    }

    @PutMapping(
            path = "/{componentName}/branch",
            produces = {MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE}
    )
    @Operation(summary = "Create new branch of a component")
    @ResponseStatus(HttpStatus.OK)
    public ApiResultResponse<Boolean> addBranch(
            @PathVariable @NotEmpty String componentName,
            @RequestBody @Valid BranchDTO branchDTO
    ) {
        return ApiResultResponse.of(componentService.addNewBranch(componentName, branchDTO));
    }

    @PutMapping(
            path = "/{componentName}/event/{enable}",
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    @Operation(summary = "Enable or disable event on component, event can be push, pull_request, ping and will be triggered directly by github or other provider")
    @ResponseStatus(HttpStatus.OK)
    public ApiResultResponse<Boolean> eventManagement(
            @PathVariable @NotEmpty String componentName,
            @PathVariable Boolean enable
    ) {
        componentService.enableEvent(componentName, enable);
        return ApiResultResponse.of(true);
    }
}