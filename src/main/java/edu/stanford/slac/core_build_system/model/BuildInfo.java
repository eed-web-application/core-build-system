package edu.stanford.slac.core_build_system.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BuildInfo {
    private String builderName;
    private String scratchLocation;
}
