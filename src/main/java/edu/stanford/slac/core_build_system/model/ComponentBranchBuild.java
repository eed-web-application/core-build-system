package edu.stanford.slac.core_build_system.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Version;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@EqualsAndHashCode
public class ComponentBranchBuild {
    /**
     * The identifier of the component branch build
     */
    @Id
    private String id;

    /**
     * The identifier of the component branch build
     */
    @Field(targetType = FieldType.OBJECT_ID)
    private String componentId;

    /**
     * The name of the branch that is used to perform this build
     */
    private String branchName;

    /**
     * The name of the pod builder that is used to perform this build
     */
    private BuildInfo buildInfo;

    /**
     * The status of the build
     */
    @Builder.Default
    private BuildStatus buildStatus = BuildStatus.PENDING;

    /**
     * The date and time when the build was started
     */
    @Builder.Default
    private LocalDateTime lastProcessTime = LocalDateTime.now();

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

    /**
     * The version of the document
     * This field is automatically updated with the version of the document, using @Version annotation.
     */
    @Version
    Long version;

}
