package edu.stanford.slac.core_build_system.repository;


import edu.stanford.slac.core_build_system.model.Component;
import org.checkerframework.checker.units.qual.C;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ComponentRepository extends MongoRepository<Component, String> {
    boolean existsByNameAndVersion(String name, String version);
    Optional<Component> findByName(String name);
}
