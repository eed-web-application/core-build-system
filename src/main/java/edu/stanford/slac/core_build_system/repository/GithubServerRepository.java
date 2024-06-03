package edu.stanford.slac.core_build_system.repository;

import edu.stanford.slac.core_build_system.model.Component;
import edu.stanford.slac.core_build_system.model.NewBranch;
import edu.stanford.slac.core_build_system.model.PullRequest;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.kohsuke.github.*;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Log4j2
@AllArgsConstructor
public class GithubServerRepository implements GitServerRepository {
    private final GitHub gitHub;
    private final GHOrganization ghOrganization;
    private final GHAppInstallation ghAppInstallation;

    @Override
    public void createRepository(Component component) throws IOException {
        GHRepository repo = ghOrganization.createRepository(component.getName())
                .private_(true)
                .wiki(false)
                .projects(false)
                .description("Description")
                .autoInit(true)
                .allowMergeCommit(true)
                .allowSquashMerge(true)
                .allowRebaseMerge(false)
                .deleteBranchOnMerge(true)
                .create();
        log.info("Repository created: {}", repo.getHtmlUrl());
    }

    @Override
    public void deleteRepo(Component component) throws IOException {
        ghOrganization.getRepository(component.getName()).delete();
        log.info("Repository delete: {}", component.getName());
    }

    @Override
    public void addUserToRepository(Component component, String username) throws IOException {
        GHRepository repo = ghOrganization.getRepository(component.getName());
        List<GHUser> users = new ArrayList<>();
        GHUser user = gitHub.getUser("test-user");
        if (user == null) {
            throw new IOException("User not found");
        }
        users.add(gitHub.getUser(username));
        repo.addCollaborators(users, GHOrganization.RepositoryRole.from(GHOrganization.Permission.PULL));
    }

    @Override
    public void addBranch(Component component, NewBranch newBranch) throws IOException {
        GHRepository repo = ghOrganization.getRepository(component.getName());
        String parentRef = repo.getRef("heads/%s".formatted(newBranch.getBaseBranch())).getObject().getSha();
        GHRef draftBranch = repo.createRef("refs/heads/%s".formatted(newBranch.getBranchName()), parentRef);

        Map<String, GHBranch> branches = repo.getBranches();
        log.info("Branches: {}", branches);
    }

    @Override
    public void createNewPR(Component component, PullRequest pullRequest) throws IOException {
        GHRepository repo = ghOrganization.getRepository(component.getName());
        GHRef branchRef = repo.getRef("heads/%s".formatted(pullRequest.getBranchName()));

        GHPullRequest draftPR = repo.createPullRequest(pullRequest.getTitle(),
                branchRef.getRef(),
                "refs/heads/%s".formatted(pullRequest.getBaseBranch()),
                pullRequest.getBase(),
                true,
                false);
    }
}
