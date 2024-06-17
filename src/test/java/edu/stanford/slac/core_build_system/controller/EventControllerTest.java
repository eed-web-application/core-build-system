package edu.stanford.slac.core_build_system.controller;

import edu.stanford.slac.core_build_system.api.v1.dto.BranchDTO;
import edu.stanford.slac.core_build_system.api.v1.dto.NewComponentDTO;
import edu.stanford.slac.core_build_system.config.GitHubClient;
import edu.stanford.slac.core_build_system.model.Component;
import edu.stanford.slac.core_build_system.service.ComponentService;
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
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest()
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
@ActiveProfiles({"test"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class EventControllerTest {
    @MockBean
    GitHubClient.GHInstancer ghInstancer;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private ComponentService componentService;
    @Autowired
    private TestControllerHelperService testControllerHelperService;

    @BeforeEach
    public void cleanCollection() {
        mongoTemplate.remove(new Query(), Component.class);
    }

    @Test
    public void pushNewBranchEvent() throws Exception {
        var newCompIdResult = testControllerHelperService.componentControllerCreate(
                mockMvc,
                status().isCreated(),
                Optional.of("user1@slac.stanford.edu"),
                NewComponentDTO
                        .builder()
                        .name("custom app 1")
                        .description("custom app 1 for c++ applications")
                        .organization("custom")
                        .url("https://www.custom.org/")
                        .approvalRule("rule1")
                        .testingCriteria("criteria1")
                        .approvalIdentity(Set.of("user1@slac.stanford.edu"))
                        .build()
        );
        assertThat(newCompIdResult).isNotNull();
        assertThat(newCompIdResult.getPayload()).isNotBlank();

        var branchCreationResult = testControllerHelperService.componentControllerCreateNewBranch(
                mockMvc,
                status().isOk(),
                Optional.of("user1@slac.stanford.edu"),
                "custom-app-1",
                BranchDTO
                        .builder()
                        .branchPoint("main")
                        .branchName("feature/branch1")
                        .build()
        );
        assertThat(branchCreationResult).isNotNull()
                .extracting("payload").isNotNull()
                .asString().isNotBlank();

        var fullComponent = testControllerHelperService.componentControllerFindById(
                mockMvc,
                status().isOk(),
                Optional.of("user1@slac.stanford.edu"),
                newCompIdResult.getPayload()
        );

        assertThat(fullComponent.getPayload()).isNotNull()
                .extracting("branches").isNotNull()
                .asList().isNotEmpty()
                .extracting("branchName").contains("feature/branch1");

        // simulate github event
        var result = testControllerHelperService.eventControllerHandlePushNewBranchEvent(
                mockMvc,
                status().isOk(),
                fullComponent.getPayload().componentToken(),
                fullComponent.getPayload().url(),
                "feature/branch1"
        );
        assertThat(result).isNotNull()
                .extracting("payload").isNotNull()
                .asString().isNotBlank();
    }
}
