package edu.stanford.slac.core_build_system.api.v1.controller;

import edu.stanford.slac.ad.eed.baselib.api.v1.dto.ApiResultResponse;
import edu.stanford.slac.core_build_system.repository.CommandTemplateRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Profile("debug")
@RestController()
@RequestMapping("/v1/debug")
@AllArgsConstructor
@Schema(description = "Utility api when debugging or create client application")
public class DebuggerUtilityController {
    private final CommandTemplateRepository commandTemplateRepository;
    private final CommandTemplateRepository componentRepository;
    @PostMapping(
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    @Operation(summary = "Reset all the data")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResultResponse<Boolean> deleteAll() throws Exception {
        componentRepository.deleteAll();
        commandTemplateRepository.deleteAll();
        return ApiResultResponse.of(
                true
        );
    }
}
