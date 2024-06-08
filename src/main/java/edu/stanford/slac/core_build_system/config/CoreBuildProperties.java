package edu.stanford.slac.core_build_system.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationProperties(
        prefix = "edu.stanford.slac.core-build-system"
)
public class CoreBuildProperties {
    private String githubAppId;
    private String githubAppPrivateKey;
    private long githubAppInstallationId;

    private String buildRootScratchDirectory;
    private String artifactRootDirectory;

    private String k8sBuildNamespace;
}
