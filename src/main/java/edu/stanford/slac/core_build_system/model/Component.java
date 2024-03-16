package edu.stanford.slac.core_build_system.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@EqualsAndHashCode
public class Component {
    private String id;
    private String name;
    private Set<String> dependOnComponentIds;
    private String url;
    private String version;
    private Set<String> buildCommandTemplateIds;
}
