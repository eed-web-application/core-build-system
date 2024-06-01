package edu.stanford.slac.core_build_system.api.v1.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.Map;

@Schema(description = "DTO representing a GitHub pull request webhook event")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public record GitHubPullRequestWebhookDTO(
        @Schema(description = "The type of event") String event,
        @Schema(description = "The payload of the pull request event") Payload payload
) {
    @Schema(description = "Payload containing details about the pull request event")
    public record Payload(
            @Schema(description = "The action that was performed") String action,
            @Schema(description = "The number of the pull request") int number,
            @Schema(description = "Details of the pull request") PullRequest pullRequest,
            @Schema(description = "The repository details") Repository repository,
            @Schema(description = "The organization details") Organization organization,
            @Schema(description = "The sender details") Sender sender
    ) {
    }

    @Schema(description = "Pull request details")
    public record PullRequest(
            @Schema(description = "The API URL of the pull request") String url,
            @Schema(description = "The ID of the pull request") long id,
            @Schema(description = "The node ID of the pull request") String nodeId,
            @Schema(description = "The HTML URL of the pull request") String htmlUrl,
            @Schema(description = "The diff URL of the pull request") String diffUrl,
            @Schema(description = "The patch URL of the pull request") String patchUrl,
            @Schema(description = "The issue URL of the pull request") String issueUrl,
            @Schema(description = "The number of the pull request") int number,
            @Schema(description = "The state of the pull request") String state,
            @Schema(description = "Indicates if the pull request is locked") boolean locked,
            @Schema(description = "The title of the pull request") String title,
            @Schema(description = "The user who created the pull request") User user,
            @Schema(description = "The body of the pull request") String body,
            @Schema(description = "The creation timestamp of the pull request") String createdAt,
            @Schema(description = "The last updated timestamp of the pull request") String updatedAt,
            @Schema(description = "The closed timestamp of the pull request") String closedAt,
            @Schema(description = "The merged timestamp of the pull request") String mergedAt,
            @Schema(description = "The SHA of the merge commit") String mergeCommitSha,
            @Schema(description = "The assignee of the pull request") User assignee,
            @Schema(description = "The list of assignees of the pull request") List<User> assignees,
            @Schema(description = "The list of requested reviewers") List<User> requestedReviewers,
            @Schema(description = "The list of requested teams") List<Team> requestedTeams,
            @Schema(description = "The list of labels") List<Label> labels,
            @Schema(description = "The milestone of the pull request") Milestone milestone,
            @Schema(description = "Indicates if the pull request is a draft") boolean draft,
            @Schema(description = "The URL of the commits in the pull request") String commitsUrl,
            @Schema(description = "The URL of the review comments in the pull request") String reviewCommentsUrl,
            @Schema(description = "The URL of the review comment") String reviewCommentUrl,
            @Schema(description = "The URL of the comments in the pull request") String commentsUrl,
            @Schema(description = "The URL of the statuses in the pull request") String statusesUrl,
            @Schema(description = "The head of the pull request") Branch head,
            @Schema(description = "The base of the pull request") Branch base,
            @Schema(description = "The links related to the pull request") Links links,
            @Schema(description = "The author association") String authorAssociation,
            @Schema(description = "The auto merge information") AutoMerge autoMerge,
            @Schema(description = "The active lock reason") String activeLockReason,
            @Schema(description = "Indicates if the pull request is merged") boolean merged,
            @Schema(description = "The mergeable state of the pull request") String mergeableState,
            @Schema(description = "The number of comments on the pull request") int comments,
            @Schema(description = "The number of review comments on the pull request") int reviewComments,
            @Schema(description = "Indicates if the maintainer can modify the pull request") boolean maintainerCanModify,
            @Schema(description = "The number of commits in the pull request") int commits,
            @Schema(description = "The number of additions in the pull request") int additions,
            @Schema(description = "The number of deletions in the pull request") int deletions,
            @Schema(description = "The number of changed files in the pull request") int changedFiles
    ) {
    }

    @Schema(description = "Repository details")
    public record Repository(
            @Schema(description = "Repository ID") Long id,
            @Schema(description = "Repository node ID") String nodeId,
            @Schema(description = "Repository name") String name,
            @Schema(description = "Full name of the repository") String fullName,
            @Schema(description = "Indicates if the repository is private") boolean isPrivate,
            @Schema(description = "Owner details of the repository") Owner owner,
            @Schema(description = "HTML URL of the repository") String htmlUrl,
            @Schema(description = "Description of the repository") String description,
            @Schema(description = "Indicates if the repository is a fork") boolean fork,
            @Schema(description = "API URL of the repository") String url,
            @Schema(description = "Creation timestamp of the repository") String createdAt,
            @Schema(description = "Last updated timestamp of the repository") String updatedAt,
            @Schema(description = "Last pushed timestamp of the repository") String pushedAt,
            @Schema(description = "Git URL of the repository") String gitUrl,
            @Schema(description = "SSH URL of the repository") String sshUrl,
            @Schema(description = "Clone URL of the repository") String cloneUrl,
            @Schema(description = "SVN URL of the repository") String svnUrl,
            @Schema(description = "Homepage of the repository") String homepage,
            @Schema(description = "Size of the repository") int size,
            @Schema(description = "Stargazers count of the repository") int stargazersCount,
            @Schema(description = "Watchers count of the repository") int watchersCount,
            @Schema(description = "Programming language of the repository") String language,
            @Schema(description = "Indicates if the repository has issues enabled") boolean hasIssues,
            @Schema(description = "Indicates if the repository has projects enabled") boolean hasProjects,
            @Schema(description = "Indicates if the repository has downloads enabled") boolean hasDownloads,
            @Schema(description = "Indicates if the repository has wiki enabled") boolean hasWiki,
            @Schema(description = "Indicates if the repository has pages enabled") boolean hasPages,
            @Schema(description = "Indicates if the repository has discussions enabled") boolean hasDiscussions,
            @Schema(description = "Forks count of the repository") int forksCount,
            @Schema(description = "Mirror URL of the repository") String mirrorUrl,
            @Schema(description = "Indicates if the repository is archived") boolean archived,
            @Schema(description = "Indicates if the repository is disabled") boolean disabled,
            @Schema(description = "Open issues count of the repository") int openIssuesCount,
            @Schema(description = "License of the repository") String license,
            @Schema(description = "Indicates if the repository allows forking") boolean allowForking,
            @Schema(description = "Indicates if the repository is a template") boolean isTemplate,
            @Schema(description = "Indicates if web commit signoff is required") boolean webCommitSignoffRequired,
            @Schema(description = "Topics of the repository") List<String> topics,
            @Schema(description = "Visibility of the repository") String visibility,
            @Schema(description = "Forks count of the repository") int forks,
            @Schema(description = "Open issues count of the repository") int openIssues,
            @Schema(description = "Watchers count of the repository") int watchers,
            @Schema(description = "Default branch of the repository") String defaultBranch,
            @Schema(description = "Stargazers count of the repository") int stargazers,
            @Schema(description = "Master branch of the repository") String masterBranch,
            @Schema(description = "Organization of the repository") String organization,
            @Schema(description = "Custom properties of the repository") Map<String, Object> customProperties
    ) {
        @Schema(description = "Owner details of the repository")
        public record Owner(
                @Schema(description = "Owner login") String login,
                @Schema(description = "Owner ID") Long id,
                @Schema(description = "Owner node ID") String nodeId,
                @Schema(description = "Owner avatar URL") String avatarUrl,
                @Schema(description = "Owner gravatar ID") String gravatarId,
                @Schema(description = "Owner API URL") String url,
                @Schema(description = "Owner HTML URL") String htmlUrl,
                @Schema(description = "Owner followers URL") String followersUrl,
                @Schema(description = "Owner following URL") String followingUrl,
                @Schema(description = "Owner gists URL") String gistsUrl,
                @Schema(description = "Owner starred URL") String starredUrl,
                @Schema(description = "Owner subscriptions URL") String subscriptionsUrl,
                @Schema(description = "Owner organizations URL") String organizationsUrl,
                @Schema(description = "Owner repos URL") String reposUrl,
                @Schema(description = "Owner events URL") String eventsUrl,
                @Schema(description = "Owner received events URL") String receivedEventsUrl,
                @Schema(description = "Owner type") String type,
                @Schema(description = "Indicates if the owner is a site admin") boolean siteAdmin
        ) {
        }
    }

    @Schema(description = "Organization details")
    public record Organization(
            @Schema(description = "Organization login") String login,
            @Schema(description = "Organization ID") Long id,
            @Schema(description = "Organization node ID") String nodeId,
            @Schema(description = "Organization API URL") String url,
            @Schema(description = "Organization repos URL") String reposUrl,
            @Schema(description = "Organization events URL") String eventsUrl,
            @Schema(description = "Organization hooks URL") String hooksUrl,
            @Schema(description = "Organization issues URL") String issuesUrl,
            @Schema(description = "Organization members URL") String membersUrl,
            @Schema(description = "Organization public members URL") String publicMembersUrl,
            @Schema(description = "Organization avatar URL") String avatarUrl,
            @Schema(description = "Organization description") String description
    ) {
    }

    @Schema(description = "Sender details")
    public record Sender(
            @Schema(description = "Sender login") String login,
            @Schema(description = "Sender ID") Long id,
            @Schema(description = "Sender node ID") String nodeId,
            @Schema(description = "Sender avatar URL") String avatarUrl,
            @Schema(description = "Sender gravatar ID") String gravatarId,
            @Schema(description = "Sender API URL") String url,
            @Schema(description = "Sender HTML URL") String htmlUrl,
            @Schema(description = "Sender followers URL") String followersUrl,
            @Schema(description = "Sender following URL") String followingUrl,
            @Schema(description = "Sender gists URL") String gistsUrl,
            @Schema(description = "Sender starred URL") String starredUrl,
            @Schema(description = "Sender subscriptions URL") String subscriptionsUrl,
            @Schema(description = "Sender organizations URL") String organizationsUrl,
            @Schema(description = "Sender repos URL") String reposUrl,
            @Schema(description = "Sender events URL") String eventsUrl,
            @Schema(description = "Sender received events URL") String receivedEventsUrl,
            @Schema(description = "Sender type") String type,
            @Schema(description = "Indicates if the sender is a site admin") boolean siteAdmin
    ) {
    }

    @Schema(description = "Branch details")
    public record Branch(
            @Schema(description = "Branch label") String label,
            @Schema(description = "Branch reference") String ref,
            @Schema(description = "Branch SHA") String sha,
            @Schema(description = "Branch user details") User user,
            @Schema(description = "Branch repository details") Repository repo
    ) {
    }

    @Schema(description = "User details")
    public record User(
            @Schema(description = "User login") String login,
            @Schema(description = "User ID") Long id,
            @Schema(description = "User node ID") String nodeId,
            @Schema(description = "User avatar URL") String avatarUrl,
            @Schema(description = "User gravatar ID") String gravatarId,
            @Schema(description = "User API URL") String url,
            @Schema(description = "User HTML URL") String htmlUrl,
            @Schema(description = "User followers URL") String followersUrl,
            @Schema(description = "User following URL") String followingUrl,
            @Schema(description = "User gists URL") String gistsUrl,
            @Schema(description = "User starred URL") String starredUrl,
            @Schema(description = "User subscriptions URL") String subscriptionsUrl,
            @Schema(description = "User organizations URL") String organizationsUrl,
            @Schema(description = "User repos URL") String reposUrl,
            @Schema(description = "User events URL") String eventsUrl,
            @Schema(description = "User received events URL") String receivedEventsUrl,
            @Schema(description = "User type") String type,
            @Schema(description = "Indicates if the user is a site admin") boolean siteAdmin
    ) {
    }

    @Schema(description = "Team details")
    public record Team(
            @Schema(description = "Team name") String name,
            @Schema(description = "Team ID") Long id,
            @Schema(description = "Team node ID") String nodeId,
            @Schema(description = "Team slug") String slug,
            @Schema(description = "Team description") String description,
            @Schema(description = "Team privacy level") String privacy,
            @Schema(description = "Team permission level") String permission,
            @Schema(description = "Team members URL") String membersUrl,
            @Schema(description = "Team repositories URL") String repositoriesUrl,
            @Schema(description = "Team organization") Organization organization
    ) {
    }

    @Schema(description = "Label details")
    public record Label(
            @Schema(description = "Label ID") Long id,
            @Schema(description = "Label node ID") String nodeId,
            @Schema(description = "Label URL") String url,
            @Schema(description = "Label name") String name,
            @Schema(description = "Label description") String description,
            @Schema(description = "Label color") String color,
            @Schema(description = "Indicates if the label is a default label") boolean isDefault
    ) {
    }

    @Schema(description = "Milestone details")
    public record Milestone(
            @Schema(description = "Milestone URL") String url,
            @Schema(description = "Milestone HTML URL") String htmlUrl,
            @Schema(description = "Milestone labels URL") String labelsUrl,
            @Schema(description = "Milestone ID") Long id,
            @Schema(description = "Milestone node ID") String nodeId,
            @Schema(description = "Milestone number") int number,
            @Schema(description = "Milestone state") String state,
            @Schema(description = "Milestone title") String title,
            @Schema(description = "Milestone description") String description,
            @Schema(description = "Milestone creator") User creator,
            @Schema(description = "Milestone open issues count") int openIssues,
            @Schema(description = "Milestone closed issues count") int closedIssues,
            @Schema(description = "Milestone creation timestamp") String createdAt,
            @Schema(description = "Milestone last updated timestamp") String updatedAt,
            @Schema(description = "Milestone closed timestamp") String closedAt,
            @Schema(description = "Milestone due on timestamp") String dueOn
    ) {
    }

    @Schema(description = "Links related to the pull request")
    public record Links(
            @Schema(description = "Link to the pull request itself") Link self,
            @Schema(description = "Link to the HTML version of the pull request") Link html,
            @Schema(description = "Link to the issue related to the pull request") Link issue,
            @Schema(description = "Link to the comments on the pull request") Link comments,
            @Schema(description = "Link to the review comments on the pull request") Link reviewComments,
            @Schema(description = "Link to the review comment on the pull request") Link reviewComment,
            @Schema(description = "Link to the commits in the pull request") Link commits,
            @Schema(description = "Link to the statuses of the pull request") Link statuses
    ) {
    }

    @Schema(description = "Link details")
    public record Link(
            @Schema(description = "URL of the link") String href
    ) {
    }

    @Schema(description = "Auto merge details")
    public record AutoMerge(
            @Schema(description = "The enabled flag for auto merge") boolean enabled,
            @Schema(description = "The strategy for auto merge") String strategy
    ) {
    }
}
