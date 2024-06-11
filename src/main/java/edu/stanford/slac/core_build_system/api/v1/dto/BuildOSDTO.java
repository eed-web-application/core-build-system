package edu.stanford.slac.core_build_system.api.v1.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Enum representing the OS of the build
 */
@Schema(description = "Enum representing the OS image for execute the build")
public enum BuildOSDTO {
    UBUNTU,
    RHEL7,
    RHEL6,
    RHEL5,
}
