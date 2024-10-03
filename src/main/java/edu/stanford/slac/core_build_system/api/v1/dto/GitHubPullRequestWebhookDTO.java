package edu.stanford.slac.core_build_system.api.v1.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.Map;

@Schema(description = "DTO representing a GitHub pull request webhook event")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public record GitHubPullRequestWebhookDTO(
        @Schema(description = "Reference of the push") @JsonProperty("ref") String ref,
        @Schema(description = "Commit SHA before the push") @JsonProperty("before") String before,
        @Schema(description = "Commit SHA after the push") @JsonProperty("after") String after,
        @Schema(description = "Repository details") @JsonProperty("repository") Repository repository,
        @Schema(description = "Pusher details") @JsonProperty("pusher") Pusher pusher,
        @Schema(description = "Organization details") @JsonProperty("organization") Organization organization,
        @Schema(description = "Sender details") @JsonProperty("sender") Sender sender,
        @Schema(description = "Indicates if the ref was created") @JsonProperty("created") boolean created,
        @Schema(description = "Indicates if the ref was deleted") @JsonProperty("deleted") boolean deleted,
        @Schema(description = "Indicates if the push was forced") @JsonProperty("forced") boolean forced,
        @Schema(description = "Base reference") @JsonProperty("base_ref") String baseRef,
        @Schema(description = "Compare URL") @JsonProperty("compare") String compare,
        @Schema(description = "List of commits") @JsonProperty("commits") List<Commit> commits,
        @Schema(description = "Head commit") @JsonProperty("head_commit") Commit headCommit
) {

    @Schema(description = "Repository details")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Repository(
            @Schema(description = "Repository ID") @JsonProperty("id") long id,
            @Schema(description = "Repository node ID") @JsonProperty("node_id") String nodeId,
            @Schema(description = "Repository name") @JsonProperty("name") String name,
            @Schema(description = "Full name of the repository") @JsonProperty("full_name") String fullName,
            @Schema(description = "Indicates if the repository is private") @JsonProperty("private") boolean isPrivate,
            @Schema(description = "Owner details") @JsonProperty("owner") Owner owner,
            @Schema(description = "HTML URL of the repository") @JsonProperty("html_url") String htmlUrl,
            @Schema(description = "Description of the repository") @JsonProperty("description") String description,
            @Schema(description = "Indicates if the repository is a fork") @JsonProperty("fork") boolean fork,
            @Schema(description = "API URL of the repository") @JsonProperty("url") String url,
            @Schema(description = "Forks URL") @JsonProperty("forks_url") String forksUrl,
            @Schema(description = "Keys URL") @JsonProperty("keys_url") String keysUrl,
            @Schema(description = "Collaborators URL") @JsonProperty("collaborators_url") String collaboratorsUrl,
            @Schema(description = "Teams URL") @JsonProperty("teams_url") String teamsUrl,
            @Schema(description = "Hooks URL") @JsonProperty("hooks_url") String hooksUrl,
            @Schema(description = "Issue events URL") @JsonProperty("issue_events_url") String issueEventsUrl,
            @Schema(description = "Events URL") @JsonProperty("events_url") String eventsUrl,
            @Schema(description = "Assignees URL") @JsonProperty("assignees_url") String assigneesUrl,
            @Schema(description = "Branches URL") @JsonProperty("branches_url") String branchesUrl,
            @Schema(description = "Tags URL") @JsonProperty("tags_url") String tagsUrl,
            @Schema(description = "Blobs URL") @JsonProperty("blobs_url") String blobsUrl,
            @Schema(description = "Git tags URL") @JsonProperty("git_tags_url") String gitTagsUrl,
            @Schema(description = "Git refs URL") @JsonProperty("git_refs_url") String gitRefsUrl,
            @Schema(description = "Trees URL") @JsonProperty("trees_url") String treesUrl,
            @Schema(description = "Statuses URL") @JsonProperty("statuses_url") String statusesUrl,
            @Schema(description = "Languages URL") @JsonProperty("languages_url") String languagesUrl,
            @Schema(description = "Stargazers URL") @JsonProperty("stargazers_url") String stargazersUrl,
            @Schema(description = "Contributors URL") @JsonProperty("contributors_url") String contributorsUrl,
            @Schema(description = "Subscribers URL") @JsonProperty("subscribers_url") String subscribersUrl,
            @Schema(description = "Subscription URL") @JsonProperty("subscription_url") String subscriptionUrl,
            @Schema(description = "Commits URL") @JsonProperty("commits_url") String commitsUrl,
            @Schema(description = "Git commits URL") @JsonProperty("git_commits_url") String gitCommitsUrl,
            @Schema(description = "Comments URL") @JsonProperty("comments_url") String commentsUrl,
            @Schema(description = "Issue comment URL") @JsonProperty("issue_comment_url") String issueCommentUrl,
            @Schema(description = "Contents URL") @JsonProperty("contents_url") String contentsUrl,
            @Schema(description = "Compare URL") @JsonProperty("compare_url") String compareUrl,
            @Schema(description = "Merges URL") @JsonProperty("merges_url") String mergesUrl,
            @Schema(description = "Archive URL") @JsonProperty("archive_url") String archiveUrl,
            @Schema(description = "Downloads URL") @JsonProperty("downloads_url") String downloadsUrl,
            @Schema(description = "Issues URL") @JsonProperty("issues_url") String issuesUrl,
            @Schema(description = "Pulls URL") @JsonProperty("pulls_url") String pullsUrl,
            @Schema(description = "Milestones URL") @JsonProperty("milestones_url") String milestonesUrl,
            @Schema(description = "Notifications URL") @JsonProperty("notifications_url") String notificationsUrl,
            @Schema(description = "Labels URL") @JsonProperty("labels_url") String labelsUrl,
            @Schema(description = "Releases URL") @JsonProperty("releases_url") String releasesUrl,
            @Schema(description = "Deployments URL") @JsonProperty("deployments_url") String deploymentsUrl,
            @Schema(description = "Creation timestamp") @JsonProperty("created_at") Object createdAt,
            @Schema(description = "Last updated timestamp") @JsonProperty("updated_at") String updatedAt,
            @Schema(description = "Last pushed timestamp") @JsonProperty("pushed_at") Object pushedAt,
            @Schema(description = "Git URL") @JsonProperty("git_url") String gitUrl,
            @Schema(description = "SSH URL") @JsonProperty("ssh_url") String sshUrl,
            @Schema(description = "Clone URL") @JsonProperty("clone_url") String cloneUrl,
            @Schema(description = "SVN URL") @JsonProperty("svn_url") String svnUrl,
            @Schema(description = "Homepage") @JsonProperty("homepage") String homepage,
            @Schema(description = "Repository size") @JsonProperty("size") int size,
            @Schema(description = "Stargazers count") @JsonProperty("stargazers_count") int stargazersCount,
            @Schema(description = "Watchers count") @JsonProperty("watchers_count") int watchersCount,
            @Schema(description = "Programming language") @JsonProperty("language") String language,
            @Schema(description = "Has issues enabled") @JsonProperty("has_issues") boolean hasIssues,
            @Schema(description = "Has projects enabled") @JsonProperty("has_projects") boolean hasProjects,
            @Schema(description = "Has downloads enabled") @JsonProperty("has_downloads") boolean hasDownloads,
            @Schema(description = "Has wiki enabled") @JsonProperty("has_wiki") boolean hasWiki,
            @Schema(description = "Has pages enabled") @JsonProperty("has_pages") boolean hasPages,
            @Schema(description = "Has discussions enabled") @JsonProperty("has_discussions") boolean hasDiscussions,
            @Schema(description = "Forks count") @JsonProperty("forks_count") int forksCount,
            @Schema(description = "Mirror URL") @JsonProperty("mirror_url") String mirrorUrl,
            @Schema(description = "Is archived") @JsonProperty("archived") boolean archived,
            @Schema(description = "Is disabled") @JsonProperty("disabled") boolean disabled,
            @Schema(description = "Open issues count") @JsonProperty("open_issues_count") int openIssuesCount,
            @Schema(description = "License") @JsonProperty("license") String license,
            @Schema(description = "Allow forking") @JsonProperty("allow_forking") boolean allowForking,
            @Schema(description = "Is template") @JsonProperty("is_template") boolean isTemplate,
            @Schema(description = "Web commit signoff required") @JsonProperty("web_commit_signoff_required") boolean webCommitSignoffRequired,
            @Schema(description = "Repository topics") @JsonProperty("topics") List<String> topics,
            @Schema(description = "Visibility") @JsonProperty("visibility") String visibility,
            @Schema(description = "Forks") @JsonProperty("forks") int forks,
            @Schema(description = "Open issues") @JsonProperty("open_issues") int openIssues,
            @Schema(description = "Watchers") @JsonProperty("watchers") int watchers,
            @Schema(description = "Default branch") @JsonProperty("default_branch") String defaultBranch,
            @Schema(description = "Stargazers") @JsonProperty("stargazers") int stargazers,
            @Schema(description = "Master branch") @JsonProperty("master_branch") String masterBranch,
            @Schema(description = "Organization") @JsonProperty("organization") String organization,
            @Schema(description = "Custom properties") @JsonProperty("custom_properties") Map<String, Object> customProperties
    ) {
        @Schema(description = "Owner details")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonIgnoreProperties(ignoreUnknown = true)
        public record Owner(
                @Schema(description = "Owner name") @JsonProperty("name") String name,
                @Schema(description = "Owner email") @JsonProperty("email") String email,
                @Schema(description = "Owner login") @JsonProperty("login") String login,
                @Schema(description = "Owner ID") @JsonProperty("id") long id,
                @Schema(description = "Owner node ID") @JsonProperty("node_id") String nodeId,
                @Schema(description = "Owner avatar URL") @JsonProperty("avatar_url") String avatarUrl,
                @Schema(description = "Owner gravatar ID") @JsonProperty("gravatar_id") String gravatarId,
                @Schema(description = "Owner API URL") @JsonProperty("url") String url,
                @Schema(description = "Owner HTML URL") @JsonProperty("html_url") String htmlUrl,
                @Schema(description = "Followers URL") @JsonProperty("followers_url") String followersUrl,
                @Schema(description = "Following URL") @JsonProperty("following_url") String followingUrl,
                @Schema(description = "Gists URL") @JsonProperty("gists_url") String gistsUrl,
                @Schema(description = "Starred URL") @JsonProperty("starred_url") String starredUrl,
                @Schema(description = "Subscriptions URL") @JsonProperty("subscriptions_url") String subscriptionsUrl,
                @Schema(description = "Organizations URL") @JsonProperty("organizations_url") String organizationsUrl,
                @Schema(description = "Repositories URL") @JsonProperty("repos_url") String reposUrl,
                @Schema(description = "Events URL") @JsonProperty("events_url") String eventsUrl,
                @Schema(description = "Received events URL") @JsonProperty("received_events_url") String receivedEventsUrl,
                @Schema(description = "Owner type") @JsonProperty("type") String type,
                @Schema(description = "Is site admin") @JsonProperty("site_admin") boolean siteAdmin
        ) {
        }
    }

    @Schema(description = "Pusher details")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Pusher(
            @Schema(description = "Pusher name") @JsonProperty("name") String name,
            @Schema(description = "Pusher email") @JsonProperty("email") String email
    ) {
    }

    @Schema(description = "Organization details")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Organization(
            @Schema(description = "Organization login") @JsonProperty("login") String login,
            @Schema(description = "Organization ID") @JsonProperty("id") long id,
            @Schema(description = "Organization node ID") @JsonProperty("node_id") String nodeId,
            @Schema(description = "Organization API URL") @JsonProperty("url") String url,
            @Schema(description = "Repositories URL") @JsonProperty("repos_url") String reposUrl,
            @Schema(description = "Events URL") @JsonProperty("events_url") String eventsUrl,
            @Schema(description = "Hooks URL") @JsonProperty("hooks_url") String hooksUrl,
            @Schema(description = "Issues URL") @JsonProperty("issues_url") String issuesUrl,
            @Schema(description = "Members URL") @JsonProperty("members_url") String membersUrl,
            @Schema(description = "Public members URL") @JsonProperty("public_members_url") String publicMembersUrl,
            @Schema(description = "Avatar URL") @JsonProperty("avatar_url") String avatarUrl,
            @Schema(description = "Description") @JsonProperty("description") String description
    ) {
    }

    @Schema(description = "Sender details")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Sender(
            @Schema(description = "Sender login") @JsonProperty("login") String login,
            @Schema(description = "Sender ID") @JsonProperty("id") long id,
            @Schema(description = "Sender node ID") @JsonProperty("node_id") String nodeId,
            @Schema(description = "Sender avatar URL") @JsonProperty("avatar_url") String avatarUrl,
            @Schema(description = "Sender gravatar ID") @JsonProperty("gravatar_id") String gravatarId,
            @Schema(description = "Sender API URL") @JsonProperty("url") String url,
            @Schema(description = "Sender HTML URL") @JsonProperty("html_url") String htmlUrl,
            @Schema(description = "Followers URL") @JsonProperty("followers_url") String followersUrl,
            @Schema(description = "Following URL") @JsonProperty("following_url") String followingUrl,
            @Schema(description = "Gists URL") @JsonProperty("gists_url") String gistsUrl,
            @Schema(description = "Starred URL") @JsonProperty("starred_url") String starredUrl,
            @Schema(description = "Subscriptions URL") @JsonProperty("subscriptions_url") String subscriptionsUrl,
            @Schema(description = "Organizations URL") @JsonProperty("organizations_url") String organizationsUrl,
            @Schema(description = "Repositories URL") @JsonProperty("repos_url") String reposUrl,
            @Schema(description = "Events URL") @JsonProperty("events_url") String eventsUrl,
            @Schema(description = "Received events URL") @JsonProperty("received_events_url") String receivedEventsUrl,
            @Schema(description = "Sender type") @JsonProperty("type") String type,
            @Schema(description = "Is site admin") @JsonProperty("site_admin") boolean siteAdmin
    ) {
    }

    @Schema(description = "Commit details")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Commit(
            @Schema(description = "Commit ID") @JsonProperty("id") String id,
            @Schema(description = "Tree ID") @JsonProperty("tree_id") String treeId,
            @Schema(description = "Is distinct") @JsonProperty("distinct") boolean distinct,
            @Schema(description = "Commit message") @JsonProperty("message") String message,
            @Schema(description = "Commit timestamp") @JsonProperty("timestamp") String timestamp,
            @Schema(description = "Commit URL") @JsonProperty("url") String url,
            @Schema(description = "Author details") @JsonProperty("author") Author author,
            @Schema(description = "Committer details") @JsonProperty("committer") Committer committer,
            @Schema(description = "Files added") @JsonProperty("added") List<String> added,
            @Schema(description = "Files removed") @JsonProperty("removed") List<String> removed,
            @Schema(description = "Files modified") @JsonProperty("modified") List<String> modified
    ) {
        @Schema(description = "Author details")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonIgnoreProperties(ignoreUnknown = true)
        public record Author(
                @Schema(description = "Author name") @JsonProperty("name") String name,
                @Schema(description = "Author email") @JsonProperty("email") String email,
                @Schema(description = "Author username") @JsonProperty("username") String username
        ) {
        }

        @Schema(description = "Committer details")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonIgnoreProperties(ignoreUnknown = true)
        public record Committer(
                @Schema(description = "Committer name") @JsonProperty("name") String name,
                @Schema(description = "Committer email") @JsonProperty("email") String email,
                @Schema(description = "Committer username") @JsonProperty("username") String username
        ) {
        }
    }
}