package edu.stanford.slac.core_build_system.controller;

import edu.stanford.slac.core_build_system.api.v1.controller.BuildController;
import edu.stanford.slac.core_build_system.api.v1.dto.*;
import edu.stanford.slac.core_build_system.config.CoreBuildProperties;
import edu.stanford.slac.core_build_system.config.GitHubClient;
import edu.stanford.slac.core_build_system.exception.BuildNotFound;
import edu.stanford.slac.core_build_system.model.Component;
import edu.stanford.slac.core_build_system.model.ComponentBranchBuild;
import edu.stanford.slac.core_build_system.model.LogEntry;
import edu.stanford.slac.core_build_system.repository.GithubServerRepository;
import edu.stanford.slac.core_build_system.repository.KubernetesRepository;
import edu.stanford.slac.core_build_system.service.ComponentBuildService;
import edu.stanford.slac.core_build_system.service.ComponentService;
import edu.stanford.slac.core_build_system.utility.GitServer;
import edu.stanford.slac.core_build_system.utility.KubernetesInit;
import org.assertj.core.api.AssertionsForClassTypes;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kohsuke.github.GHOrganization;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.mockito.InjectMocks;
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
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
public class BuildControllerTest {
    private String repositoryPath = null;
    private ComponentDTO component = null;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private GHRepository ghRepository;
    @MockBean
    private GitHub gitHub;

    @MockBean
    private GHOrganization ghOrganization;

    @MockBean
    private UsernamePasswordCredentialsProvider credentialsProvider;

    @MockBean
    private GitHubClient.GHInstancer ghInstancer;

    @Autowired
    private ComponentService componentService;

    @Autowired
    private ComponentBuildService componentBuildService;
    @InjectMocks
    private GithubServerRepository githubServerRepository;
    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    private KubernetesRepository kubernetesRepository;
    @Autowired
    private CoreBuildProperties coreBuildProperties;
    @Autowired
    private ThreadPoolTaskScheduler taskScheduler;
    @Autowired
    private TestControllerHelperService testControllerHelperService;
    @Autowired
    private BuildController buildController;

    @BeforeAll
    public void setUp() throws Exception {
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

        assertThat(componentId).isNotNull();
        component = assertDoesNotThrow(
                () -> componentService.findById(componentId)
        );
        assertThat(component).isNotNull();

        assertDoesNotThrow(
                ()->kubernetesRepository.ensureNamespace(coreBuildProperties.getK8sBuildNamespace())
        );
        KubernetesInit.init(kubernetesRepository, coreBuildProperties.getK8sBuildNamespace());
    }

    @AfterAll
    public static void tearDown() {
        GitServer.cleanup();
    }

    @BeforeEach
    public void cleanBuild() {
        mongoTemplate.remove(new Query(), ComponentBranchBuild.class);
        mongoTemplate.remove(new Query(), LogEntry.class);
        // Reset the mock before each test
        taskScheduler.initialize();
    }

    @AfterEach
    public void cleanComponent() {
        taskScheduler.shutdown();
    }

