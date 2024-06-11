package edu.stanford.slac.core_build_system.repository;
import com.mongodb.client.result.UpdateResult;
import edu.stanford.slac.core_build_system.model.BuildInfo;
import edu.stanford.slac.core_build_system.model.BuildStatus;
import edu.stanford.slac.core_build_system.model.ComponentBranchBuild;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Instant;
import java.util.Optional;

@Repository
public class ComponentBranchBuildRepositoryImpl implements ComponentBranchBuildRepositoryCustom {
    @Autowired
    private MongoTemplate mongoTemplate;


    @Override
    public boolean updateBuildInfo(String id, BuildInfo buildInfo){
        Query query = new Query(
                new Criteria("id").is(id)
        );
        Update update = new Update().set("buildInfo", buildInfo);
        return mongoTemplate.updateFirst(query, update, ComponentBranchBuild.class).getModifiedCount() > 0;
    }

    @Override
    public Optional<ComponentBranchBuild> findAndLockNextDocument(Instant lockTimeout) throws UnknownHostException {
        // Find an unlocked or expired lock document and atomically lock it
        Query query = new Query(
                new Criteria().orOperator(
                        new Criteria().andOperator(
                                Criteria.where("lockTime").lt(lockTimeout),
                                Criteria.where("buildStatus").nin(BuildStatus.IN_PROGRESS)
                        ),
                        new Criteria().andOperator(
                                Criteria.where("lockTime").exists(false),
                                Criteria.where("buildStatus").nin(BuildStatus.SUCCESS, BuildStatus.FAILED)
                        )
                )

        ).with(Sort.by(Sort.Order.asc("lastProcessTime"))).limit(1);

        Update update = new Update().set("lockTime", Instant.now()).set("lockedBy", InetAddress.getLocalHost().getHostName());
        return Optional.ofNullable(mongoTemplate.findAndModify(query, update, FindAndModifyOptions.options().returnNew(true),ComponentBranchBuild.class));
    }

    @Override
    public boolean releaseLock(String buildId) throws UnknownHostException {
        Query query = new Query(
                new Criteria().orOperator(
                        Criteria.where("id").is(buildId)
                )
        );
        Update u = new Update()
                .unset("lockTime")
                .unset("lockedBy")
                .set("lastProcessTime", Instant.now());
        UpdateResult ur = mongoTemplate.updateFirst(query, u, ComponentBranchBuild.class);
        return ur.getModifiedCount() > 0;
    }

    @Override
    public boolean releaseLock(String buildId, BuildStatus buildStatus) throws UnknownHostException {
        Query query = new Query(
                new Criteria().orOperator(
                        Criteria.where("id").is(buildId)
                )
        );
        Update u = new Update()
                .unset("lockTime")
                .unset("lockedBy")
                .set("lastProcessTime", Instant.now())
                .set("buildStatus", buildStatus);
        UpdateResult ur = mongoTemplate.updateFirst(query, u, ComponentBranchBuild.class);
        return ur.getModifiedCount() > 0;
    }
}
