package edu.stanford.slac.core_build_system.api.v1.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Define details for create a new component")
public record NewComponentDTO(
        @Schema(description = "The name of the component")
        @NotEmpty String name,
        @Schema(description = "The description of the component")
        @NotEmpty String description,
        @Schema(description = "The organization of the component")
        String organization,
        @Schema(description = "The URL of the component [src, artifact, etc.]")
        @NotEmpty String url,
        @Schema(description = "The approval rule of the component")
        String approvalRule,
        @Schema(description = "The testing criteria of the component")
        String testingCriteria,
        @Valid Set<String> approvalIdentity,
        @Schema(description = "The list of unique identifier of the components that this component depends on.")
        @Valid Set<ComponentDependencyDTO> dependOn,
        @Schema(description = "The list of os that the component need to be build on")
        @Valid List<BuildOSDTO> buildOs,
        @Schema(description = "The isntruction for build the component")
        String buildInstructions
) {
}
