package edu.stanford.slac.core_build_system.repository;

import edu.stanford.slac.ad.eed.baselib.exception.ControllerLogicException;
import edu.stanford.slac.core_build_system.config.CoreBuildProperties;
import edu.stanford.slac.core_build_system.config.GitHubClient;
import edu.stanford.slac.core_build_system.model.Component;
import edu.stanford.slac.core_build_system.model.NewBranch;
import edu.stanford.slac.core_build_system.model.PullRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.eclipse.jgit.api.Git;
import org.kohsuke.github.*;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.Collections.singletonList;

@Log4j2
@Repository
@RequiredArgsConstructor
public class GithubServerRepository implements GitServerRepository {
    private final CoreBuildProperties coreBuildProperties;
    private final GitHubClient.GHInstancer ghInstancer;

    @Override
    public void createRepository(Component component) throws Exception {
        GHRepository repo = ghInstancer.ghOrganization(coreBuildProperties.getGithubOrgName()).createRepository(component.getName())
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
    public void deleteRepo(Component component) throws Exception {
        ghInstancer.ghOrganization(coreBuildProperties.getGithubOrgName()).getRepository(component.getName()).delete();
        log.info("Repository delete: {}", component.getName());
    }

    @Override
    public void addUserToRepository(Component component, String username) throws Exception {
        GHRepository repo = ghInstancer.ghOrganization(coreBuildProperties.getGithubOrgName()).getRepository(component.getName());
        List<GHUser> users = new ArrayList<>();
        GHUser user = ghInstancer.getClient().getUser("test-user");
        if (user == null) {
            throw new IOException("User not found");
        }
        users.add(ghInstancer.getClient().getUser(username));
        repo.addCollaborators(users, GHOrganization.RepositoryRole.from(GHOrganization.Permission.PULL));
    }

    @Override
    public void addBranch(Component component, NewBranch newBranch) throws Exception {
        GHRepository repo = ghInstancer.ghOrganization(coreBuildProperties.getGithubOrgName()).getRepository(component.getName());
        String parentRef = repo.getRef("heads/%s".formatted(newBranch.getBaseBranch())).getObject().getSha();
        GHRef draftBranch = repo.createRef("refs/heads/%s".formatted(newBranch.getBranchName()), parentRef);
        Map<String, GHBranch> branches = repo.getBranches();
        log.info("Branches: {}", branches);
    }

    @Override
    public void createNewPR(Component component, PullRequest pullRequest) throws Exception {
        GHRepository repo = ghInstancer.ghOrganization(coreBuildProperties.getGithubOrgName()).getRepository(component.getName());
        GHRef branchRef = repo.getRef("heads/%s".formatted(pullRequest.getBranchName()));

        GHPullRequest draftPR = repo.createPullRequest(pullRequest.getTitle(),
                branchRef.getRef(),
                "refs/heads/%s".formatted(pullRequest.getBaseBranch()),
                pullRequest.getBase(),
                true,
                false);
    }

    /**
     * Download the repository
     *
     * @param component  The component
     * @param branchName The branch name
     * @param clonePath  The path to clone the repository
     * @return The string representation of the repository
     * @throws Exception if there is an error
     */
    @Override
    public String downLoadRepository(Component component, String branchName, String clonePath) throws Exception {
        try (Git git = Git.cloneRepository()
                .setURI(component.getUrl())
                .setBranch(branchName)
                .setDirectory(new File(clonePath))
                .setCredentialsProvider(ghInstancer.gitCredentialsProvider())
                .call()) {
           return git.getRepository().getDirectory().getParent();
        }
    }
    @Override
    public void enableEvent(Component component, String uriToCall) throws Exception {
        log.debug("Enabling event for component: {}", component.getName());
        GHRepository repo = ghInstancer.ghOrganization(coreBuildProperties.getGithubOrgName()).getRepository(component.getName());
        log.debug("Creating hook for component: {}", component.getName());
        var gitHubHook = repo.createHook(
                "web",
                Map.of(
                        "url", uriToCall,
                        "content_type", "json",
                        "secret", component.getComponentToken()
                ),
                singletonList(GHEvent.ALL),
                true
        );
        log.info("Hook created: {}", gitHubHook);
    }

    @Override
    public void disableEvent(Component component, String uriToCall) throws Exception {
        log.debug("Disabling event for component: {}", component.getName());
        GHRepository repo = ghInstancer.ghOrganization(coreBuildProperties.getGithubOrgName()).getRepository(component.getName());
        log.debug("Deleting hook for component: {}", component.getName());
        repo.getHooks().stream()
                .filter(hook -> hook.getConfig().containsValue(uriToCall))
                .findFirst()
                .ifPresentOrElse(hook -> {
                            try {
                                hook.delete();
                            } catch (IOException e) {
                                log.error("Error deleting hook", e);
                            }
                        },
                        () -> {
                            throw ControllerLogicException
                                    .builder()
                                    .errorCode(-1)
                                    .errorMessage("Hook not found")
                                    .errorDomain("GithubServerRepository::disableEvent")
                                    .build();
                        });
    }
}
