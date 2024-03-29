package edu.stanford.slac.core_build_system.service;

import edu.stanford.slac.ad.eed.baselib.exception.ControllerLogicException;
import edu.stanford.slac.core_build_system.api.v1.dto.*;
import edu.stanford.slac.core_build_system.api.v1.mapper.CommandTemplateMapper;
import edu.stanford.slac.core_build_system.exception.CommandTemplateAlreadyExists;
import edu.stanford.slac.core_build_system.exception.CommandTemplateNotFound;
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
public class CommandTemplateService {
    CommandTemplateMapper commandTemplateMapper;
    ComponentRepository componentRepository;
    CommandTemplateRepository commandTemplateRepository;

    /**
     * Create a new component
     *
     * @param newCommandTemplateDTO The details of the new component
     * @return The unique identifier of the new component
     */
    public String create(@Valid NewCommandTemplateDTO newCommandTemplateDTO) {
        // check if there is a conflict
        assertion(
                CommandTemplateAlreadyExists.byName()
                        .errorCode(-1)
                        .name(newCommandTemplateDTO.name())
                        .build(),
                ()-> !commandTemplateRepository.existsByName(
                        newCommandTemplateDTO.name()
                )
        );
        // create a new component
        var savedComponent = wrapCatch(
                () -> commandTemplateRepository.save(
                        commandTemplateMapper.toModel(newCommandTemplateDTO)
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
    public CommandTemplateDTO findById(String id) {
        return wrapCatch(
                () -> commandTemplateRepository.findById(id),
                -1
        )
                .map(commandTemplateMapper::toDTO)
                .orElseThrow(() -> CommandTemplateNotFound.byId().errorCode(-2).id(id).build());
    }

    /**
     * Find all components
     *
     * @return The details of all components
     */
    public List<CommandTemplateSummaryDTO> findAll() {
        return wrapCatch(
                () -> commandTemplateRepository.findAll(),
                -1
        ).stream().map(commandTemplateMapper::toSummaryDTO).toList();
    }

    /**
     * Delete a component by its unique identifier
     *
     * @param id The unique identifier of the component
     */
    public void deleteById(String id) {
        assertion(
                ControllerLogicException.builder()
                        .errorCode(-1)
                        .errorMessage("The command template is in use by components")
                        .build(),
                () -> !componentRepository.existsByCommandTemplatesInstances_IdContains(id)
        );
        wrapCatch(
                () -> {commandTemplateRepository.deleteById(id); return null;},
                -1
        );
    }

    /**
     * Update a component by its unique identifier
     *
     * @param updateComponentDTO The details of the component to update
     */
    public void updateById(String id, UpdateCommandTemplateDTO updateComponentDTO) {
        var commandTemplateToUpdate = wrapCatch(
                () -> commandTemplateRepository.findById(id),
                -1
        )
                .orElseThrow(() -> CommandTemplateNotFound.byId().errorCode(-2).id(id).build());

        wrapCatch(
                () -> commandTemplateRepository.save(
                        commandTemplateMapper.updateModel(updateComponentDTO, commandTemplateToUpdate)
                ),
                -1
        );
    }
}
