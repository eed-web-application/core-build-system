package edu.stanford.slac.core_build_system.service;

import edu.stanford.slac.core_build_system.api.v1.dto.GitHubPushWebhookDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
@AllArgsConstructor
public class GithubEventService {

    /**
     * Validate the signature of the webhook
     *
     * @param signature     The signature of the webhook
     * @param repositoryDTO The repository information
     *                      <p>
     * use one of these urls to find the component that manage the project
     * git_url: "git://github.com/ad-build-test/repository-name.git",
     * ssh_url: "git@github.com:ad-build-test/repository-name.git",
     * clone_url: "https://github.com/ad-build-test/repository-name.git",
     */
    void validateSignature(String signature, GitHubPushWebhookDTO.Repository repositoryDTO) {
        System.out.println("Validating signature: " + signature);

        //repositoryDTO.cloneUrl()
        //repositoryDTO.gitUrl();
        //repositoryDTO.sshUrl();
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
