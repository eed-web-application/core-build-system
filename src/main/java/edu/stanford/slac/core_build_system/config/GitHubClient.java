package edu.stanford.slac.core_build_system.config;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SignatureAlgorithm;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.kohsuke.github.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

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

@Profile("!test")
@Configuration
@RequiredArgsConstructor
public class GitHubClient {
    private CoreBuildProperties coreBuildProperties;
    private final long ttMsec = 600000;

    @Bean
    public GHInstancer ghInstancer() throws Exception {
        return new GHInstancer(ghAppInstallation());
    }

    @Bean
    public GHAppInstallation ghAppInstallation() throws Exception {
        var gh = new GitHubBuilder().withJwtToken(createJWT()).build();
        return gh.getApp().getInstallationById(coreBuildProperties.getGithubAppInstallationId());
    }

    private PrivateKey getKey() throws Exception {
        if (Security.getProvider("BC") == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
        String keyStr = new String(Base64.getDecoder().decode(coreBuildProperties.getGithubAppPrivateKey()));
        PemReader pemReader = new PemReader(new StringReader(keyStr));
        PemObject pemObject = (PemObject) pemReader.readPemObject();
        byte[] pemContent = pemObject.getContent();

        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(pemContent);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }

    private String createJWT() throws Exception {
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
        if (ttMsec > 0) {
            long expMillis = nowMillis + ttMsec;
            Date exp = new Date(expMillis);
            builder.expiration(exp);
        }

        //Builds the JWT and serializes it to a compact, URL-safe string
        return builder.compact();
    }

    @RequiredArgsConstructor
    static public class GHInstancer {
        private GitHub gitHub = null;
        private volatile Instant tokenExpirationTime;
        private final GHAppInstallation ghAppInstallation;
        /**
         * Get the GitHub client
         *
         * @return GitHub client
         * @throws Exception if there is an error
         */
        public GitHub getClient() throws Exception {
            if (gitHub == null || Instant.now().isAfter(tokenExpirationTime.minusSeconds(300))) {
                GHAppInstallationToken token = ghAppInstallation.createToken().create();
                gitHub = new GitHubBuilder().withAppInstallationToken(token.getToken()).build();
                tokenExpirationTime = token.getExpiresAt().toInstant();
            }
            return gitHub;
        }

        /**
         * Get the GitHub organization
         *
         * @return GitHub organization
         * @throws Exception if there is an error
         */
        public GHOrganization ghOrganization() throws Exception {
            return getClient().getOrganization(ghAppInstallation.getAccount().getLogin());
        }

        /**
         * Get the GitHub credentials provider
         *
         * @return GitHub credentials provider
         * @throws IOException if there is an error
         */
        public UsernamePasswordCredentialsProvider gitCredentialsProvider() throws IOException {
            GHAppInstallationToken instToken = ghAppInstallation.createToken().create();
            return new UsernamePasswordCredentialsProvider(instToken.getToken(), "");
        }


    }
}
