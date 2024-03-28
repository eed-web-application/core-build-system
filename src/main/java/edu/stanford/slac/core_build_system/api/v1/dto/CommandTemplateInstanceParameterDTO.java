package edu.stanford.slac.core_build_system.api.v1.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Define an instance of the command template with the parameters values")
public record CommandTemplateInstanceParameterDTO(
        @Schema(description = "The name of the parameter")
        String name,
        @Schema(description = "The values ")
        String value
) {
}
