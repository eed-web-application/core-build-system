package edu.stanford.slac.core_build_system.repository;

import edu.stanford.slac.core_build_system.model.Component;
import edu.stanford.slac.core_build_system.model.NewBranch;
import edu.stanford.slac.core_build_system.model.PullRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@AutoConfigureMockMvc
@SpringBootTest()
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
@ActiveProfiles({"test"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class TestGitHubRepository {
    @Autowired
    private GitServerRepository gitServerRepository;

    @Test
    public void createAndDeleteRepo() throws IOException {
        Component comp = Component.builder().name("component-name").build();
        assertDoesNotThrow(
                ()->gitServerRepository.createRepository(comp)
        );
        assertDoesNotThrow(
                ()->gitServerRepository.addUserToRepository(comp, "test-user")
        );
        assertDoesNotThrow(
                ()->gitServerRepository.addBranch(
                        comp,
                        NewBranch
                                .builder()
                                .branchName("new-dev-branch")
                                .baseBranch("main")
                                .build()
                )
        );
        assertDoesNotThrow(
                ()->gitServerRepository.createNewPR(
                        comp,
                        PullRequest
                                .builder()
                                .title("Automatic pr creation")
                                .branchName("new-dev-branch")
                                .baseBranch("main")
                                .base("This is the description?")
                                .build()
                )
        );
        assertDoesNotThrow(
                ()->gitServerRepository.deleteRepo(comp)
        );
    }
}
