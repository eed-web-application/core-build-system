package edu.stanford.slac.core_build_system.api.v1.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import edu.stanford.slac.core_build_system.model.BuildOS;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Define details for the build of a component branch")
public record ComponentBranchBuildSummaryDTO(
        @Schema(description = "The identifier of the build")
        String id,
        @Schema(description = "The identifier of the build image that is used to perform this build")
        BuildOS buildOs,
        @Schema(description = "The status of the build")
        BuildStatusDTO buildStatus
) {
}
