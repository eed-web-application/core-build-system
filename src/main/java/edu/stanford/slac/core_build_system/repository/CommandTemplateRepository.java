package edu.stanford.slac.core_build_system.repository;


import edu.stanford.slac.core_build_system.model.Component;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CommandTemplateRepository extends MongoRepository<Component, String> {

}
