package edu.stanford.slac.core_build_system.repository;


import edu.stanford.slac.core_build_system.model.ComponentBranchBuild;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ComponentBranchBuildRepository extends MongoRepository<ComponentBranchBuild, String>, ComponentBranchBuildRepositoryCustom {

}
