package edu.stanford.slac.core_build_system.api.v1.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Define details for the build of a component branch")
public record ComponentBranchBuildDTO(
        @Schema(description = "The identifier of the build")
        String id,
        @Schema(description = "The identifier of the component")
        String componentId,
        @Schema(description = "The name of the branch that is used to perform this build")
        String branchName,
        @Schema(description = "The name of the pod builder that is used to perform this build")
        String builderName,
        @Schema(description = "The status of the build")
        BuildStatusDTO buildStatus,
        @Schema(description = "The date and time when the build was started")
        LocalDateTime lastProcessTime,
        @Schema(description = "The date and time when the activity was created")
        LocalDateTime createdDate,
        @Schema(description = "The user who created the activity")
        String createdBy,
        @Schema(description = "The date and time when the activity was last modified")
        LocalDateTime lastModifiedDate,
        @Schema(description = "The user who last modified the activity")
        String lastModifiedBy,
        @Schema(description = "The version of the activity")
        Long version
) {
}
