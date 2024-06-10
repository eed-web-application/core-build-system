package edu.stanford.slac.core_build_system.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
public class LogEntry {
    @Id
    private String id;
    @Field(targetType = FieldType.OBJECT_ID)
    private String buildId;
    private LocalDateTime timestamp;
    private String log;
}
