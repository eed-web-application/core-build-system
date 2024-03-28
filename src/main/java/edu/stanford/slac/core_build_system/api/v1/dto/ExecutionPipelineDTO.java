package edu.stanford.slac.core_build_system.api.v1.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Define details for create a new execution pipeline for a command")
public record ExecutionPipelineDTO (
    @Schema(description = "The name of the pipeline")
    String engine,
    @Schema(description = "The architecture where the pipeline can work")
    List<String> architecture,
    @Schema(description = "The operating system where the pipeline can work")
    List<String> operatingSystem,
    @Schema(description = "The list of the commands that the pipeline will execute")
    List<String> executionCommands){}
