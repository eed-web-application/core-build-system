package edu.stanford.slac.core_build_system.api.v1.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;

import java.util.Set;

@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Define details for create a new component")
public record NewCommandTemplateDTO(
        @NotEmpty
        @Schema(description = "The name of the component")
        String name,
        @NotEmpty
        @Schema(description = "The description of the component")
        String description,
        @NotEmpty
        @Schema(description = "The parameter used by the command")
        Set<CommandTemplateParameterDTO> parameters,
        @NotEmpty
        @Schema(description = "Those are the list of the action that the command will take")
        Set<String> commandExecutionsLayers
) {
}
