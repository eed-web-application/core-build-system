package edu.stanford.slac.core_build_system.api.v1.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;

@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Define new branch")
public record BranchDTO(
        @Schema(description = "ex: [fix, dev] where fix is meant to merge to main, and dev is not.")
        @NotEmpty String type,
        @Schema(description = "The unique name that identify of the branch")
        @NotEmpty String branchName,
        @Schema(description = "The unique name that identify of the branch start point")
        @NotEmpty String branchPoint
) {
}
