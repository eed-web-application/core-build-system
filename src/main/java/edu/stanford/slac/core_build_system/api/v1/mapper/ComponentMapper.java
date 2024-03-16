package edu.stanford.slac.core_build_system.api.v1.mapper;

import edu.stanford.slac.core_build_system.api.v1.dto.ComponentDTO;
import edu.stanford.slac.core_build_system.api.v1.dto.NewComponentDTO;
import edu.stanford.slac.core_build_system.model.Component;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = "spring"
)
public abstract class ComponentMapper {
    abstract public Component toModel(NewComponentDTO newComponentDTO);
    abstract public ComponentDTO toDTO(Component component);
}