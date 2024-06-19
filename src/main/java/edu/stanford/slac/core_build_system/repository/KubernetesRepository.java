package edu.stanford.slac.core_build_system.repository;

import edu.stanford.slac.core_build_system.model.K8SPodBuilder;
import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.PodResource;
import io.fabric8.kubernetes.client.utils.Serialization;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

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
     *
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
     *
     * @param podBuilder the pod builder
     * @return the newly created podName
     */
    public Pod spinUpBuildPod(K8SPodBuilder podBuilder) {
        Pod result = null;
        Pod newPod = new PodBuilder()
                .withNewMetadata()
                .withNamespace(podBuilder.getNamespace())
                .withName("builder-%s".formatted(podBuilder.getBuilderName().toLowerCase(Locale.ROOT)))
                .endMetadata()
                .withNewSpec()
                .addNewContainer()
                .withName("builder-%s".formatted(podBuilder.getBuilderName().toLowerCase(Locale.ROOT)))
                .withImage(podBuilder.getDockerImage())
                .addNewVolumeMount()
                .withName("data-volume")
                .withMountPath(podBuilder.getMountLocation())
                .endVolumeMount()
                .endContainer()
                .addNewVolume()
                .withName("data-volume")
                .withNewPersistentVolumeClaim()
                .withClaimName("build-scratch-volume-claim")
                .endPersistentVolumeClaim()
                .endVolume()
                .withRestartPolicy("Never")
                .endSpec()
                .build();

        if (podBuilder.getBuildCommand() != null) {
            newPod.getSpec().getContainers().getFirst().setCommand(podBuilder.getBuildCommand());
        }
        if (podBuilder.getBuildArgs() != null) {
            newPod.getSpec().getContainers().getFirst().setArgs(podBuilder.getBuildArgs());
        }

        if (podBuilder.getEnvVars() != null) {
            podBuilder.getEnvVars().forEach((k, v) -> newPod.getSpec().getContainers().getFirst().getEnv().add(new EnvVarBuilder().withName(k).withValue(v).build()));
        }
        result = client.resource(newPod).create();
        log.info("Pod created: {}", result.getMetadata().getName());
        return result;

    }

    /**
     * Get the pod
     *
     * @return the pod
     */
    public PodResource getPod(String namespace, String podName) {
        return client.pods().inNamespace(namespace).withName(podName);
    }

    /**
     * Delete the pod
     *
     * @return the pod
     */
    public List<StatusDetails> deletePod(String namespace, String podName) {
        var pod = client.pods().inNamespace(namespace).withName(podName);
        if (pod == null) {
            return Collections.emptyList();
        }
        return pod.delete();
    }

    /**
     * Create a new persistent volume
     *
     * @param pvResourceInputstream input stream of the persistent volume resource
     */
    public void createPersistenceVolume(InputStream pvResourceInputstream, String namespace) {
        PersistentVolume pv = Serialization.unmarshal(pvResourceInputstream, PersistentVolume.class);
        pv.getMetadata().setNamespace(namespace);
        var result = client.resource(pv).create();
        log.info("PV created: {}", result.getMetadata().getName());
    }

    public void createPersistenceVolumeClaim(InputStream pvcResourceInputstream, String namespace) {
        PersistentVolumeClaim pvc = Serialization.unmarshal(pvcResourceInputstream, PersistentVolumeClaim.class);
        pvc.getMetadata().setNamespace(namespace);
        var result = client.resource(pvc).create();
        log.info("PVC created: {}", result.getMetadata().getName());
    }
}
