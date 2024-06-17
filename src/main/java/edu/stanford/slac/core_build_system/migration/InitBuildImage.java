package edu.stanford.slac.core_build_system.migration;

import edu.stanford.slac.ad.eed.base_mongodb_lib.utility.MongoDDLOps;
import edu.stanford.slac.core_build_system.model.BuildImage;
import edu.stanford.slac.core_build_system.model.BuildOS;
import edu.stanford.slac.core_build_system.repository.BuildImageRepository;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;

@AllArgsConstructor
@ChangeUnit(id = "init-build-os-index", order = "3", author = "bisegni")
public class InitBuildImage {
    private BuildImageRepository buildImageRepository;
    private final MongoTemplate mongoTemplate;
    @Execution
    public void changeSet() {
        MongoDDLOps.createIndex(
                BuildImage.class,
                mongoTemplate,
                new Index().on(
                                "os",
                                Sort.Direction.ASC
                        )
                        .on(
                                "dockerImageUrl",
                                Sort.Direction.ASC
                        )
                        .named("os-image")
                        .unique()
        );

        buildImageRepository.save(
                BuildImage.builder()
                        .os(BuildOS.RHEL8)
                        .dockerImageUrl("pnispero/rhel8-env:latest")
                        .build()
        );
        buildImageRepository.save(
                BuildImage.builder()
                        .os(BuildOS.ROCKY9)
                        .dockerImageUrl("pnispero/rocky9-env:latest")
                        .build()
        );
    }

    @RollbackExecution
    public void rollback() {

    }
}
