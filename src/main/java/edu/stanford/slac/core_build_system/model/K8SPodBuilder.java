package edu.stanford.slac.core_build_system.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@AllArgsConstructor
@EqualsAndHashCode
public class K8SPodBuilder {
    private String namespace;
    private String builderName;
    private String dockerImage;
    private String mountLocation;
    private String buildLocation;
}
