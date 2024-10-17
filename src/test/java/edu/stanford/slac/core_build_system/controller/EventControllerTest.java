package edu.stanford.slac.core_build_system.controller;

import edu.stanford.slac.core_build_system.api.v1.dto.*;
import edu.stanford.slac.core_build_system.config.CoreBuildProperties;
import edu.stanford.slac.core_build_system.config.GitHubClient;
import edu.stanford.slac.core_build_system.model.Component;
import edu.stanford.slac.core_build_system.model.ComponentBranchBuild;
import edu.stanford.slac.core_build_system.model.LogEntry;
import edu.stanford.slac.core_build_system.repository.KubernetesRepository;
import edu.stanford.slac.core_build_system.service.ComponentService;
import edu.stanford.slac.core_build_system.utility.GitServer;
import edu.stanford.slac.core_build_system.utility.KubernetesInit;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kohsuke.github.GHOrganization;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
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
    @MockBean
    private GHRepository ghRepository;
    @MockBean
    private GitHub gitHub;
    @MockBean
    private GHOrganization ghOrganization;
    @MockBean
    private UsernamePasswordCredentialsProvider credentialsProvider;
    @Autowired
    private KubernetesRepository kubernetesRepository;
    @Autowired
    private CoreBuildProperties coreBuildProperties;
    @Autowired
    private ThreadPoolTaskScheduler taskScheduler;
    private String repositoryPath = null;
    private ComponentDTO component = null;

    @BeforeEach
    public void cleanCollection() throws Exception {
        mongoTemplate.remove(new Query(), Component.class);
        when(ghInstancer.getClient()).thenReturn(gitHub);
        when(ghInstancer.ghOrganization(anyString())).thenReturn(ghOrganization);
        when(ghInstancer.gitCredentialsProvider()).thenReturn(credentialsProvider);
        when(ghOrganization.getRepository(any())).thenReturn(ghRepository);
        // setup repository
        repositoryPath = GitServer.setupServer(List.of("branch1", "branch2"));
        when(ghRepository.getHttpTransportUrl()).thenReturn(repositoryPath);
        // create component
        var componentId = assertDoesNotThrow(
                () -> componentService.create(
                        NewComponentDTO
                                .builder()
                                .name("component-a")
                                .description("component-a description")
                                .organization("organization-a")
                                .url(repositoryPath)
                                .buildOs(List.of(BuildOSDTO.ROCKY9, BuildOSDTO.RHEL8))
                                .approvalRule("rule1")
                                .testingCriteria("criteria1")
                                .approvalIdentity(Set.of("user1@slac.stanford.edu"))
                                .build()
                )
        );
        assertThat(componentId).isNotNull();
        component = assertDoesNotThrow(
                () -> componentService.findById(componentId)
        );
        assertThat(component).isNotNull();
        var branchAddResult1 = assertDoesNotThrow(
                () -> componentService.addNewBranch(
                        "component-a",
                        BranchDTO
                                .builder()
                                .type("feature")
                                .branchName("branch1")
                                .branchPoint("main")
                                .build()
                )
        );
        assertThat(branchAddResult1).isNotNull();
        assertThat(branchAddResult1).isTrue();

        var branchAddResult2 = assertDoesNotThrow(
                () -> componentService.addNewBranch(
                        "component-a",
                        BranchDTO
                                .builder()
                                .type("feature")
                                .branchName("branch2")
                                .branchPoint("main")
                                .build()
                )
        );
        assertThat(branchAddResult2).isNotNull();
        assertThat(branchAddResult2).isTrue();

        assertDoesNotThrow(
                ()->kubernetesRepository.ensureNamespace(coreBuildProperties.getK8sBuildNamespace())
        );
        KubernetesInit.init(kubernetesRepository, coreBuildProperties.getK8sBuildNamespace());
    }


    @BeforeEach
    public void cleanBuild() {
        mongoTemplate.remove(new Query(), ComponentBranchBuild.class);
        mongoTemplate.remove(new Query(), LogEntry.class);
        // Reset the mock before each test
        taskScheduler.initialize();
    }

    @AfterEach
    public void clearAfterEach() {
        taskScheduler.shutdown();
    }

    @AfterAll
    public void tearDown() {
        GitServer.cleanup();
    }

    @Test
    public void receivePRSyncEventTest() throws Exception {
        // simulate github pr sync event
        var result = testControllerHelperService.eventControllerHandleSyncPREvent(
                mockMvc,
                status().isOk(),
                component.componentToken(),
                Map.of(
                        "pull_request.head.label", "branch1",
                        "pull_request.head.ref", "branch1",
                        "repository.git_url", component.url()
                )
        );
        assertThat(result).isNotNull()
                .extracting("payload").isNotNull()
                .asString().isNotBlank();


        // wait for the completion of the build on branch1
        await()
                .atMost(120, SECONDS)
                .pollDelay(2, SECONDS)
                .pollInterval(2, SECONDS)
                .until(
                        () -> {
                            List<Boolean> completionState = new ArrayList<>();
                            var foundBuildsResult = assertDoesNotThrow(
                                    () -> testControllerHelperService.buildControllerFindByComponentNameAndBranch(
                                            mockMvc,
                                            status().isOk(),
                                            component.name(),
                                            "branch1"
                                    )
                            );
                            assertThat(foundBuildsResult).isNotNull();
                            // fetch each single build
                            foundBuildsResult.getPayload().forEach(
                                    componentBranchBuildDTO -> {
                                        completionState.add(componentBranchBuildDTO.buildStatus() == BuildStatusDTO.SUCCESS);
                                    }
                            );

                            return completionState.stream().allMatch(s -> s);
                        }
                );
    }
}
