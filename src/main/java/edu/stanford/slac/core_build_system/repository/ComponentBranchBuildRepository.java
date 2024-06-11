package edu.stanford.slac.core_build_system.repository;


import edu.stanford.slac.core_build_system.model.ComponentBranchBuild;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ComponentBranchBuildRepository extends MongoRepository<ComponentBranchBuild, String>, ComponentBranchBuildRepositoryCustom {
    List<ComponentBranchBuild> findByComponentIdAndBranchName(String componentName, String branchName);
}
