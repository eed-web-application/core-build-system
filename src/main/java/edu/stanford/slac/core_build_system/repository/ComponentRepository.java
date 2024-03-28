package edu.stanford.slac.core_build_system.repository;


import edu.stanford.slac.core_build_system.model.Component;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ComponentRepository extends MongoRepository<Component, String> {
    boolean existsByNameAndVersion(String name, String version);

    /**
     * Check if a command template is used into a component
     *
     * @param buildCommandId The unique identifier of the command template
     * @return True if at least one component uses that command template
     */
    boolean existsByCommandTemplatesInstances_IdContains(String buildCommandId);

    /**
     * Check if a component  is used by another component
     *
     * @param id The unique identifier of the component
     * @return True if at least one component uses that component
     */
    boolean existsByDependOnComponentIdsContaining(String id);
}
