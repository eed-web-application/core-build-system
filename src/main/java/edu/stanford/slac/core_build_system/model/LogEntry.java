package edu.stanford.slac.core_build_system.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.time.LocalDateTime;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class LogEntry {
    @Id
    private String id;
    @Field(targetType = FieldType.OBJECT_ID)
    private String buildId;
    private LocalDateTime timestamp;
    private String log;
}
