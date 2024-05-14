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
@Schema(description = "Define the single component")
public record ComponentSummaryDTO(
        @Schema(description = "The unique identifier of the component")
        String id,
        @Schema(description = "The name of the component")
        String name,
        @Schema(description = "The URL of the component [src, artifact, etc.]")
        String url,
        @Schema(description = "The description of the component")
        String description
) {
}
