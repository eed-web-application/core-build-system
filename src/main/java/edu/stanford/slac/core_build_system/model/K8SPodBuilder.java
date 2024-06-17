package edu.stanford.slac.core_build_system.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@EqualsAndHashCode
public class K8SPodBuilder {
    private List<String> buildCommand;
    private List<String> buildArgs;
    private String namespace;
    private String builderName;
    private String dockerImage;
    private String mountLocation;
    private Map<String,String> envVars;
}
