package edu.stanford.slac.core_build_system.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@EqualsAndHashCode
public class DeploymentHost {
    private String id;
    private String name;
    private String location;
    private String owner;
    private String status;
    private List<String> specialHardware;
}
