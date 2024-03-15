package edu.stanford.slac.core_build_system.api.v1.controller;

import edu.stanford.slac.ad.eed.baselib.api.v1.dto.ApiResultResponse;
import edu.stanford.slac.core_build_system.api.v1.dto.ComponentDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController()
@RequestMapping("/v1/component")
@AllArgsConstructor
@Schema(description = "Api set for media management")
public class ComponentController {
    @GetMapping(
            path = "/world",
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    @Operation(summary = "Download a content from a file entry")
    public ApiResultResponse<List<ComponentDTO>> listAllComponent() throws Exception {
        return ApiResultResponse.of(
                List.of(
                        ComponentDTO.builder()
                                .id("1")
                                .name("component 1")
                                .build(),
                        ComponentDTO.builder()
                                .id("2")
                                .name("component 2")
                                .build(),
                        ComponentDTO.builder()
                                .id("3")
                                .name("component 3")
                                .build()
                )
        );
    }
}