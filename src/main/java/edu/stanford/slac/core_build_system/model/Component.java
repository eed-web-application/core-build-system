package edu.stanford.slac.core_build_system.model;

import edu.stanford.slac.core_build_system.api.v1.dto.CommandTemplateDTO;
import edu.stanford.slac.core_build_system.api.v1.dto.CommandTemplateInstanceDTO;
import edu.stanford.slac.core_build_system.api.v1.dto.CommandTemplateParameterDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@EqualsAndHashCode
public class Component {
    private String id;
    private String name;
    private String organization;
    private String description;
    private String url;
    private String approvalRule;
    private List<String> approvalIdentity;
    private String testingCriteria;
    private String buildInstructions;
    /**
     * The version of the component.
     * Version of component schema (NOT the repo version)
     */
    private String version;
    @Builder.Default
    private Set<ComponentDependency> dependOnComponentIds = new java.util.HashSet<>();
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