    @Test
    public void testBuildComponentByController() {
        // build component
        var newBuildIdsResult = assertDoesNotThrow(
                () -> testControllerHelperService.buildControllerStartNewBuild(
                        mockMvc,
                        status().isCreated(),
                        component.name(),
                        "branch1",
                        Optional.of(Map.of(
                                "ADBS_BUILD_TYPE", "container"
                        ))

                )
        );
        AssertionsForClassTypes.assertThat(newBuildIdsResult).isNotNull();
        AssertionsForClassTypes.assertThat(newBuildIdsResult.getErrorCode()).isEqualTo(0);
        assertThat(newBuildIdsResult.getPayload()).isNotEmpty();
        AssertionsForClassTypes.assertThat(newBuildIdsResult.getPayload().size()).isEqualTo(2);

        // wait for completion
        await()
                .atMost(90, SECONDS)
                .pollDelay(2, SECONDS)
                .pollInterval(2, SECONDS)
                .until(
                        () -> {
                            List<Boolean> completionState = new ArrayList<>();
                            // fetch each single build
                            newBuildIdsResult.getPayload().forEach(
                                    buildId -> {
                                        var buildResult = assertDoesNotThrow(
                                                () -> testControllerHelperService.buildControllerFindBuildById(
                                                        mockMvc,
                                                        status().isOk(),
                                                        buildId
                                                )
                                        );
                                        AssertionsForClassTypes.assertThat(buildResult).isNotNull();
                                        AssertionsForClassTypes.assertThat(buildResult.getErrorCode()).isEqualTo(0);
                                        completionState.add(buildResult.getPayload().buildStatus() == BuildStatusDTO.SUCCESS || buildResult.getPayload().buildStatus() == BuildStatusDTO.FAILED);
                                    }
                            );
                            return completionState.stream().allMatch(s -> s);
                        }
                );

        newBuildIdsResult.getPayload().forEach(
                buildId -> {
                    var fundBuildResult = assertDoesNotThrow(
                            () -> testControllerHelperService.buildControllerFindBuildById(
                                    mockMvc,
                                    status().isOk(),
                                    buildId
                            )
                    );
                    AssertionsForClassTypes.assertThat(fundBuildResult).isNotNull();
                    AssertionsForClassTypes.assertThat(fundBuildResult.getErrorCode()).isEqualTo(0);
                    assertThat(fundBuildResult.getPayload().buildStatus()).isEqualTo(BuildStatusDTO.SUCCESS);
                    // check for build variable
                    assertThat(fundBuildResult.getPayload().buildCustomVariables()).isNotEmpty();
                    assertThat(fundBuildResult.getPayload().buildCustomVariables().size()).isEqualTo(1);
                    assertThat(fundBuildResult.getPayload().buildCustomVariables().get("ADBS_BUILD_TYPE")).isEqualTo("container");
                }
        );


        // fetch the log
        newBuildIdsResult.getPayload().forEach(
                buildId -> {
                    var buildLogResult = assertDoesNotThrow(
                            () -> testControllerHelperService.buildControllerFindLogByBuildId(
                                    mockMvc,
                                    status().isOk(),
                                    buildId
                            )
                    );
                    assertThat(buildLogResult.getPayload()).isNotEmpty();
                }
        );

        // test find all build for component name and branch

        var allBuildsResult = assertDoesNotThrow(
                () -> testControllerHelperService.buildControllerFindByComponentNameAndBranch(
                        mockMvc,
                        status().isOk(),
                        "component-a",
                        "branch1"
                )
        );
        AssertionsForClassTypes.assertThat(allBuildsResult).isNotNull();
        AssertionsForClassTypes.assertThat(allBuildsResult.getErrorCode()).isEqualTo(0);
        assertThat(allBuildsResult.getPayload()).isNotEmpty();
        AssertionsForClassTypes.assertThat(allBuildsResult.getPayload().size()).isEqualTo(2);

        newBuildIdsResult.getPayload().forEach(
                buildId -> {
                    // delete build
                    assertDoesNotThrow(
                            () -> testControllerHelperService.buildControllerDeleteBuildById(
                                    mockMvc,
                                    status().isOk(),
                                    buildId
                            )
                    );
                }
        );

        newBuildIdsResult.getPayload().forEach(
                buildId -> {
                    // check for resource has been deleted
                    BuildNotFound buildNotFoundException = assertThrows(
                            BuildNotFound.class,
                            () -> testControllerHelperService.buildControllerFindBuildById(
                                    mockMvc,
                                    status().isNotFound(),
                                    buildId
                            )
                    );
                    assertThat(buildNotFoundException).isNotNull();
                }
        );

        newBuildIdsResult.getPayload().forEach(
                buildId -> {
                    // check that no more are present
                    var buildLogEmptyResult = assertDoesNotThrow(
                            () -> testControllerHelperService.buildControllerFindLogByBuildId(
                                    mockMvc,
                                    status().isOk(),
                                    buildId
                            )
                    );
                    AssertionsForClassTypes.assertThat(buildLogEmptyResult).isNotNull();
                    assertThat(buildLogEmptyResult.getPayload()).isEmpty();
                }
        );
    }
}
