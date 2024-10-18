package edu.stanford.slac.core_build_system.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.stanford.slac.core_build_system.api.v1.dto.ComponentDTO;
import edu.stanford.slac.core_build_system.api.v1.dto.GitHubPullRequestWebhookDTO;
import edu.stanford.slac.core_build_system.api.v1.dto.GitHubPushWebhookDTO;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

import static edu.stanford.slac.ad.eed.baselib.exception.Utility.wrapCatch;

@Log4j2
@Service
@AllArgsConstructor
public class GithubEventService {
    ObjectMapper objectMapper;
    ComponentService componentService;
    ComponentBuildService componentBuildService;
    /**
     * Manage the push event from github
     *
     * @param receivedSignature the signature received from the webhook
     * @param payload           the payload received from the webhook
     * @throws JsonProcessingException
     */
    public void managePushEvent(String receivedSignature, String payload) throws JsonProcessingException {
        ComponentDTO componentDTO = null;
        GitHubPushWebhookDTO githubPushEventPayload = objectMapper.readValue(payload, GitHubPushWebhookDTO.class);
        try {
            componentDTO = wrapCatch(
                    () -> componentService.findComponentByProjectUrl(
                            List.of(
                                    githubPushEventPayload.repository().gitUrl(),
                                    githubPushEventPayload.repository().sshUrl(),
                                    githubPushEventPayload.repository().cloneUrl()
                            )
                    ),
                    -1
            );
        } catch (Throwable e) {
            log.error(
                    "[GH push event for {}] Error finding component {}",
                    githubPushEventPayload.repository().gitUrl(),
                    e.getMessage()
            );
        }

        if (componentDTO == null) {
            return;
        }

        // verify signature
        if (
                !verifySignature(componentDTO.componentToken(), payload, receivedSignature)
        ) {
            log.error("[GH push event for {}] Signature verification failed", githubPushEventPayload.repository().gitUrl());
        }

        log.info("[GH push event for {}] Signature verification passed", githubPushEventPayload.repository().gitUrl());
    }

    /**
     * Manage the PR event from github
     *
     * @param receivedSignature the signature received from the webhook
     * @param payload           the payload received from the webhook
     * @throws JsonProcessingException
     */
    @Transactional
    public void managePREvent(String receivedSignature, String payload) throws JsonProcessingException {
        ComponentDTO componentDTO = null;
        GitHubPullRequestWebhookDTO githubPushEventPayload = objectMapper.readValue(payload, GitHubPullRequestWebhookDTO.class);
        log.info("Received PR {} event for {}", githubPushEventPayload.action(), githubPushEventPayload.repository().gitUrl());
        componentDTO = wrapCatch(
                () -> componentService.findComponentByProjectUrl(
                        List.of(
                                githubPushEventPayload.repository().gitUrl(),
                                githubPushEventPayload.repository().sshUrl(),
                                githubPushEventPayload.repository().cloneUrl()
                        )
                ),
                -1
        );
        log.info("Component found: {}", componentDTO);
        // verify signature
        if (
                !verifySignature(componentDTO.componentToken(), payload, receivedSignature)
        ) {
            log.error("[GH pr event {} for {}] Signature verification failed", githubPushEventPayload.action(), githubPushEventPayload.repository().gitUrl());
        }

        log.info("[GH pr event {} for {}] Signature verification passed", githubPushEventPayload.action(), githubPushEventPayload.repository().gitUrl());
        switch (githubPushEventPayload.action()) {
            case "opened":
            case "closed":
                manageClosed(componentDTO, githubPushEventPayload);
                break;
            case "reopened":
            case "synchronize":
                manageSynchronize(componentDTO, githubPushEventPayload);
                break;
            default:
                log.info("Ignoring PR event {} for  {}", githubPushEventPayload.action(), githubPushEventPayload.action());
                return;
        }
    }

    /**
     * Manage the closed event of the pull request from github
     * closing a pull request need to trigger the build on the base branch(where the branch is merged on)
     * @param componentDTO the component
     * @param githubPushEventPayload the payload received from the webhook
     */
    private void manageClosed(ComponentDTO componentDTO, GitHubPullRequestWebhookDTO githubPushEventPayload) {
        Map<String, String> buildVariables = Map.of(
                "ADBS_BUILD_TYPE", "container"
        );
        // start build on base branch
        log.info("Starting build on base branch {} for PR {}", githubPushEventPayload.pullRequest().base().ref(), githubPushEventPayload.pullRequest().title());
        componentService.setBranchAsMerged(componentDTO.name(), githubPushEventPayload.pullRequest().head().ref());
        componentBuildService.startBuild(componentDTO.name(), githubPushEventPayload.pullRequest().base().ref(), buildVariables);
    }

    /**
     * Manage the synchronize event of the pull request from github
     * the synchronization event need to trigger a build on the head branch
     *
     * @param componentDTO the component
     * @param githubPushEventPayload the payload received from the webhook
     */
    private void manageSynchronize(ComponentDTO componentDTO, GitHubPullRequestWebhookDTO githubPushEventPayload) {
        log.info("Starting build on head branch {} for PR {}", githubPushEventPayload.pullRequest().head().ref(), githubPushEventPayload.pullRequest().title());
        Map<String, String> buildVariables = Map.of(
                "ADBS_BUILD_TYPE", "container"
        );
        componentBuildService.startBuild(componentDTO.name(), githubPushEventPayload.pullRequest().head().ref(),buildVariables);
    }

    /**
     * Verify the signature of the payload
     *
     * @param secret            the secret
     * @param payloadBody       the payload body
     * @param receivedSignature the received signature
     * @return true if the signature is verified, false otherwise
     */
    private boolean verifySignature(String secret, String payloadBody, String receivedSignature) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKeySpec);
            byte[] hash = mac.doFinal(payloadBody.getBytes(StandardCharsets.UTF_8));
            String expectedSignature = "sha256=" + bytesToHex(hash);

            return secureCompare(expectedSignature, receivedSignature);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Failed to verify signature", e);
        }
    }

    /**
     * Securely compare two strings to prevent timing attacks.
     *
     * @param a The first string
     * @param b The second string
     * @return true if the strings are equal, false otherwise
     */
    private boolean secureCompare(String a, String b) {
        if (a.length() != b.length()) {
            return false;
        }

        int result = 0;
        for (int i = 0; i < a.length(); i++) {
            result |= a.charAt(i) ^ b.charAt(i);
        }
        return result == 0;
    }

    /**
     * Convert a byte array to a hexadecimal string.
     *
     * @param bytes The byte array
     * @return The hexadecimal string
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
