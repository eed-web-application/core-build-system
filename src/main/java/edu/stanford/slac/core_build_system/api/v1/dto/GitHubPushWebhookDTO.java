package edu.stanford.slac.core_build_system.api.v1.dto;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.Map;

@Schema(description = "DTO representing a GitHub push webhook event")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public record GitHubPushWebhookDTO(
        @Schema(description = "The type of event") String event,
        @Schema(description = "The payload of the push event") Payload payload
) {
    @Schema(description = "Payload containing details about the push event")
    public record Payload(
            @Schema(description = "The Git reference of the push") String ref,
            @Schema(description = "The commit SHA before the push") String before,
            @Schema(description = "The commit SHA after the push") String after,
            @Schema(description = "The repository details") Repository repository,
            @Schema(description = "The pusher details") Pusher pusher,
            @Schema(description = "The organization details") Organization organization,
            @Schema(description = "The sender details") Sender sender,
            @Schema(description = "Flag indicating if the branch was created") boolean created,
            @Schema(description = "Flag indicating if the branch was deleted") boolean deleted,
            @Schema(description = "Flag indicating if the branch was force pushed") boolean forced,
            @Schema(description = "The base reference, if any") String baseRef,
            @Schema(description = "The URL to compare changes") String compare,
            @Schema(description = "List of commits in the push") List<Commit> commits,
            @Schema(description = "Details of the head commit") Commit headCommit
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
            @Schema(description = "Creation timestamp of the repository") long createdAt,
            @Schema(description = "Last updated timestamp of the repository") String updatedAt,
            @Schema(description = "Last pushed timestamp of the repository") long pushedAt,
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
                @Schema(description = "Owner name") String name,
                @Schema(description = "Owner email") String email,
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

    @Schema(description = "Pusher details")
    public record Pusher(
            @Schema(description = "Pusher name") String name,
            @Schema(description = "Pusher email") String email
    ) {
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

    @Schema(description = "Commit details")
    public record Commit(
            @Schema(description = "Commit ID") String id,
            @Schema(description = "Tree ID of the commit") String treeId,
            @Schema(description = "Indicates if the commit is distinct") boolean distinct,
            @Schema(description = "Commit message") String message,
            @Schema(description = "Commit timestamp") String timestamp,
            @Schema(description = "Commit URL") String url,
            @Schema(description = "Author details of the commit") Author author,
            @Schema(description = "Committer details of the commit") Committer committer,
            @Schema(description = "List of files added in the commit") List<String> added,
            @Schema(description = "List of files removed in the commit") List<String> removed,
            @Schema(description = "List of files modified in the commit") List<String> modified
    ) {
        @Schema(description = "Author details of the commit")
        public record Author(
                @Schema(description = "Author name") String name,
                @Schema(description = "Author email") String email,
                @Schema(description = "Author username") String username
        ) {
        }

        @Schema(description = "Committer details of the commit")
        public record Committer(
                @Schema(description = "Committer name") String name,
                @Schema(description = "Committer email") String email,
                @Schema(description = "Committer username") String username
        ) {
        }
    }
}
