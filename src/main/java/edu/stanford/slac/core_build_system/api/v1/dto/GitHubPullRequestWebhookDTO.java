package edu.stanford.slac.core_build_system.api.v1.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public record GitHubPullRequestWebhookDTO(
        @Schema(description = "Action performed on the pull request") @JsonProperty("action") String action,
        @Schema(description = "Number of the pull request") @JsonProperty("number") int number,
        @Schema(description = "Pull request details") @JsonProperty("pull_request") PullRequest pullRequest,
        @Schema(description = "Commit SHA before the push") @JsonProperty("before") String before,
        @Schema(description = "Commit SHA after the push") @JsonProperty("after") String after,
        @Schema(description = "Repository details") @JsonProperty("repository") Repository repository,
        @Schema(description = "Organization details") @JsonProperty("organization") Organization organization,
        @Schema(description = "Sender details") @JsonProperty("sender") User sender
) {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record PullRequest(
            @Schema(description = "URL of the pull request") @JsonProperty("url") String url,
            @Schema(description = "ID of the pull request") @JsonProperty("id") long id,
            @Schema(description = "Node ID of the pull request") @JsonProperty("node_id") String nodeId,
            @Schema(description = "HTML URL of the pull request") @JsonProperty("html_url") String htmlUrl,
            @Schema(description = "State of the pull request") @JsonProperty("state") String state,
            @Schema(description = "Title of the pull request") @JsonProperty("title") String title,
            @Schema(description = "User details") @JsonProperty("user") User user,
            @Schema(description = "Branch details for head") @JsonProperty("head") Branch head,
            @Schema(description = "Branch details for base") @JsonProperty("base") Branch base
    ) {}

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Branch(
            @Schema(description = "Label of the branch") @JsonProperty("label") String label,
            @Schema(description = "Reference of the branch") @JsonProperty("ref") String ref,
            @Schema(description = "SHA of the branch") @JsonProperty("sha") String sha,
            @Schema(description = "User associated with the branch") @JsonProperty("user") User user,
            @Schema(description = "Repository details") @JsonProperty("repo") Repository repo
    ) {}

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Repository(
            @Schema(description = "ID of the repository") @JsonProperty("id") long id,
            @Schema(description = "Node ID of the repository") @JsonProperty("node_id") String nodeId,
            @Schema(description = "Name of the repository") @JsonProperty("name") String name,
            @Schema(description = "Full name of the repository") @JsonProperty("full_name") String fullName,
            @Schema(description = "Indicates if the repository is private") @JsonProperty("private") boolean isPrivate,
            @Schema(description = "Owner of the repository") @JsonProperty("owner") User owner,
            @Schema(description = "HTML URL of the repository") @JsonProperty("html_url") String htmlUrl,
            @Schema(description = "Description of the repository") @JsonProperty("description") String description,
            @Schema(description = "Indicates if the repository is a fork") @JsonProperty("fork") boolean fork,
            @Schema(description = "API URL of the repository") @JsonProperty("url") String url,
            @Schema(description = "Creation date of the repository") @JsonProperty("created_at") String createdAt,
            @Schema(description = "Last update date of the repository") @JsonProperty("updated_at") String updatedAt,
            @Schema(description = "Push date of the repository") @JsonProperty("pushed_at") String pushedAt,
            @Schema(description = "Git URL of the repository") @JsonProperty("git_url") String gitUrl,
            @Schema(description = "SSH URL of the repository") @JsonProperty("ssh_url") String sshUrl,
            @Schema(description = "Clone URL of the repository") @JsonProperty("clone_url") String cloneUrl,
            @Schema(description = "SVN URL of the repository") @JsonProperty("svn_url") String svnUrl,
            @Schema(description = "Homepage of the repository") @JsonProperty("homepage") String homepage,
            @Schema(description = "Size of the repository") @JsonProperty("size") int size,
            @Schema(description = "Stargazers count of the repository") @JsonProperty("stargazers_count") int stargazersCount,
            @Schema(description = "Watchers count of the repository") @JsonProperty("watchers_count") int watchersCount,
            @Schema(description = "Primary language of the repository") @JsonProperty("language") String language,
            @Schema(description = "Indicates if the repository has issues") @JsonProperty("has_issues") boolean hasIssues,
            @Schema(description = "Indicates if the repository has projects") @JsonProperty("has_projects") boolean hasProjects,
            @Schema(description = "Indicates if the repository has downloads") @JsonProperty("has_downloads") boolean hasDownloads,
            @Schema(description = "Indicates if the repository has a wiki") @JsonProperty("has_wiki") boolean hasWiki,
            @Schema(description = "Indicates if the repository has pages") @JsonProperty("has_pages") boolean hasPages,
            @Schema(description = "Forks count of the repository") @JsonProperty("forks_count") int forksCount,
            @Schema(description = "Open issues count of the repository") @JsonProperty("open_issues_count") int openIssuesCount,
            @Schema(description = "Watchers count of the repository") @JsonProperty("watchers") int watchers,
            @Schema(description = "Default branch of the repository") @JsonProperty("default_branch") String defaultBranch
    ) {}

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Organization(
            @Schema(description = "Login name of the organization") @JsonProperty("login") String login,
            @Schema(description = "ID of the organization") @JsonProperty("id") long id,
            @Schema(description = "Node ID of the organization") @JsonProperty("node_id") String nodeId,
            @Schema(description = "URL of the organization") @JsonProperty("url") String url,
            @Schema(description = "Repositories URL of the organization") @JsonProperty("repos_url") String reposUrl,
            @Schema(description = "Events URL of the organization") @JsonProperty("events_url") String eventsUrl,
            @Schema(description = "Hooks URL of the organization") @JsonProperty("hooks_url") String hooksUrl,
            @Schema(description = "Issues URL of the organization") @JsonProperty("issues_url") String issuesUrl,
            @Schema(description = "Members URL of the organization") @JsonProperty("members_url") String membersUrl,
            @Schema(description = "Public members URL of the organization") @JsonProperty("public_members_url") String publicMembersUrl,
            @Schema(description = "Avatar URL of the organization") @JsonProperty("avatar_url") String avatarUrl,
            @Schema(description = "Description of the organization") @JsonProperty("description") String description
    ) {}

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record User(
            @Schema(description = "Login name of the user") @JsonProperty("login") String login,
            @Schema(description = "ID of the user") @JsonProperty("id") long id,
            @Schema(description = "Node ID of the user") @JsonProperty("node_id") String nodeId,
            @Schema(description = "Avatar URL of the user") @JsonProperty("avatar_url") String avatarUrl,
            @Schema(description = "Gravatar ID of the user") @JsonProperty("gravatar_id") String gravatarId,
            @Schema(description = "URL of the user") @JsonProperty("url") String url,
            @Schema(description = "HTML URL of the user") @JsonProperty("html_url") String htmlUrl,
            @Schema(description = "Followers URL of the user") @JsonProperty("followers_url") String followersUrl,
            @Schema(description = "Following URL of the user") @JsonProperty("following_url") String followingUrl,
            @Schema(description = "Gists URL of the user") @JsonProperty("gists_url") String gistsUrl,
            @Schema(description = "Starred URL of the user") @JsonProperty("starred_url") String starredUrl,
            @Schema(description = "Subscriptions URL of the user") @JsonProperty("subscriptions_url") String subscriptionsUrl,
            @Schema(description = "Organizations URL of the user") @JsonProperty("organizations_url") String organizationsUrl,
            @Schema(description = "Repositories URL of the user") @JsonProperty("repos_url") String reposUrl,
            @Schema(description = "Events URL of the user") @JsonProperty("events_url") String eventsUrl,
            @Schema(description = "Received events URL of the user") @JsonProperty("received_events_url") String receivedEventsUrl,
            @Schema(description = "Type of the user") @JsonProperty("type") String type,
            @Schema(description = "Indicates if the user is a site admin") @JsonProperty("site_admin") boolean siteAdmin
    ) {}
}

