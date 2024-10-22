package edu.stanford.slac.core_build_system.api.v1.mapper;

import edu.stanford.slac.core_build_system.api.v1.dto.*;
import edu.stanford.slac.core_build_system.exception.ComponentNotFound;
import edu.stanford.slac.core_build_system.model.Branch;
import edu.stanford.slac.core_build_system.model.Issue;
import edu.stanford.slac.core_build_system.model.Component;
import edu.stanford.slac.core_build_system.model.ComponentDependency;
import edu.stanford.slac.core_build_system.repository.ComponentRepository;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import static edu.stanford.slac.ad.eed.baselib.exception.Utility.wrapCatch;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = "spring"
)
public abstract class ComponentMapper {
    @Autowired
    private ComponentRepository componentRepository;
    @Mapping(target = "name", qualifiedByName = "sanitize-name")
    @Mapping(target = "dependOn", expression = "java(toModel(newComponentDTO.dependOn()))")
//    @Mapping(target = "versions", ignore = true)
    abstract public Component toModel(NewComponentDTO newComponentDTO);
    abstract public Component toModel(ComponentDTO componentDTO);
    abstract public ComponentDTO toDTO(Component component);
    abstract public ComponentSummaryDTO toSummaryDTO(Component component);
    @Mapping(target = "name", qualifiedByName = "sanitize-name")
    @Mapping(target = "dependOn", expression = "java(toModel(updateComponentDTO.dependOn()))")
    abstract public Component updateModel(UpdateComponentDTO updateComponentDTO, @MappingTarget Component componentToUpdate);

    @Named("sanitize-name")
    public String sanitizeName(String name) {
        return name.toLowerCase().trim().replace(" ", "-");
    }

    /**
     * Convert the component name to the component id.
     * @param dependency The component name.
     * @return The id of the component.
     */
    public ComponentDependency toModel(ComponentDependencyDTO dependency) {
        String componentId = wrapCatch(
                ()->
                        componentRepository.findByName(sanitizeName(dependency.componentName()))
                                .map(Component::getId)
                                .orElseThrow(
                                        ()-> ComponentNotFound.byId()
                                                .id(dependency.componentName())
                                                .errorCode(-1)
                                                .build()
                                ),
                -1
        );

        return ComponentDependency
                .builder()
                .componentId(componentId)
                .version(dependency.tagName())
                .build();
    }

    /**
     * Convert the list of component names to the list of component ids.
     * @param dependencies The list of component dependencies.
     * @return The list of component ids.
     */
    public Set<ComponentDependency> toModel(Set<ComponentDependencyDTO> dependencies) {
        if(dependencies == null) return Collections.emptySet();
        return dependencies.stream().map(this::toModel).collect(Collectors.toSet());
    }

    abstract public Branch toModel(BranchDTO branchDTO);
    abstract public Issue toModel(IssueDTO issueDTO);
}