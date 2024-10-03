package edu.stanford.slac.core_build_system.repository;

import edu.stanford.slac.core_build_system.model.Component;
import edu.stanford.slac.core_build_system.model.NewBranch;
import edu.stanford.slac.core_build_system.model.PullRequest;

public interface GitServerRepository {
    void createRepository(Component component) throws Exception;

    void deleteRepo(Component component) throws Exception;

    void addUserToRepository(Component component, String username) throws Exception;

    void addBranch(Component component, NewBranch newBranch) throws Exception;

    void createNewPR(Component component, PullRequest pullRequest) throws Exception;

    String downLoadRepository(Component component, String branchName, String clonePath) throws Exception;

    void enableEvent(Component component, String uriToCall, String hookName) throws Exception;

    void disableEvent(Component component, String hookName) throws Exception;
}
