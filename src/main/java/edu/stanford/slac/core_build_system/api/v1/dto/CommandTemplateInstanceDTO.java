package edu.stanford.slac.core_build_system.api.v1.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;

import java.util.Map;
import java.util.Set;

@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Define details for create a new component")
public record CommandTemplateInstanceDTO(
        @NotEmpty
        @Schema(description = "Is the id of the command template to use")
        String id,
        @NotEmpty
        @Schema(description = "The parameter value to use in the command")
        Map<String, String> parametersValues
) {
}
