package edu.stanford.slac.core_build_system.migration;

import edu.stanford.slac.ad.eed.base_mongodb_lib.utility.MongoDDLOps;
import edu.stanford.slac.core_build_system.model.Component;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;

@AllArgsConstructor
@ChangeUnit(id = "init-component-index", order = "1", author = "bisegni")
public class ComponentIndex {
    private final MongoTemplate mongoTemplate;
    @Execution
    public void changeSet() {
        MongoDDLOps.createIndex(
                Component.class,
                mongoTemplate,
                new Index().on(
                                "name",
                                Sort.Direction.ASC
                        )
                        .named("name")
                        .unique()
        );
    }

    @RollbackExecution
    public void rollback() {

    }
}
