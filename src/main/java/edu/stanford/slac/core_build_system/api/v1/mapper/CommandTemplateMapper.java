package edu.stanford.slac.core_build_system.api.v1.mapper;

import edu.stanford.slac.core_build_system.api.v1.dto.*;
import edu.stanford.slac.core_build_system.model.CommandTemplate;
import edu.stanford.slac.core_build_system.model.Component;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = "spring"
)
public abstract class CommandTemplateMapper {
    abstract public CommandTemplate toModel(NewCommandTemplateDTO newCommandTemplateDTO);
    abstract public CommandTemplateDTO toDTO(CommandTemplate commandTemplate);
    abstract public CommandTemplateSummaryDTO toSummaryDTO(CommandTemplate commandTemplate);
    abstract public CommandTemplate updateModel(UpdateCommandTemplateDTO updateComponentDTO, @MappingTarget CommandTemplate commandTemplate);
}