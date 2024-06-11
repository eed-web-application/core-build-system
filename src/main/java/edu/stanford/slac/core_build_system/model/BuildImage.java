package edu.stanford.slac.core_build_system.model;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

/**
 * Represents a build image
 */
@Data
@Builder
@ToString
public class BuildImage {
    private BuildOS os;
    private String dockerImageUrl;
}
