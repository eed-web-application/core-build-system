package edu.stanford.slac.core_build_system.repository;

import edu.stanford.slac.core_build_system.model.ComponentBranchBuild;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@AutoConfigureMockMvc
@SpringBootTest()
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
@ActiveProfiles({"test"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class ComponentBranchBuildRepositoryTest {
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
                () -> componentBranchBuildRepository.releaseLock(newBuild.getId())
        );
        assertThat(deleteResult).isTrue();
    }
}
