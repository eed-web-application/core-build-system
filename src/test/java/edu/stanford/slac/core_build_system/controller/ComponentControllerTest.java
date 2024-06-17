package edu.stanford.slac.core_build_system.controller;

import edu.stanford.slac.core_build_system.api.v1.dto.ComponentSummaryDTO;
import edu.stanford.slac.core_build_system.api.v1.dto.NewComponentDTO;
import edu.stanford.slac.core_build_system.api.v1.dto.UpdateComponentDTO;
import edu.stanford.slac.core_build_system.config.GitHubClient;
import edu.stanford.slac.core_build_system.exception.ComponentNotFound;
import edu.stanford.slac.core_build_system.model.Component;
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

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest()
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
@ActiveProfiles({"test"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class ComponentControllerTest {
    @MockBean
    GitHubClient.GHInstancer ghInstancer;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private TestControllerHelperService testControllerHelperService;

    @BeforeEach
    public void cleanCollection() {
        mongoTemplate.remove(new Query(), Component.class);
    }

    @Test
    public void createNewComponentOK() throws Exception {
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
    }

    @Test
    public void findById() throws Exception {
        var newCompIdResult = assertDoesNotThrow(
                () -> testControllerHelperService.componentControllerCreate(
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
                )
        );
        assertThat(newCompIdResult).isNotNull();
        assertThat(newCompIdResult.getPayload()).isNotBlank();

        var componentFoundResult = assertDoesNotThrow(
                () -> testControllerHelperService.componentControllerFindById(
                        mockMvc,
                        status().isOk(),
                        Optional.of("user1@slac.stanford.edu"),
                        newCompIdResult.getPayload()
                )
        );
        assertThat(componentFoundResult).isNotNull();
        assertThat(componentFoundResult.getPayload()).isNotNull();
        assertThat(componentFoundResult.getPayload().id()).isEqualTo(newCompIdResult.getPayload());
    }

    @Test
    public void componentUpdate() throws Exception {
        var newCompIdResult = assertDoesNotThrow(
                () -> testControllerHelperService.componentControllerCreate(
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
                )
        );
        assertThat(newCompIdResult).isNotNull();
        assertThat(newCompIdResult.getPayload()).isNotBlank();


        var updateResult = assertDoesNotThrow(
                () -> testControllerHelperService.componentControllerUpdateById(
                        mockMvc,
                        status().isOk(),
                        Optional.of("user1@slac.stanford.edu"),
                        newCompIdResult.getPayload(),
                        UpdateComponentDTO
                                .builder()
                                .name("custom app 2")
                                .description("custom app 2 for c++ applications")
                                .organization("custom")
                                .url("https://www.custom.org/")
                                .approvalRule("rule1")
                                .testingCriteria("criteria1")
                                .approvalIdentity(Set.of("user2@slac.stanford.edu"))
                                .build()
                )
        );
        assertThat(updateResult).isNotNull();
        assertThat(updateResult.getPayload()).isTrue();
    }

    @Test
    public void findAll() throws Exception {
        Set<String> newCreatedId = new HashSet<>();
        for (int idx = 0; idx <= 99; idx++) {
            int finalIdx = idx;
            var newCompIdResult = assertDoesNotThrow(
                    () -> testControllerHelperService.componentControllerCreate(
                            mockMvc,
                            status().isCreated(),
                            Optional.of("user1@slac.stanford.edu"),
                            NewComponentDTO
                                    .builder()
                                    .name("custom app %d".formatted(finalIdx))
                                    .description("custom app 1 for c++ applications")
                                    .organization("custom")
                                    .url("https://www.custom.org/")
                                    .approvalRule("rule1")
                                    .testingCriteria("criteria1")
                                    .approvalIdentity(Set.of("user1@slac.stanford.edu"))
                                    .build()
                    )
            );
            assertThat(newCompIdResult).isNotNull();
            assertThat(newCompIdResult.getPayload()).isNotBlank();
            newCreatedId.add(newCompIdResult.getPayload());
        }


        var findAllResult = assertDoesNotThrow(
                () -> testControllerHelperService.componentControllerFindAll(
                        mockMvc,
                        status().isOk(),
                        Optional.of("user1@slac.stanford.edu")
                )
        );
        assertThat(findAllResult).isNotNull();
        assertThat(findAllResult.getPayload()).isNotNull();
        assertThat(findAllResult.getPayload()).hasSize(100);
        assertThat(findAllResult.getPayload().stream().map(ComponentSummaryDTO::id)).containsAll(newCreatedId);
    }

    @Test
    public void deleteById() throws Exception {
        var newCompIdResult = assertDoesNotThrow(
                () -> testControllerHelperService.componentControllerCreate(
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
                )
        );
        assertThat(newCompIdResult).isNotNull();
        assertThat(newCompIdResult.getPayload()).isNotBlank();

        var componentFoundResult = assertDoesNotThrow(
                () -> testControllerHelperService.componentControllerDeleteById(
                        mockMvc,
                        status().isOk(),
                        Optional.of("user1@slac.stanford.edu"),
                        newCompIdResult.getPayload()
                )
        );
        assertThat(componentFoundResult).isNotNull();
        assertThat(componentFoundResult.getPayload()).isTrue();

        var componentNotFoundException = assertThrows(
                ComponentNotFound.class,
                () -> testControllerHelperService.componentControllerFindById(
                        mockMvc,
                        status().is4xxClientError(),
                        Optional.of("user1@slac.stanford.edu"),
                        newCompIdResult.getPayload()
                )
        );
        assertThat(componentNotFoundException).isNotNull();
        assertThat(componentNotFoundException.getErrorCode()).isEqualTo(-2);
    }
}
