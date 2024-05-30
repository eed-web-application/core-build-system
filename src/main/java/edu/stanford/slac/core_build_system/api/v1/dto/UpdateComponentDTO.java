package edu.stanford.slac.core_build_system.api.v1.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;
import java.util.Set;

@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Define the single component")
public record UpdateComponentDTO(
        @Schema(description = "The unique name that identify of the component")
        @NotNull String name,
        @Schema(description = "The description of the component")
        @NotEmpty String description,
        @Schema(description = "The organization of the component")
        @NotEmpty String organization,
        @Schema(description = "The URL of the component [src, artifact, etc.]")
        @NotEmpty String url,
        @Schema(description = "The approval rule of the component")
        @NotEmpty String approvalRule,
        @Schema(description = "The testing criteria of the component")
        @NotEmpty String testingCriteria,
        @NotEmpty Set<String> approvalIdentity,
        @Schema(description = "The list of unique identifier of the components that this component depends on.")
        Set<ComponentDependencyDTO> dependOn
) {
}
