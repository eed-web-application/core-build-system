package edu.stanford.slac.core_build_system.api.v1.controller;

import edu.stanford.slac.ad.eed.baselib.api.v1.dto.ApiResultResponse;
import edu.stanford.slac.core_build_system.api.v1.dto.CommandTemplateDTO;
import edu.stanford.slac.core_build_system.api.v1.dto.CommandTemplateSummaryDTO;
import edu.stanford.slac.core_build_system.api.v1.dto.NewCommandTemplateDTO;
import edu.stanford.slac.core_build_system.api.v1.dto.UpdateCommandTemplateDTO;
import edu.stanford.slac.core_build_system.service.CommandTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController()
@RequestMapping("/v1/command")
@AllArgsConstructor
@Schema(description = "Api set for the component management")
public class CommandController {
    CommandTemplateService commandTemplateService;

    @PostMapping(
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    @Operation(summary = "Create a new component")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResultResponse<String> createCommand(
            @RequestBody @Valid NewCommandTemplateDTO newCommandTemplateDTO
    ) throws Exception {
        return ApiResultResponse.of(
                commandTemplateService.create(newCommandTemplateDTO)
        );
    }

    @GetMapping(
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    @Operation(summary = "List all command templates")
    @ResponseStatus(HttpStatus.OK)
    public ApiResultResponse<List<CommandTemplateSummaryDTO>> listAllCommand() throws Exception {
        return ApiResultResponse.of(
                commandTemplateService.findAll()
        );
    }

    @GetMapping(
            path = "/{id}",
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    @Operation(summary = "List all command templates")
    @ResponseStatus(HttpStatus.OK)
    public ApiResultResponse<CommandTemplateDTO> findCommandById(
            @PathVariable String id
    ) throws Exception {
        return ApiResultResponse.of(
                commandTemplateService.findById(id)
        );
    }

    @PutMapping(
            path = "/{id}",
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    @Operation(summary = "Update a command template by its unique identifier")
    @ResponseStatus(HttpStatus.OK)
    public ApiResultResponse<Boolean> updateCommandById(
            @PathVariable String id,
            @RequestBody @Valid UpdateCommandTemplateDTO  updateComponentDTO) throws Exception {
        commandTemplateService.updateById(id, updateComponentDTO);
        return ApiResultResponse.of(
                true
        );
    }

    @DeleteMapping(
            path = "/{id}",
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    @Operation(summary = "Return a command template by its unique identifier")
    @ResponseStatus(HttpStatus.OK)
    public ApiResultResponse<Boolean> deleteCommandById(
            @PathVariable String id) throws Exception {
        commandTemplateService.deleteById(id);
        return ApiResultResponse.of(
                true
        );
    }
}