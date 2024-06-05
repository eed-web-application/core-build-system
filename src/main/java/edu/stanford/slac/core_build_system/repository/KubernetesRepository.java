package edu.stanford.slac.core_build_system.repository;

import io.fabric8.kubernetes.api.model.NamespaceBuilder;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Repository;

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

    public void spinUpPod(String namespace, String podName) {
        var result = client.pods().inNamespace("ns1").resource(new PodBuilder().withNewMetadata().withName(podName).endMetadata().build()).create();
        log.info("Pod created: {}", result.getMetadata().getName());
    }

    public Pod getPodDescription() {
        return client.pods().inNamespace("ns1").withName("pod1").get();
    }
}
