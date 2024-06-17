package edu.stanford.slac.core_build_system.utility;

import edu.stanford.slac.core_build_system.repository.KubernetesRepository;
import io.fabric8.kubernetes.client.KubernetesClientException;
import org.springframework.core.io.ClassPathResource;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class KubernetesInit {
    static public void init(KubernetesRepository repository, String buildNamespace) {
        // ensure namespace
        assertDoesNotThrow(() -> repository.ensureNamespace(buildNamespace));
        var resultNamespace = assertDoesNotThrow(() -> repository.existsNamespace(buildNamespace));
        assertThat(resultNamespace).isTrue();
        // ensure persistent volume and claim
        ClassPathResource cpResourcePV = new ClassPathResource("persistent-volume.yaml");
        ClassPathResource cpResourcePVC = new ClassPathResource("persistent-volume-claim.yaml");
        try (InputStream inputStream = new FileInputStream(cpResourcePV.getFile())) {
            repository.createPersistenceVolume(inputStream, buildNamespace);
        } catch (KubernetesClientException e) {
            if (e.getStatus().getReason().compareToIgnoreCase("AlreadyExists") != 0) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try (InputStream inputStream = new FileInputStream(cpResourcePVC.getFile())) {
            repository.createPersistenceVolumeClaim(inputStream, buildNamespace);
        } catch (KubernetesClientException e) {
            if (e.getStatus().getReason().compareToIgnoreCase("AlreadyExists") != 0) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
