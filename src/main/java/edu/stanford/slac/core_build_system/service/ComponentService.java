package edu.stanford.slac.core_build_system.service;

import edu.stanford.slac.ad.eed.baselib.exception.ControllerLogicException;
import edu.stanford.slac.core_build_system.api.v1.dto.ComponentDTO;
import edu.stanford.slac.core_build_system.api.v1.dto.ComponentSummaryDTO;
import edu.stanford.slac.core_build_system.api.v1.dto.NewComponentDTO;
import edu.stanford.slac.core_build_system.api.v1.dto.UpdateComponentDTO;
import edu.stanford.slac.core_build_system.api.v1.mapper.ComponentMapper;
import edu.stanford.slac.core_build_system.exception.ComponentAlreadyExists;
import edu.stanford.slac.core_build_system.exception.ComponentNotFound;
import edu.stanford.slac.core_build_system.repository.CommandTemplateRepository;
import edu.stanford.slac.core_build_system.repository.ComponentRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
                () -> !componentRepository.existsByNameAndVersion(
                        newComponentDTO.name(),
                        newComponentDTO.version()
                )
        );

        //check if the command templates exists
        newComponentDTO.commandTemplatesInstances().forEach(
                templateInstance -> {
                    // check for the existence of the command template
                    assertion(
                            ControllerLogicException.builder()
                                    .errorCode(-2)
                                    .errorMessage("The command template %s does not exist".formatted(templateInstance.id()))
                                    .build(),
                            () -> commandTemplateRepository.existsById(templateInstance.id())
                    );

                    // check for the validity of the parameters for the command template
                    assertion(
                            ControllerLogicException.builder()
                                    .errorCode(-3)
                                    .errorMessage("One or more parameters '%s' are not valid for the command template".formatted(String.join(", ", templateInstance.parametersValues().keySet())))
                                    .build(),
                            () -> commandTemplateRepository.existsByIdAndParametersContains(
                                    templateInstance.id(),
                                    templateInstance.parametersValues().keySet()
                            )
                    );
                }

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
                .orElseThrow(() -> ComponentNotFound.byId().errorCode(-2).id(id).build());
    }

    /**
     * Find all components
     *
     * @return The details of all components
     */
    public List<ComponentSummaryDTO> findAll() {
        return wrapCatch(
                () -> componentRepository.findAll(),
                -1
        ).stream().map(componentMapper::toSummaryDTO).toList();
    }

    /**
     * Update a component by its unique identifier
     *
     * @param id                 The unique identifier of the component
     * @param updateComponentDTO The details of the component to update
     */
    public void updateById(String id, UpdateComponentDTO updateComponentDTO) {
        var componentToUpdate = wrapCatch(
                () -> componentRepository.findById(id),
                -1
        ).orElseThrow(() -> ComponentNotFound.byId().errorCode(-2).id(id).build());

        // check for depend on itself
        if (updateComponentDTO.dependOnComponentIds() != null) {
            assertion(
                    ControllerLogicException.builder()
                            .errorCode(-1)
                            .errorMessage("The component cannot depend on itself")
                            .build(),
                    () -> !updateComponentDTO.dependOnComponentIds().contains(id)
            );
        }

        // update
        wrapCatch(
                () -> componentRepository.save(
                        componentMapper.updateModel(updateComponentDTO, componentToUpdate)
                ),
                -1
        );
    }

    @Transactional
    public void deleteById(String id) {
        // check presence
        assertion(
                ComponentNotFound.byId()
                        .errorCode(-1)
                        .id(id)
                        .build(),
                () -> componentRepository.existsById(id)
        );
        // check usage by other components
        assertion(
                ControllerLogicException.builder()
                        .errorCode(-2)
                        .errorMessage("The component is in use by other components")
                        .build(),
                () -> !componentRepository.existsByDependOnComponentIdsContaining(id)
        );
        //delete
        wrapCatch(
                () -> {
                    componentRepository.deleteById(id);
                    return null;
                },
                -3
        );
    }
}
