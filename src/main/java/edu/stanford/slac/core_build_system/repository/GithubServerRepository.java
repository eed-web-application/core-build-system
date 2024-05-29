package edu.stanford.slac.core_build_system.repository;

import com.google.common.io.Files;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SignatureAlgorithm;
import org.kohsuke.github.GHAppInstallation;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Date;

@Repository
public class GithubServerRepository {
    @Value("${edu.stanford.slac.core-build-system.github-app-id}")
    private String githubId;
    @Value("${edu.stanford.slac.core-build-system.github-app-installation-id}")
    private long githubInstallationId;
    @Value("${edu.stanford.slac.core-build-system.secret-key}")
    private String githubSecretKey;
    private long ttMsec = 60000;
    private PrivateKey getKey() throws Exception {
        byte[] keyBytes = githubSecretKey.getBytes();

        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
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
                .issuer(githubId)
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

    private GHAppInstallation getInstallation() throws Exception {
        GitHub gitHubApp = new GitHubBuilder().withJwtToken(createJWT()).build();
        return gitHubApp.getApp().getInstallationById(githubInstallationId);
    }
}
