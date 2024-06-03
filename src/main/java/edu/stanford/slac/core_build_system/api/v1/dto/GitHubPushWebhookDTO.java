package edu.stanford.slac.core_build_system.api.v1.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.Map;

@Schema(description = "DTO representing a GitHub push webhook event")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public record GitHubPushWebhookDTO(
        @Schema(description = "The type of event") @JsonProperty("event") String event,
        @Schema(description = "The payload of the push event") @JsonProperty("payload") Payload payload
) {
    @Schema(description = "Payload containing details about the push event")
    public record Payload(
            @Schema(description = "The Git reference of the push") @JsonProperty("ref") String ref,
            @Schema(description = "The commit SHA before the push") @JsonProperty("before") String before,
            @Schema(description = "The commit SHA after the push") @JsonProperty("after") String after,
            @Schema(description = "The repository details") @JsonProperty("repository") Repository repository,
            @Schema(description = "The pusher details") @JsonProperty("pusher") Pusher pusher,
            @Schema(description = "The organization details") @JsonProperty("organization") Organization organization,
            @Schema(description = "The sender details") @JsonProperty("sender") Sender sender,
            @Schema(description = "Flag indicating if the branch was created") @JsonProperty("created") boolean created,
            @Schema(description = "Flag indicating if the branch was deleted") @JsonProperty("deleted") boolean deleted,
            @Schema(description = "Flag indicating if the branch was force pushed") @JsonProperty("forced") boolean forced,
            @Schema(description = "The base reference, if any") @JsonProperty("base_ref") String baseRef,
            @Schema(description = "The URL to compare changes") @JsonProperty("compare") String compare,
            @Schema(description = "List of commits in the push") @JsonProperty("commits") List<Commit> commits,
            @Schema(description = "Details of the head commit") @JsonProperty("head_commit") Commit headCommit
    ) {
    }

    @Schema(description = "Repository details")
    public record Repository(
            @Schema(description = "Repository ID") @JsonProperty("id") Long id,
            @Schema(description = "Repository node ID") @JsonProperty("node_id") String nodeId,
            @Schema(description = "Repository name") @JsonProperty("name") String name,
            @Schema(description = "Full name of the repository") @JsonProperty("full_name") String fullName,
            @Schema(description = "Indicates if the repository is private") @JsonProperty("private") boolean isPrivate,
            @Schema(description = "Owner details of the repository") @JsonProperty("owner") Owner owner,
            @Schema(description = "HTML URL of the repository") @JsonProperty("html_url") String htmlUrl,
            @Schema(description = "Description of the repository") @JsonProperty("description") String description,
            @Schema(description = "Indicates if the repository is a fork") @JsonProperty("fork") boolean fork,
            @Schema(description = "API URL of the repository") @JsonProperty("url") String url,
            @Schema(description = "Creation timestamp of the repository") @JsonProperty("created_at") long createdAt,
            @Schema(description = "Last updated timestamp of the repository") @JsonProperty("updated_at") String updatedAt,
            @Schema(description = "Last pushed timestamp of the repository") @JsonProperty("pushed_at") long pushedAt,
            @Schema(description = "Git URL of the repository") @JsonProperty("git_url") String gitUrl,
            @Schema(description = "SSH URL of the repository") @JsonProperty("ssh_url") String sshUrl,
            @Schema(description = "Clone URL of the repository") @JsonProperty("clone_url") String cloneUrl,
            @Schema(description = "SVN URL of the repository") @JsonProperty("svn_url") String svnUrl,
            @Schema(description = "Homepage of the repository") @JsonProperty("homepage") String homepage,
            @Schema(description = "Size of the repository") @JsonProperty("size") int size,
            @Schema(description = "Stargazers count of the repository") @JsonProperty("stargazers_count") int stargazersCount,
            @Schema(description = "Watchers count of the repository") @JsonProperty("watchers_count") int watchersCount,
            @Schema(description = "Programming language of the repository") @JsonProperty("language") String language,
            @Schema(description = "Indicates if the repository has issues enabled") @JsonProperty("has_issues") boolean hasIssues,
            @Schema(description = "Indicates if the repository has projects enabled") @JsonProperty("has_projects") boolean hasProjects,
            @Schema(description = "Indicates if the repository has downloads enabled") @JsonProperty("has_downloads") boolean hasDownloads,
            @Schema(description = "Indicates if the repository has wiki enabled") @JsonProperty("has_wiki") boolean hasWiki,
            @Schema(description = "Indicates if the repository has pages enabled") @JsonProperty("has_pages") boolean hasPages,
            @Schema(description = "Indicates if the repository has discussions enabled") @JsonProperty("has_discussions") boolean hasDiscussions,
            @Schema(description = "Forks count of the repository") @JsonProperty("forks_count") int forksCount,
            @Schema(description = "Mirror URL of the repository") @JsonProperty("mirror_url") String mirrorUrl,
            @Schema(description = "Indicates if the repository is archived") @JsonProperty("archived") boolean archived,
            @Schema(description = "Indicates if the repository is disabled") @JsonProperty("disabled") boolean disabled,
            @Schema(description = "Open issues count of the repository") @JsonProperty("open_issues_count") int openIssuesCount,
            @Schema(description = "License of the repository") @JsonProperty("license") String license,
            @Schema(description = "Indicates if the repository allows forking") @JsonProperty("allow_forking") boolean allowForking,
            @Schema(description = "Indicates if the repository is a template") @JsonProperty("is_template") boolean isTemplate,
            @Schema(description = "Indicates if web commit signoff is required") @JsonProperty("web_commit_signoff_required") boolean webCommitSignoffRequired,
            @Schema(description = "Topics of the repository") @JsonProperty("topics") List<String> topics,
            @Schema(description = "Visibility of the repository") @JsonProperty("visibility") String visibility,
            @Schema(description = "Forks count of the repository") @JsonProperty("forks") int forks,
            @Schema(description = "Open issues count of the repository") @JsonProperty("open_issues") int openIssues,
            @Schema(description = "Watchers count of the repository") @JsonProperty("watchers") int watchers,
            @Schema(description = "Default branch of the repository") @JsonProperty("default_branch") String defaultBranch,
            @Schema(description = "Stargazers count of the repository") @JsonProperty("stargazers") int stargazers,
            @Schema(description = "Master branch of the repository") @JsonProperty("master_branch") String masterBranch,
            @Schema(description = "Organization of the repository") @JsonProperty("organization") String organization,
            @Schema(description = "Custom properties of the repository") @JsonProperty("custom_properties") Map<String, Object> customProperties
    ) {
        @Schema(description = "Owner details of the repository")
        public record Owner(
                @Schema(description = "Owner name") @JsonProperty("name") String name,
                @Schema(description = "Owner email") @JsonProperty("email") String email,
                @Schema(description = "Owner login") @JsonProperty("login") String login,
                @Schema(description = "Owner ID") @JsonProperty("id") Long id,
                @Schema(description = "Owner node ID") @JsonProperty("node_id") String nodeId,
                @Schema(description = "Owner avatar URL") @JsonProperty("avatar_url") String avatarUrl,
                @Schema(description = "Owner gravatar ID") @JsonProperty("gravatar_id") String gravatarId,
                @Schema(description = "Owner API URL") @JsonProperty("url") String url,
                @Schema(description = "Owner HTML URL") @JsonProperty("html_url") String htmlUrl,
                @Schema(description = "Owner followers URL") @JsonProperty("followers_url") String followersUrl,
                @Schema(description = "Owner following URL") @JsonProperty("following_url") String followingUrl,
                @Schema(description = "Owner gists URL") @JsonProperty("gists_url") String gistsUrl,
                @Schema(description = "Owner starred URL") @JsonProperty("starred_url") String starredUrl,
                @Schema(description = "Owner subscriptions URL") @JsonProperty("subscriptions_url") String subscriptionsUrl,
                @Schema(description = "Owner organizations URL") @JsonProperty("organizations_url") String organizationsUrl,
                @Schema(description = "Owner repos URL") @JsonProperty("repos_url") String reposUrl,
                @Schema(description = "Owner events URL") @JsonProperty("events_url") String eventsUrl,
                @Schema(description = "Owner received events URL") @JsonProperty("received_events_url") String receivedEventsUrl,
                @Schema(description = "Owner type") @JsonProperty("type") String type,
                @Schema(description = "Indicates if the owner is a site admin") @JsonProperty("site_admin") boolean siteAdmin
        ) {
        }
    }

    @Schema(description = "Pusher details")
    public record Pusher(
            @Schema(description = "Pusher name") @JsonProperty("name") String name,
            @Schema(description = "Pusher email") @JsonProperty("email") String email
    ) {
    }

    @Schema(description = "Organization details")
    public record Organization(
            @Schema(description = "Organization login") @JsonProperty("login") String login,
            @Schema(description = "Organization ID") @JsonProperty("id") Long id,
            @Schema(description = "Organization node ID") @JsonProperty("node_id") String nodeId,
            @Schema(description = "Organization API URL") @JsonProperty("url") String url,
            @Schema(description = "Organization repos URL") @JsonProperty("repos_url") String reposUrl,
            @Schema(description = "Organization events URL") @JsonProperty("events_url") String eventsUrl,
            @Schema(description = "Organization hooks URL") @JsonProperty("hooks_url") String hooksUrl,
            @Schema(description = "Organization issues URL") @JsonProperty("issues_url") String issuesUrl,
            @Schema(description = "Organization members URL") @JsonProperty("members_url") String membersUrl,
            @Schema(description = "Organization public members URL") @JsonProperty("public_members_url") String publicMembersUrl,
            @Schema(description = "Organization avatar URL") @JsonProperty("avatar_url") String avatarUrl,
            @Schema(description = "Organization description") @JsonProperty("description") String description
    ) {
    }

    @Schema(description = "Sender details")
    public record Sender(
            @Schema(description = "Sender login") @JsonProperty("login") String login,
            @Schema(description = "Sender ID") @JsonProperty("id") Long id,
            @Schema(description = "Sender node ID") @JsonProperty("node_id") String nodeId,
            @Schema(description = "Sender avatar URL") @JsonProperty("avatar_url") String avatarUrl,
            @Schema(description = "Sender gravatar ID") @JsonProperty("gravatar_id") String gravatarId,
            @Schema(description = "Sender API URL") @JsonProperty("url") String url,
            @Schema(description = "Sender HTML URL") @JsonProperty("html_url") String htmlUrl,
            @Schema(description = "Sender followers URL") @JsonProperty("followers_url") String followersUrl,
            @Schema(description = "Sender following URL") @JsonProperty("following_url") String followingUrl,
            @Schema(description = "Sender gists URL") @JsonProperty("gists_url") String gistsUrl,
            @Schema(description = "Sender starred URL") @JsonProperty("starred_url") String starredUrl,
            @Schema(description = "Sender subscriptions URL") @JsonProperty("subscriptions_url") String subscriptionsUrl,
            @Schema(description = "Sender organizations URL") @JsonProperty("organizations_url") String organizationsUrl,
            @Schema(description = "Sender repos URL") @JsonProperty("repos_url") String reposUrl,
            @Schema(description = "Sender events URL") @JsonProperty("events_url") String eventsUrl,
            @Schema(description = "Sender received events URL") @JsonProperty("received_events_url") String receivedEventsUrl,
            @Schema(description = "Sender type") @JsonProperty("type") String type,
            @Schema(description = "Indicates if the sender is a site admin") @JsonProperty("site_admin") boolean siteAdmin
    ) {
    }

    @Schema(description = "Commit details")
    public record Commit(
            @Schema(description = "Commit ID") @JsonProperty("id") String id,
            @Schema(description = "Tree ID of the commit") @JsonProperty("tree_id") String treeId,
            @Schema(description = "Indicates if the commit is distinct") @JsonProperty("distinct") boolean distinct,
            @Schema(description = "Commit message") @JsonProperty("message") String message,
            @Schema(description = "Commit timestamp") @JsonProperty("timestamp") String timestamp,
            @Schema(description = "Commit URL") @JsonProperty("url") String url,
            @Schema(description = "Author details of the commit") @JsonProperty("author") Author author,
            @Schema(description = "Committer details of the commit") @JsonProperty("committer") Committer committer,
            @Schema(description = "List of files added in the commit") @JsonProperty("added") List<String> added,
            @Schema(description = "List of files removed in the commit") @JsonProperty("removed") List<String> removed,
            @Schema(description = "List of files modified in the commit") @JsonProperty("modified") List<String> modified
    ) {
        @Schema(description = "Author details of the commit")
        public record Author(
                @Schema(description = "Author name") @JsonProperty("name") String name,
                @Schema(description = "Author email") @JsonProperty("email") String email,
                @Schema(description = "Author username") @JsonProperty("username") String username
        ) {
        }

        @Schema(description = "Committer details of the commit")
        public record Committer(
                @Schema(description = "Committer name") @JsonProperty("name") String name,
                @Schema(description = "Committer email") @JsonProperty("email") String email,
                @Schema(description = "Committer username") @JsonProperty("username") String username
        ) {
        }
    }
}
