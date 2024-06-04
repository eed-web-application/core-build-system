package edu.stanford.slac.core_build_system.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.stanford.slac.core_build_system.api.v1.dto.ComponentDTO;
import edu.stanford.slac.core_build_system.api.v1.dto.GitHubPushWebhookDTO;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import static edu.stanford.slac.ad.eed.baselib.exception.Utility.wrapCatch;

@Log4j2
@Service
@AllArgsConstructor
public class GithubEventService {
    ObjectMapper objectMapper;
    ComponentService componentService;

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
        try{
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
        } catch(Throwable e){
            log.error(
                    "[GH push event for {}] Error finding component {}",
                    githubPushEventPayload.repository().gitUrl(),
                    e.getMessage()
            );
        }

        if(componentDTO == null){
            return;
        }

        // verify signature
        if (
                !verifySignature(componentDTO.componentToken(), payload, receivedSignature)
        ){
            log.error("[GH push event for {}] Signature verification failed", githubPushEventPayload.repository().gitUrl());
        }

        log.info("[GH push event for {}] Signature verification passed", githubPushEventPayload.repository().gitUrl());
    }

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
