package edu.stanford.slac.core_build_system.api.v1.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.Set;

@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Define the single component")
public record UpdateComponentDTO(
        @Schema(description = "The name of the component")
        String name,
        @Schema(description = "The description of the component")
        String description,
        @Schema(description = "The URL of the component [src, artifact, etc.]")
        String url,
        @Schema(description = "The list of unique identifier of the components that this component depends on.")
        Set<String> dependOnComponentIds,
        @Schema(description = "The list command template instances to execute to build the component.")
        Set<CommandTemplateInstanceDTO> commandTemplatesInstances
) {
}