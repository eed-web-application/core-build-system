package edu.stanford.slac.core_build_system.api.v1.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder(toBuilder = true)
@Schema(description = "Define the build information")
public record BuildInfoDTO (
    @Schema(description = "The name of the builder")
    String builderName,
    @Schema(description = "The path where the soruce code are stored")
    String scratchLocation
){}
