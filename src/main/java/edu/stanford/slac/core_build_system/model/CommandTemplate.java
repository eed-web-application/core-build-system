package edu.stanford.slac.core_build_system.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.Set;
/**
 * This class represents a command template.
 * a command template is a command that performs a specific task.
 * for example copy/move, etc....
 */
@Data
@Builder
@AllArgsConstructor
@EqualsAndHashCode
public class CommandTemplate {
    private String id;
    private String name;
    private String description;
    private Set<String> dependOnComponents;
    private Set<CommandTemplateParameter> parameters;
    /**
     * The list of the action that the command will take.
     */
    private Set<ExecutionPipeline> commandExecutionsLayers;
    /**
     * The date and time when the activity was created.
     * This field is automatically populated with the creation date and time, using @CreatedDate annotation.
     */
    @CreatedDate
    private LocalDateTime createdDate;
    /**
     * The identifier of the user who created the activity.
     * This field stores the ID of the user who initially created the activity, using @CreatedBy annotation.
     */
    @CreatedBy
    private String createdBy;
    /**
     * The date and time when the activity was last modified.
     * This field is automatically updated with the date and time of the last modification, using @LastModifiedDate annotation.
     */
    @LastModifiedDate
    private LocalDateTime lastModifiedDate;
    /**
     * The identifier of the user who last modified the activity.
     * This field stores the ID of the user who made the last modification to the activity, using @LastModifiedBy annotation.
     */
    @LastModifiedBy
    private String lastModifiedBy;
}
