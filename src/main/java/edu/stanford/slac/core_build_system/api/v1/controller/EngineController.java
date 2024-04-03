package edu.stanford.slac.core_build_system.api.v1.controller;

import edu.stanford.slac.ad.eed.baselib.api.v1.dto.ApiResultResponse;
import edu.stanford.slac.core_build_system.api.v1.dto.FileResourceDTO;
import edu.stanford.slac.core_build_system.service.ComponentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController()
@RequestMapping("/v1/engine")
@AllArgsConstructor
@Schema(description = "Api set for the generation of artifact by the respective engines")
public class EngineController {
    private final ComponentService componentService;
    @GetMapping(
            path = "/generate",
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    @Operation(summary = "Generate component artifact using a specific engine")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Resource> generateComponentArtifact(
            @Parameter(description = "IS the engine to use represented by his name", required = true)
            @RequestParam String engineName,
            @Parameter(description = "Is the list of the component id for wich the artifact should be generated", required = true)
            @RequestParam String[] componentId,
            @Parameter(description = "is the build specs to use for the generation of the artifact", required = false, example = "{\"buildSpecs\": \"value\"}")
            @RequestParam Map<String,String> allRequestParams
    ) throws Exception {
        Map<String, String> buildSpecs = new HashMap<>(allRequestParams);
        buildSpecs.remove("engineName");
        buildSpecs.remove("componentId");
        FileResourceDTO content = componentService.createArtifactByEngineNameAndComponentList(engineName, List.of(componentId), buildSpecs);

        // Set the headers and content disposition (e.g., attachment; filename="filename.txt")
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=%s".formatted(content.fileName()));

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(content.length())
                .contentType(MediaType.TEXT_PLAIN) // Adjust the content type as necessary
                .body(new InputStreamResource(content.fileStream()));
    }

    @GetMapping(
            path = "/all",
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    @Operation(summary = "Return all engine name")
    @ResponseStatus(HttpStatus.OK)
    public ApiResultResponse<Set<String>> findAllEngineNames() throws Exception {
        return ApiResultResponse.of(
                componentService.getEngineNames()
        );
    }
}
