package edu.stanford.slac.core_build_system.api.v1.mapper;

import edu.stanford.slac.core_build_system.api.v1.dto.ComponentDTO;
import edu.stanford.slac.core_build_system.api.v1.dto.ComponentSummaryDTO;
import edu.stanford.slac.core_build_system.api.v1.dto.NewComponentDTO;
import edu.stanford.slac.core_build_system.api.v1.dto.UpdateComponentDTO;
import edu.stanford.slac.core_build_system.exception.ComponentNotFound;
import edu.stanford.slac.core_build_system.model.Component;
import edu.stanford.slac.core_build_system.repository.ComponentRepository;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static edu.stanford.slac.ad.eed.baselib.exception.Utility.wrapCatch;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = "spring"
)
public abstract class ComponentMapper {
    @Autowired
    private ComponentRepository componentRepository;
    abstract public Component toModel(NewComponentDTO newComponentDTO);
    abstract public ComponentDTO toDTO(Component component);
    abstract public ComponentSummaryDTO toSummaryDTO(Component component);

    abstract public Component updateModel(UpdateComponentDTO updateComponentDTO, @MappingTarget Component componentToUpdate);

    /**
     * Convert the component name to the component id.
     * @param name The name of the component.
     * @return The id of the component.
     */
    public String nameToId(String name) {
        return wrapCatch(
                ()->
                        componentRepository.findByName(name)
                                .map(Component::getId)
                                .orElseThrow(
                                        ()-> ComponentNotFound.byId()
                                                .id(name)
                                                .errorCode(-1)
                                                .build()
                                ),
                -1
        );
    }

    /**
     * Convert the list of component names to the list of component ids.
     * @param names The list of component names.
     * @return The list of component ids.
     */
    public List<String> nameToId(List<String> names) {
        return names.stream().map(this::nameToId).toList();
    }
}