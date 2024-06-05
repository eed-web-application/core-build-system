package edu.stanford.slac.core_build_system.repository;

import edu.stanford.slac.core_build_system.model.ComponentBranchBuild;

import java.net.UnknownHostException;
import java.time.Instant;
import java.util.Optional;

public interface ComponentBranchBuildRepositoryCustom {
    /**
     * Find and lock the next document that is not locked or the lock has expired
     * @param lockTimeout The time when the lock will expire
     * @return The document that was locked
     */
    Optional<ComponentBranchBuild> findAndLockNextDocument(Instant lockTimeout) throws UnknownHostException;
    /**
     * Release the lock on the document
     * @param buildId The identifier of the document
     * @return True if the lock was released, false otherwise
     */
    boolean releaseLock(String buildId) throws UnknownHostException;
}