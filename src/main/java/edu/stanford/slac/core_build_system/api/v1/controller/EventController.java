package edu.stanford.slac.core_build_system.api.v1.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.stanford.slac.ad.eed.baselib.api.v1.dto.ApiResultResponse;
import edu.stanford.slac.core_build_system.api.v1.dto.GitHubPingWebhookDTO;
import edu.stanford.slac.core_build_system.api.v1.dto.GitHubPullRequestWebhookDTO;
import edu.stanford.slac.core_build_system.api.v1.dto.GitHubPushWebhookDTO;
import edu.stanford.slac.core_build_system.service.GithubEventService;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RestController
@RequestMapping("/v1/event/gh")
@AllArgsConstructor
@Schema(description = "Api set for the component management")
public class EventController {
    ObjectMapper objectMapper;
    GithubEventService githubEventService;

    @PostMapping("/webhook")
    @ResponseStatus(HttpStatus.OK)
    public ApiResultResponse<String> handlePushEvent(
            @RequestHeader("X-Hub-Signature-256") String signature,
            @RequestHeader("X-GitHub-Event") String event,
            @RequestBody String payload) throws JsonProcessingException {
        if(event.compareToIgnoreCase("push") == 0) {
            log.info("Received push event");
            githubEventService.managePushEvent(signature, payload);
        } else if (event.compareToIgnoreCase("pull_request") == 0) {
            log.info("Received pull request event");
            githubEventService.managePREvent(signature, payload);
        } else if (event.compareToIgnoreCase("ping") == 0) {
            log.info("Received ping event");
            GitHubPingWebhookDTO pingEventPayload = objectMapper.readValue(payload, GitHubPingWebhookDTO.class);
        } else {
            log.error("Event not mapped {}", payload);
        }
        return ApiResultResponse.of("Event received: " + event);
    }
}
