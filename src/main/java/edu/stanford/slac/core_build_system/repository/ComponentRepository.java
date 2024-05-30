package edu.stanford.slac.core_build_system.repository;


import edu.stanford.slac.core_build_system.model.Component;
import org.checkerframework.checker.units.qual.C;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ComponentRepository extends MongoRepository<Component, String> {
    boolean existsByDependOn_ComponentIdContains(String componentId);
    Optional<Component> findByName(String name);
    boolean existsByName(String name);
    boolean existsByNameAndIdIsNot(String name, String id);
}
