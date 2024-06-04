package edu.stanford.slac.core_build_system.api.v1.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.Map;

@Schema(description = "DTO representing a GitHub ping webhook event")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public record GitHubPingWebhookDTO(
        @Schema(description = "Zen message from GitHub") String zen,
        @Schema(description = "ID of the webhook") long hookId,
        @Schema(description = "Details of the webhook configuration") Hook hook,
        @Schema(description = "The repository details") Repository repository,
        @Schema(description = "The sender details") Sender sender
) {
    @Schema(description = "Hook configuration details")
    public record Hook(
            @Schema(description = "Type of the hook") String type,
            @Schema(description = "ID of the hook") long id,
            @Schema(description = "Name of the hook") String name,
            @Schema(description = "Indicates if the hook is active") boolean active,
            @Schema(description = "Events that trigger the hook") List<String> events,
            @Schema(description = "Configuration details of the hook") Config config,
            @Schema(description = "Last updated timestamp of the hook") String updatedAt,
            @Schema(description = "Creation timestamp of the hook") String createdAt,
            @Schema(description = "API URL of the hook") String url,
            @Schema(description = "Test URL of the hook") String testUrl,
            @Schema(description = "Ping URL of the hook") String pingUrl,
            @Schema(description = "Deliveries URL of the hook") String deliveriesUrl,
            @Schema(description = "Last response of the hook") LastResponse lastResponse
    ) {
    }

    @Schema(description = "Hook configuration")
    public record Config(
            @Schema(description = "Content type of the hook") String contentType,
            @Schema(description = "Insecure SSL flag") String insecureSsl,
            @Schema(description = "Secret for the hook") String secret,
            @Schema(description = "URL of the hook") String url
    ) {
    }

    @Schema(description = "Last response details")
    public record LastResponse(
            @Schema(description = "Response code") Integer code,
            @Schema(description = "Response status") String status,
            @Schema(description = "Response message") String message
    ) {
    }

    @Schema(description = "Repository details")
    public record Repository(
            @Schema(description = "Repository ID") long id,
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
            @Schema(description = "Master branch of the repository") String masterBranch,
            @Schema(description = "Organization of the repository") String organization,
            @Schema(description = "Custom properties of the repository") Map<String, Object> customProperties
    ) {
        @Schema(description = "Owner details of the repository")
        public record Owner(
                @Schema(description = "Owner login") String login,
                @Schema(description = "Owner ID") long id,
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

    @Schema(description = "Sender details")
    public record Sender(
            @Schema(description = "Sender login") String login,
            @Schema(description = "Sender ID") long id,
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
}
