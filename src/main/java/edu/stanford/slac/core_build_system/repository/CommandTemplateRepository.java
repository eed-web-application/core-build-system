package edu.stanford.slac.core_build_system.repository;


import edu.stanford.slac.core_build_system.model.CommandTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Set;

public interface CommandTemplateRepository extends MongoRepository<CommandTemplate, String> {
    boolean existsByName(String name);

    boolean existsByIdAndParametersContains(String id, Set<String> parameters);
}
