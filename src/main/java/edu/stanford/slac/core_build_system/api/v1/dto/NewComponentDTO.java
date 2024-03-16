package edu.stanford.slac.core_build_system.api.v1.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;

import java.util.Set;

@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Define details for create a new component")
public record NewComponentDTO(
        @NotEmpty
        @Schema(description = "The name of the component")
        String name,
        @NotEmpty
        @Schema(description = "The version of the component")
        String version,
        @NotEmpty
        @Schema(description = "The URL of the component [src, artifact, etc.]")
        String url,
        @Schema(description = "The list of unique identifier of the components that this component depends on.")
        Set<String> dependOnComponentIds,
        @NotEmpty
        @Schema(description = "The list of unique identifier of the command templates that this component uses to build.")
        Set<String> buildCommandTemplateIds
) {
}
