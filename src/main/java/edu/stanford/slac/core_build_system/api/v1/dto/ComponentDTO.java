package edu.stanford.slac.core_build_system.api.v1.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import edu.stanford.slac.core_build_system.model.Version;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Define the single component")
public record ComponentDTO(
        @Schema(description = "The unique identifier of the component")
        String id,
        @Schema(description = "The name of the component")
        String name,
        @Schema(description = "The description of the component")
        String description,
        @Schema(description = "The organization of the component")
        String organization,
        @Schema(description = "The URL of the component [src, artifact, etc.]")
        String url,
        @Schema(description = "The approval rule of the component")
        String approvalRule,
        @Schema(description = "The testing criteria of the component")
        String testingCriteria,
        @Schema(description = "The build command of the component")
        String buildInstructions,
        Set<String> approvalIdentity,
        @Schema(description = "The list of unique identifier of the components that this component depends on.")
        Set<ComponentDependencyDTO> dependOn,
        @Schema(description = "The list of os that the component need to be build on")
        List<BuildOSDTO> buildOs,
        @Schema(description = "The token for authorize action on the component")
        String componentToken,
        @Schema(description = "The list of versions of the component")
        List<VersionDTO> versions,
        @Schema(description = "The list of branches of the component")
        List<BranchDTO> branches,
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        @JsonSerialize(using = LocalDateTimeSerializer.class)
        @Schema(description = "The date and time when the activity was created")
        LocalDateTime createdDate,
        @Schema(description = "The user who created the activity")
        String createdBy,
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        @JsonSerialize(using = LocalDateTimeSerializer.class)
        @Schema(description = "The date and time when the activity was last modified")
        LocalDateTime lastModifiedDate,
        @Schema(description = "The user who last modified the activity")
        String lastModifiedBy,
        @Schema(description = "The version of the activity")
        Long version
        ) {
}
