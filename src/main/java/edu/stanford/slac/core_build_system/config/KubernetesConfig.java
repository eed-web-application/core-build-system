package edu.stanford.slac.core_build_system.config;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.IOException;

@Configuration
public class KubernetesConfig {

    /**
     * Create a new ApiClient object and set it as the default for all Kubernetes API calls.
     * @return ApiClient
     * @throws IOException
     */
    @Bean
    public KubernetesClient client() throws IOException {
        KubernetesClient client = new KubernetesClientBuilder().build();
        return  client;
    }
}
