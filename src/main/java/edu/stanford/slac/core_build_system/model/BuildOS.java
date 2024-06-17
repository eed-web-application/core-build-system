package edu.stanford.slac.core_build_system.model;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Enum representing the OS of the build
 */
@Schema(description = "Enum representing the OS image for execute the build")
public enum BuildOS {
    UBUNTU,
    RHEL8,
    RHEL7,
    RHEL6,
    RHEL5,
    ROCKY9,
}
