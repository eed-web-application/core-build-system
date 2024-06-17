package edu.stanford.slac.core_build_system.migration;

import edu.stanford.slac.ad.eed.base_mongodb_lib.utility.MongoDDLOps;
import edu.stanford.slac.core_build_system.model.ComponentBranchBuild;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;

@AllArgsConstructor
@ChangeUnit(id = "init-component-branch-build-index", order = "2", author = "bisegni")
public class ComponentBranchBuildIndex {
    private final MongoTemplate mongoTemplate;
    @Execution
    public void changeSet() {
        MongoDDLOps.createIndex(
                ComponentBranchBuild.class,
                mongoTemplate,
                new Index().on(
                                "componentId",
                                Sort.Direction.ASC
                        )
                        .on(
                                "branchName",
                                Sort.Direction.ASC
                        )
                        .named("name")
        );
    }

    @RollbackExecution
    public void rollback() {

    }
}
