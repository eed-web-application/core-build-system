package edu.stanford.slac.core_build_system.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@EqualsAndHashCode
public class ExecutionPipeline {
    /**
     * The name of the pipeline.
     */
    String engine;
    /**
     * The architecture where the pipeline can work.
     */
    List<String> architecture;
    /**
     * The operating system where the pipeline can work.
     */
    List<String> operatingSystem;
    /**
     * The list of the commands that the pipeline will execute.
     */
    @Builder.Default
    List<String> executionCommands = new ArrayList<>();
}
