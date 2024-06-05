package edu.stanford.slac.core_build_system.repository;

import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.fabric8.kubernetes.client.dsl.PodResource;
import io.fabric8.kubernetes.client.utils.Serialization;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Log4j2
@Repository
@RequiredArgsConstructor
public class KubernetesRepository {
    private final KubernetesClient client;

    public void ensureNamespace(String namespace) {
        var namespaceRes = client
                .namespaces()
                .resource(new NamespaceBuilder().withNewMetadata().withName(namespace).endMetadata().build())
                .get();


        if (namespaceRes == null) {
            var result = client
                    .namespaces()
                    .resource(new NamespaceBuilder().withNewMetadata().withName(namespace).endMetadata().build())
                    .create();
            log.info("Namespace created: {}", result.getMetadata().getName());
        }
    }

    /**
     * Check if namespace exists
     * @param namespace namespace name
     * @return true if namespace exists
     */
    public boolean existsNamespace(String namespace) {
        var namespaceRes = client
                .namespaces()
                .resource(new NamespaceBuilder().withNewMetadata().withName(namespace).endMetadata().build())
                .get();
        return namespaceRes != null;
    }

    /**
     * Create a new builder pod
     * @param namespace namespace name
     * @param builderName component name
     * @return the newly created podName
     */
    public String spinUpBuildPod(String namespace, String builderName) {
        Pod result = null;
        ClassPathResource cpResourcePV = new ClassPathResource("pod-builder-template.yaml");
        try (InputStream inputStream = new FileInputStream(cpResourcePV.getFile())) {
            Pod pv = Serialization.unmarshal(inputStream , Pod.class);
            pv.getMetadata().setName("builder-%s".formatted(builderName));
            pv.getMetadata().setNamespace(namespace);
            result = client.resource(pv).create();
            log.info("Pod created: {}", result.getMetadata().getName());
            return result.getMetadata().getName();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get the pod
     * @return the pod
     */
    public PodResource getPod(String namespace, String podName) {
        return client.pods().inNamespace(namespace).withName(podName);
    }

    public List<StatusDetails> deletePod(String namespace, String podName){
        return client.pods().inNamespace(namespace).withName(podName).delete();
    }
}
