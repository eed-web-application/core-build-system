package edu.stanford.slac.core_build_system.model;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.Id;

/**
 * Represents a build image
 */
@Data
@Builder
@ToString
public class BuildImage {
    @Id
    private String id;
    private BuildOS os;
    private String dockerImageUrl;
}
