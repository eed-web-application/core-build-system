package edu.stanford.slac.core_build_system.repository;


import edu.stanford.slac.core_build_system.model.CommandTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CommandTemplateRepository extends MongoRepository<CommandTemplate, String> {
    boolean existsByName(String name);
}
