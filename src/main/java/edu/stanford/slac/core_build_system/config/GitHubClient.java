package edu.stanford.slac.core_build_system.config;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SignatureAlgorithm;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMReader;
import org.kohsuke.github.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.StringReader;
import java.security.Key;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.Security;
import java.util.Base64;
import java.util.Date;

@Configuration
public class GitHubClient {
    @Value("${edu.stanford.slac.core-build-system.github-app-id}")
    private String githubId;
    @Value("${edu.stanford.slac.core-build-system.github-app-installation-id}")
    private long githubInstallationId;
    @Value("${edu.stanford.slac.core-build-system.github-app-private-key}")
    private String githubSecretKey;
    private long ttMsec = 600000;
    private PrivateKey getKey() throws Exception {
        if (Security.getProvider("BC") == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
        String keyStr = new String(Base64.getDecoder().decode(githubSecretKey));
        PEMReader pemReader = new PEMReader(new StringReader(keyStr));
        KeyPair keyPair = (KeyPair) pemReader.readObject();
        PrivateKey key = keyPair.getPrivate();
        return key;
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

    @Bean
    public GHOrganization ghOrganization() throws Exception {
        return gitHub().getOrganization(ghAppInstallation().getAccount().getLogin());
    }

    @Bean
    public GitHub gitHub() throws Exception {
        GHAppInstallationToken instToken = ghAppInstallation().createToken().create();
        String appToken = instToken.getToken();
        return new GitHubBuilder().withAppInstallationToken(appToken).build();
    }

    @Bean
    public GHAppInstallation ghAppInstallation() throws Exception {
        var gh = new GitHubBuilder().withJwtToken(createJWT()).build();
        return gh.getApp().getInstallationById(githubInstallationId);
    }
}
