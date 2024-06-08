package edu.stanford.slac.core_build_system.api.v1.mapper;

import edu.stanford.slac.core_build_system.api.v1.dto.BuildStatusDTO;
import edu.stanford.slac.core_build_system.api.v1.dto.ComponentBranchBuildDTO;
import edu.stanford.slac.core_build_system.model.BuildStatus;
import edu.stanford.slac.core_build_system.model.ComponentBranchBuild;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = "spring"
)
public abstract class ComponentBranchBuildMapper {
    abstract public ComponentBranchBuildDTO toDTO(ComponentBranchBuild componentBranchBuild);

    abstract public BuildStatus toModel(BuildStatusDTO status);
}
