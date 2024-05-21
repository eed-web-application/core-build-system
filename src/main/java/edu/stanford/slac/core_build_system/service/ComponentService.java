package edu.stanford.slac.core_build_system.service;

import edu.stanford.slac.ad.eed.baselib.exception.ControllerLogicException;
import edu.stanford.slac.core_build_system.api.v1.dto.*;
import edu.stanford.slac.core_build_system.api.v1.mapper.ComponentMapper;
import edu.stanford.slac.core_build_system.exception.ComponentAlreadyExists;
import edu.stanford.slac.core_build_system.exception.ComponentNotFound;
import edu.stanford.slac.core_build_system.model.ComponentDependency;
import edu.stanford.slac.core_build_system.repository.CommandTemplateRepository;
import edu.stanford.slac.core_build_system.repository.ComponentRepository;
import edu.stanford.slac.core_build_system.service.engine.EngineFactory;
import io.mongock.runner.core.executor.dependency.DependencyContext;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.io.ByteArrayInputStream;
import java.util.*;

import static edu.stanford.slac.ad.eed.baselib.exception.Utility.assertion;
import static edu.stanford.slac.ad.eed.baselib.exception.Utility.wrapCatch;

@Service
@Validated
@AllArgsConstructor
public class ComponentService {
    private final ComponentMapper componentMapper;
    private final ComponentRepository componentRepository;
    private final CommandTemplateRepository commandTemplateRepository;
    private final EngineFactory engineFactory;

    /**
     * Create a new component
     *
     * @param newComponentDTO The details of the new component
     * @return The unique identifier of the new component
     */
    public String create(@Valid NewComponentDTO newComponentDTO) {

        var componentToSave = componentMapper.toModel(newComponentDTO);
        // check if there is a conflict
        assertion(
                ComponentAlreadyExists.byName()
                        .errorCode(-1)
                        .name(componentToSave.getName())
                        .build(),
                () -> !componentRepository.existsByName(
                        componentToSave.getName()
                )
        );
        // check dependency
        validateDependencies(Optional.empty(), componentToSave.getDependOn());

        // create a new component
        var savedComponent = wrapCatch(
                () -> componentRepository.save(componentToSave),
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
                componentRepository::findAll,
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

        var componentUpdated = componentMapper.updateModel(updateComponentDTO, componentToUpdate);

        // check if there is a conflict with anther component
        assertion(
                ComponentAlreadyExists.byName()
                        .errorCode(-1)
                        .name(componentUpdated.getName())
                        .build(),
                () -> !componentRepository.existsByNameAndIdIsNot(
                        componentUpdated.getName(),
                        componentUpdated.getId()
                )
        );

        // check for depend on itself
        validateDependencies(Optional.of(id), componentUpdated.getDependOn());

        // update
        wrapCatch(
                () -> componentRepository.save(componentUpdated),
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
                () -> !componentRepository.existsByDependOn_ComponentIdContains(id)
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

    /**
     * Create an artifact by engine name and component list
     *
     * @param engineName   The name of the engine
     * @param componentIds The list of component unique identifiers
     * @param buildSpecs  The build specs
     * @return The artifact
     */
    public FileResourceDTO createArtifactByEngineNameAndComponentList(String engineName, List<String> componentIds, Map<String, String> buildSpecs) {
        var components = wrapCatch(
                () -> componentRepository.findAllById(componentIds),
                -1
        );
        var engineBuilder = wrapCatch(
                () -> engineFactory.getEngineBuilder(engineName),
                -1
        );
        components.forEach(engineBuilder::addComponent);
        buildSpecs.forEach(engineBuilder::addBuilderSpec);
        String content = engineBuilder.build();
        return FileResourceDTO
                .builder()
                .length(content.length())
                .fileStream(new ByteArrayInputStream(content.getBytes()))
                .fileName(engineName.equals("docker") ? "Dockerfile" : "ansible.yml")
                .build();
    }

    /**
     * Get the list of engine names
     *
     * @return The list of engine names
     */
    public Set<String> getEngineNames() {
        return engineFactory.getEngineNames();
    }

    /**
     * Validate the dependencies
     *
     * @param dependOnComponent The list of dependencies
     */
    private void validateDependencies(Optional<String> parentId, Set<ComponentDependency> dependOnComponent) {
        if (dependOnComponent == null || dependOnComponent.isEmpty()) return;
        // check cyclic dependency
        parentId.ifPresent(
                s -> assertion(
                        ControllerLogicException.builder()
                                .errorCode(-1)
                                .errorMessage("The component cannot depend on itself")
                                .build(),
                        () -> dependOnComponent.stream().noneMatch(d -> d.getComponentId().compareToIgnoreCase(s) == 0)
                )
        );

        var dependOnComponentIds = dependOnComponent.stream().map(ComponentDependency::getComponentId).toList();
        dependOnComponentIds.forEach(
                componentId -> {
                    assertion(
                            ControllerLogicException.builder()
                                    .errorCode(-2)
                                    .errorMessage("The component has not been found")
                                    .build(),
                            () -> componentRepository.existsById(componentId)
                    );
                }
        );
    }
}
