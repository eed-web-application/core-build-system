package edu.stanford.slac.core_build_system.repository;

import io.fabric8.kubernetes.api.model.PersistentVolume;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaim;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.fabric8.kubernetes.client.utils.Serialization;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@AutoConfigureMockMvc
@SpringBootTest()
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
@ActiveProfiles({"test"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class KubernetesRepositoryTest {
    private static final Logger LOG = LoggerFactory.getLogger(KubernetesRepositoryTest.class);
    @Autowired
    KubernetesClient client;
    @Autowired
    KubernetesRepository repository;
    private final String buildNamespace = "build-ns";
    @BeforeAll
    public void initResources() {
        // ensure namespace
        assertDoesNotThrow(()->repository.ensureNamespace(buildNamespace));
        var resultNamespace = assertDoesNotThrow(()->repository.existsNamespace(buildNamespace));
        assertThat(resultNamespace).isTrue();
        // ensure persistent volume and claim
        ClassPathResource cpResourcePV = new ClassPathResource("persistent-volume.yaml");
        ClassPathResource cpResourcePVC = new ClassPathResource("persistent-volume-claim.yaml");
        try (InputStream inputStream = new FileInputStream(cpResourcePV.getFile())) {
            PersistentVolume pv = Serialization.unmarshal(inputStream , PersistentVolume.class);
            var result = client.resource(pv).create();
        } catch (KubernetesClientException e) {
            if(e.getStatus().getReason().compareToIgnoreCase("AlreadyExists") != 0){
                e.printStackTrace();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try (InputStream inputStream = new FileInputStream(cpResourcePVC.getFile())) {
            PersistentVolumeClaim pvc = Serialization.unmarshal(inputStream , PersistentVolumeClaim.class);
            pvc.getMetadata().setNamespace(buildNamespace);
            var result = client.resource(pvc).create();
            System.out.println("PV created: " + result.getMetadata().getName());
        } catch (KubernetesClientException e) {
            if(e.getStatus().getReason().compareToIgnoreCase("AlreadyExists") != 0){
                e.printStackTrace();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Test
    public void createPodAndWaitForTermination() {
        LOG.info(() -> "Creating a pod and waiting for it to terminate");
        String newPodName = assertDoesNotThrow(()->repository.spinUpBuildPod(buildNamespace, "build-1", "/mnt/build-scratch", "/mnt/build-scratch/build-project-1"));
        await().atMost(30, SECONDS).pollDelay(2, SECONDS).until(
                () -> {
                    LOG.info(() -> "Checking if the pod has terminated");
                    var pod = assertDoesNotThrow(()->repository.getPod(buildNamespace, newPodName));
                    if(pod==null) {
                        LOG.info(() -> "Pod not found");
                        return false;
                    }
                    var podSpecific = pod.get();
                    return podSpecific.getStatus().getContainerStatuses().size()==1 &&
                    podSpecific.getStatus().getContainerStatuses().getFirst().getState().getTerminated() !=null &&
                    podSpecific.getStatus().getContainerStatuses().getFirst().getState().getTerminated().getReason().compareToIgnoreCase("Completed") == 0;
                }
        );
        LOG.info(() -> "Pod has terminated, checking logs");
        var pod = assertDoesNotThrow(()->repository.getPod(buildNamespace, newPodName));
        String log = pod.getLog();
        assertThat(log).isNotEmpty();
        LOG.info(() -> "Deleting the pod");
        var deletePodResult = assertDoesNotThrow(()->repository.deletePod(buildNamespace, newPodName));
        assertThat(deletePodResult).isNotEmpty();
    }
}
