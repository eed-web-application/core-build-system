package edu.stanford.slac.core_build_system.service;

import edu.stanford.slac.core_build_system.api.v1.dto.ComponentDTO;
import edu.stanford.slac.core_build_system.api.v1.dto.NewComponentDTO;
import edu.stanford.slac.core_build_system.api.v1.mapper.ComponentMapper;
import edu.stanford.slac.core_build_system.exception.ComponentAlreadyExists;
import edu.stanford.slac.core_build_system.exception.ComponentNotFound;
import edu.stanford.slac.core_build_system.repository.CommandTemplateRepository;
import edu.stanford.slac.core_build_system.repository.ComponentRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static edu.stanford.slac.ad.eed.baselib.exception.Utility.assertion;
import static edu.stanford.slac.ad.eed.baselib.exception.Utility.wrapCatch;

@Service
@Validated
@AllArgsConstructor
public class ComponentService {
    ComponentMapper componentMapper;
    ComponentRepository componentRepository;
    CommandTemplateRepository commandTemplateRepository;

    /**
     * Create a new component
     *
     * @param newComponentDTO The details of the new component
     * @return The unique identifier of the new component
     */
    public String create(@Valid NewComponentDTO newComponentDTO) {
        // check if there is a conflict
        assertion(
                ComponentAlreadyExists.byNameAndVersion()
                        .errorCode(-1)
                        .name(newComponentDTO.name())
                        .version(newComponentDTO.version())
                        .build(),
                ()-> !componentRepository.existsByNameAndVersion(
                        newComponentDTO.name(),
                        newComponentDTO.version()
                )
        );
        // create a new component
        var savedComponent = wrapCatch(
                () -> componentRepository.save(
                        componentMapper.toModel(newComponentDTO)
                ),
                -1
        );
        return savedComponent.getId();
    }

    /**
     * Find a component by its unique identifier
     *
     * @param id The unique identifier of the component
     * @return The details of the component
     */
    public ComponentDTO findById(String id) {
        return wrapCatch(
                () -> componentRepository.findById(id),
                -1
        )
                .map(componentMapper::toDTO)
                .orElseThrow(() -> ComponentNotFound.byId().errorCode(-2).componentId(id).build());
    }

    /**
     * Find all components
     *
     * @return The details of all components
     */
    public List<ComponentDTO> findAll() {
        return wrapCatch(
                () -> componentRepository.findAll(),
                -1
        ).stream().map(componentMapper::toDTO).toList();
    }
}
