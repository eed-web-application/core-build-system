package edu.stanford.slac.core_build_system.repository;

import edu.stanford.slac.core_build_system.config.GitHubClient;
import edu.stanford.slac.core_build_system.model.BuildStatus;
import edu.stanford.slac.core_build_system.model.ComponentBranchBuild;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@AutoConfigureMockMvc
@SpringBootTest()
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
@ActiveProfiles({"test"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class ComponentBranchBuildRepositoryTest {
    @MockBean
    private GitHubClient.GHInstancer ghInstancer;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private ComponentBranchBuildRepository componentBranchBuildRepository;

    @BeforeEach
    public void clean() {
        mongoTemplate.remove(new Query(), ComponentBranchBuild.class);
    }

    @Test
    public void testFindAndLockNextDocument() {
        ComponentBranchBuild newBuild = assertDoesNotThrow(
                () -> componentBranchBuildRepository.save(
                        ComponentBranchBuild.builder().build()
                )

        );

        Optional<ComponentBranchBuild> selectedDocument = assertDoesNotThrow(
                () -> componentBranchBuildRepository.findAndLockNextDocument(Instant.now().minus(5, ChronoUnit.MINUTES))
        );

        assertThat(selectedDocument).isPresent();
        assertThat(selectedDocument.get().getId()).isEqualTo(newBuild.getId());

        Optional<ComponentBranchBuild> selectedDocumentEmpty = assertDoesNotThrow(
                () -> componentBranchBuildRepository.findAndLockNextDocument(Instant.now().minus(5, ChronoUnit.MINUTES))
        );

        assertThat(selectedDocumentEmpty).isEmpty();


        boolean deleteResult = assertDoesNotThrow(
                () -> componentBranchBuildRepository.releaseLock(newBuild.getId(), BuildStatus.SUCCESS)
        );
        assertThat(deleteResult).isTrue();
    }

    @Test
    public void testBuildAreGivenFromTheOldestOne() {
        Map<String, String> executionMap = new HashMap<>();
        for (int i = 0; i < 10; i++) {
            int finalI = i;
            assertDoesNotThrow(
                    () -> componentBranchBuildRepository.save(
                            ComponentBranchBuild
                                    .builder()
                                    .branchName("%d".formatted(finalI))
                                    .build()
                    )
            );
        }

        // testing with multithreading task to fetch every build and check that all has be taken
        CountDownLatch latch = new CountDownLatch(10);
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                Optional<ComponentBranchBuild> selectedDocument = assertDoesNotThrow(
                        () -> componentBranchBuildRepository.findAndLockNextDocument(Instant.now().minus(5, ChronoUnit.MINUTES))
                );
                assertThat(selectedDocument).isPresent();
                executionMap.put(Thread.currentThread().getName(), selectedDocument.get().getBranchName());
                latch.countDown();
            }).start();
        }

        assertDoesNotThrow(
                () -> latch.await()
        );
        // Check that all documents have been locked and released once
        for (int i = 0; i < 10; i++) {
            Optional<ComponentBranchBuild> document = assertDoesNotThrow(
                    () -> componentBranchBuildRepository.findAndLockNextDocument(Instant.now().minus(5, ChronoUnit.MINUTES))
            );
            assertThat(document).isEmpty();
        }
    }

    @Test
    public void testCompletedBuildAreNotFetchedAnymoreCase1() {
        assertDoesNotThrow(
                () -> componentBranchBuildRepository.save(
                        ComponentBranchBuild
                                .builder()
                                .branchName("1")
                                .build()
                )
        );

        // fetch and lock
        Optional<ComponentBranchBuild> selectedDocument = assertDoesNotThrow(
                () -> componentBranchBuildRepository.findAndLockNextDocument(Instant.now().minus(5, ChronoUnit.MINUTES))
        );
        assertThat(selectedDocument).isPresent();
        // simulate processed
        var fetchedDocument = selectedDocument.get();
        fetchedDocument.setBuildStatus(BuildStatus.SUCCESS);
        ComponentBranchBuild savedBuild = assertDoesNotThrow(
                () -> componentBranchBuildRepository.save(fetchedDocument)
        );
        assertThat(savedBuild.getBuildStatus()).isEqualTo(BuildStatus.SUCCESS);
        // release the lock
        Boolean releaseResult = assertDoesNotThrow(
                () -> componentBranchBuildRepository.releaseLock(selectedDocument.get().getId())
        );
        assertThat(releaseResult).isTrue();

        // next call to fetch document will return empty
        Optional<ComponentBranchBuild> emptyDocumentFetched = assertDoesNotThrow(
                () -> componentBranchBuildRepository.findAndLockNextDocument(Instant.now().minus(5, ChronoUnit.MINUTES))
        );
        assertThat(emptyDocumentFetched).isEmpty();
    }

    @Test
    public void testCOmpletedBuildAreNotFetchedAnymoreCase2() {
        assertDoesNotThrow(
                () -> componentBranchBuildRepository.save(
                        ComponentBranchBuild
                                .builder()
                                .branchName("1")
                                .build()
                )
        );
        assertDoesNotThrow(
                () -> componentBranchBuildRepository.save(
                        ComponentBranchBuild
                                .builder()
                                .branchName("2")
                                .build()
                )
        );
        // fetch and lock
        Optional<ComponentBranchBuild> selectedDocument = assertDoesNotThrow(
                () -> componentBranchBuildRepository.findAndLockNextDocument(Instant.now().minus(5, ChronoUnit.MINUTES))
        );
        assertThat(selectedDocument).isPresent();
        assertThat(selectedDocument.get().getBranchName()).isEqualTo("1");
        // simulate processed
        var fetchedDocument = selectedDocument.get();
        fetchedDocument.setBuildStatus(BuildStatus.SUCCESS);
        ComponentBranchBuild savedBuild = assertDoesNotThrow(
                () -> componentBranchBuildRepository.save(fetchedDocument)
        );
        assertThat(savedBuild.getBuildStatus()).isEqualTo(BuildStatus.SUCCESS);
        // release the lock
        Boolean releaseResult = assertDoesNotThrow(
                () -> componentBranchBuildRepository.releaseLock(selectedDocument.get().getId())
        );
        assertThat(releaseResult).isTrue();

        // next call to fetch document will return empty
        Optional<ComponentBranchBuild> secondBuild = assertDoesNotThrow(
                () -> componentBranchBuildRepository.findAndLockNextDocument(Instant.now().minus(5, ChronoUnit.MINUTES))
        );
        assertThat(secondBuild).isNotEmpty();
        assertThat(secondBuild.get().getBranchName()).isEqualTo("2");
        var fetchedDocumentSecondBuild = secondBuild.get();
        fetchedDocumentSecondBuild.setBuildStatus(BuildStatus.SUCCESS);
        savedBuild = assertDoesNotThrow(
                () -> componentBranchBuildRepository.save(fetchedDocumentSecondBuild)
        );
        assertThat(savedBuild.getBuildStatus()).isEqualTo(BuildStatus.SUCCESS);
        // release the lock
        releaseResult = assertDoesNotThrow(
                () -> componentBranchBuildRepository.releaseLock(secondBuild.get().getId())
        );

        // next call to fetch document will return empty
        Optional<ComponentBranchBuild> emptyDocumentFetched = assertDoesNotThrow(
                () -> componentBranchBuildRepository.findAndLockNextDocument(Instant.now().minus(5, ChronoUnit.MINUTES))
        );
        assertThat(emptyDocumentFetched).isEmpty();
    }
}
