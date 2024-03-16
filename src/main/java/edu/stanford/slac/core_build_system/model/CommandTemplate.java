package edu.stanford.slac.core_build_system.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@AllArgsConstructor
@EqualsAndHashCode
public class CommandTemplate {
    private String id;
    private String name;
    private String command;
}
