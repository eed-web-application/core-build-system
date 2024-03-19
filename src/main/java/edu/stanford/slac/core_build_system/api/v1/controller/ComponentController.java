package edu.stanford.slac.core_build_system.api.v1.controller;

import edu.stanford.slac.ad.eed.baselib.api.v1.dto.ApiResultResponse;
import edu.stanford.slac.core_build_system.api.v1.dto.ComponentDTO;
import edu.stanford.slac.core_build_system.api.v1.dto.NewComponentDTO;
import edu.stanford.slac.core_build_system.service.ComponentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
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
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    @Operation(summary = "Create a new component")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResultResponse<String> create(
            @RequestBody @Valid NewComponentDTO newComponentDTO
    ) throws Exception {
        return ApiResultResponse.of(
               componentService.create(newComponentDTO)
        );
    }

    @GetMapping(
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    @Operation(summary = "List all components")
    @ResponseStatus(HttpStatus.OK)
    public ApiResultResponse<List<ComponentDTO>> listAllComponent() throws Exception {
        return ApiResultResponse.of(
                componentService.findAll()
        );
    }
}