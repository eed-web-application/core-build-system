package edu.stanford.slac.core_build_system.config;

import edu.stanford.slac.ad.eed.baselib.exception.ControllerLogicException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.kohsuke.github.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.StringReader;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Security;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;

/**
 * GitHub client configuration
 */
@Log4j2
@Configuration
@RequiredArgsConstructor
public class GitHubClient {
    private final CoreBuildProperties coreBuildProperties;

    @Bean
    public GHInstancer ghInstancer() throws Exception {
        log.info(
                "Creating GHInstancer client for app-id:{}",
                coreBuildProperties.getGithubAppId()
        );
        return new GHInstancer(coreBuildProperties);
    }

    /**
     * GitHub client instance
     */
    @RequiredArgsConstructor
    static public class GHInstancer {
        private final CoreBuildProperties coreBuildProperties;
        private final long jwtTtMsec = 600000; // JWT token valid duration (10 minutes)
        private final long jwtRefreshBufferSec = 300; // Refresh JWT if less than 5 minutes
        private GitHub gitHubApp = null; // Client authenticated with JWT
        private GitHub gitHub = null;
        private String token = null;
        private PrivateKey privateKey = null;
        private volatile Instant jwtExpirationTime;
        private GHAppInstallation ghAppInstallation;
        private String installationToken;
        private Instant installationTokenExpirationTime;
        /**
         * Get the GitHub client
         *
         * @return GitHub client
         * @throws Exception if there is an error
         */
        public GitHub getClient() throws Exception {
            refreshGithubClient();
            return gitHub;
        }

        public GHApp getApplication() throws Exception {
            refreshGithubClient();
            return gitHub.getApp();
        }

        /**
         * Get the GitHub organization
         *
         * @return GitHub organization
         * @throws Exception if there is an error
         */
        public GHOrganization ghOrganization(String organizationName) throws Exception {
            refreshGithubClient();
            return getClient().getOrganization(organizationName);
        }

        /**
         * Get the GitHub credentials provider
         *
         * @return GitHub credentials provider
         * @throws IOException if there is an error
         */
        public UsernamePasswordCredentialsProvider gitCredentialsProvider() throws Exception {
            refreshGithubClient();
            GHAppInstallationToken instToken = ghAppInstallation.createToken().create();
            return new UsernamePasswordCredentialsProvider(instToken.getToken(), "");
        }

        /**
         * Refresh the GitHub client
         *
         * @throws Exception if there is an error
         */
        private void refreshGithubClient() throws Exception {
            if (gitHubApp == null || Instant.now().isAfter(jwtExpirationTime.minusSeconds(jwtRefreshBufferSec))) {
                log.debug("Creating new GitHub client with JWT token for app-id:{}", coreBuildProperties.getGithubAppId());
                gitHubApp = new GitHubBuilder().withJwtToken(getJWT()).build();
                ghAppInstallation = gitHubApp.getApp().getInstallationById(coreBuildProperties.getGithubAppInstallationId());
                log.debug("gitHubApp client created");
            }
            if (gitHub == null || Instant.now().isAfter(installationTokenExpirationTime.minusSeconds(jwtRefreshBufferSec))) {
                log.debug("Creating new GitHub client with installation token for app-id:{}", coreBuildProperties.getGithubAppId());
                GHAppInstallationToken instToken = ghAppInstallation.createToken().create();
                log.debug("Installation token created");
                installationToken = instToken.getToken();
                installationTokenExpirationTime = instToken.getExpiresAt().toInstant();
                log.debug("Create client using installation token");
                gitHub = new GitHubBuilder().withAppInstallationToken(installationToken).build();
                log.debug("gitHub client created");
            }
        }


        /**
         * Get the GitHub app installation
         * The private key never expires, so we can cache it
         * @return GitHub app installation
         * @throws Exception if there is an error
         */
        private PrivateKey getKey() throws Exception {
            if(privateKey == null) {
                if (Security.getProvider("BC") == null) {
                    Security.addProvider(new BouncyCastleProvider());
                }
                var key = coreBuildProperties.getGithubAppPrivateKey();
                if(key == null) {
                    throw ControllerLogicException.builder().errorCode(-1).errorMessage("GitHub app private key is not set").errorDomain("GitHubClient").build();
                }
                log.debug("Creating private key from GitHub app private key");
                String keyStr = new String(Base64.getDecoder().decode(key));
                PemReader pemReader = new PemReader(new StringReader(keyStr));
                PemObject pemObject = (PemObject) pemReader.readPemObject();
                byte[] pemContent = pemObject.getContent();

                PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(pemContent);
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                privateKey = keyFactory.generatePrivate(keySpec);
                log.debug("Private key created");
            }
            return privateKey;
        }

        /**
         * Create a JWT token
         * Jwt is update if it is null or expired
         * @return JWT token
         * @throws Exception if there is an error
         */
        private String getJWT() throws Exception {
            if (token == null || Instant.now().isAfter(jwtExpirationTime.minusSeconds(jwtRefreshBufferSec))){
                log.debug("Creating new JWT token for app-id:{}", coreBuildProperties.getGithubAppId());
                //The JWT signature algorithm we will be using to sign the token
                SignatureAlgorithm signatureAlgorithm = Jwts.SIG.RS256;

                long nowMillis = System.currentTimeMillis();
                Date now = new Date(nowMillis);

                //We will sign our JWT with our private key
                Key signingKey = getKey();

                //Let's set the JWT Claims
                JwtBuilder builder = Jwts.builder()
                        .issuedAt(now)
                        .issuer(coreBuildProperties.getGithubAppId())
                        .signWith(signingKey);

                //if it has been specified, let's add the expiration
                if (jwtTtMsec > 0) {
                    long expMillis = nowMillis + jwtTtMsec;
                    Date exp = new Date(expMillis);
                    builder.expiration(exp);
                }

                //Builds the JWT and serializes it to a compact, URL-safe string
                token = builder.compact();

                // update expiration time
                jwtExpirationTime = Instant.now().plusMillis(jwtTtMsec);
                log.debug("JWT token created");
            }
            return token;
        }
    }
}
