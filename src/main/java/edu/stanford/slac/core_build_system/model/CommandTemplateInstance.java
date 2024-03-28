package edu.stanford.slac.core_build_system.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import edu.stanford.slac.core_build_system.api.v1.dto.CommandTemplateInstanceParameterDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@EqualsAndHashCode
public class CommandTemplateInstance{

        private String id;

        private List<CommandTemplateInstanceParameter> parameters;
}
