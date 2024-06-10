package edu.stanford.slac.core_build_system.service;

import edu.stanford.slac.core_build_system.api.v1.dto.*;
import edu.stanford.slac.core_build_system.config.CoreBuildProperties;
import edu.stanford.slac.core_build_system.config.GitHubClient;
import edu.stanford.slac.core_build_system.exception.BuildNotFound;
import edu.stanford.slac.core_build_system.model.Component;
import edu.stanford.slac.core_build_system.model.ComponentBranchBuild;
import edu.stanford.slac.core_build_system.model.LogEntry;
import edu.stanford.slac.core_build_system.repository.KubernetesRepository;
import edu.stanford.slac.core_build_system.utility.GitServer;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kohsuke.github.GHOrganization;
import org.kohsuke.github.GitHub;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Set;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@AutoConfigureMockMvc
@SpringBootTest()
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
@ActiveProfiles({"test", "async-build-processing"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class ComponentBuildServiceTest {
    private String repositoryPath = null;
    private ComponentDTO component = null;
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

    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    private KubernetesRepository kubernetesRepository;
    @Autowired
    private CoreBuildProperties coreBuildProperties;

    @BeforeAll
    public void setUp() throws Exception {
        mongoTemplate.remove(new Query(), Component.class);

        when(ghInstancer.getClient()).thenReturn(gitHub);
        when(ghInstancer.ghOrganization()).thenReturn(ghOrganization);
        when(ghInstancer.gitCredentialsProvider()).thenReturn(credentialsProvider);

        // setup repository
        repositoryPath = GitServer.setupServer(List.of("branch1", "branch2"));
        // create component
        var componentId = assertDoesNotThrow(
                () -> componentService.create(
                        NewComponentDTO
                                .builder()
                                .name("boost libraries")
                                .description("boost libraries for c++ applications")
                                .organization("boost")
                                .url(repositoryPath)
                                .approvalRule("rule1")
                                .testingCriteria("criteria1")
                                .approvalIdentity(Set.of("user1@slac.stanford.edu"))
                                .build()
                )
        );
        var branchAddResult1 = assertDoesNotThrow(
                () -> componentService.addNewBranch(
                        "boost-libraries",
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
                        "boost-libraries",
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
    }

    @AfterAll
    public static void tearDown() {
        GitServer.cleanup();
    }

    @BeforeEach
    public void cleanBuild() {
        mongoTemplate.remove(new Query(), ComponentBranchBuild.class);
        mongoTemplate.remove(new Query(), LogEntry.class);
    }

    @Test
    public void testBuildComponent() throws Exception {
        // build component
        String buildId = assertDoesNotThrow(
                () -> componentBuildService.startBuild(
                        component.name(),
                        "branch1"
                )
        );
        assertThat(buildId).isNotNull();

        // wait for completion
        await().atMost(60, SECONDS).pollDelay(2, SECONDS).until(
                () -> {
                    ComponentBranchBuildDTO build = assertDoesNotThrow(
                            () -> componentBuildService.findBuildById(buildId)
                    );
                    return build.buildStatus() == BuildStatusDTO.SUCCESS;
                }
        );
        ComponentBranchBuildDTO fundBuild = assertDoesNotThrow(
                () -> componentBuildService.findBuildById(buildId)
        );
        assertThat(fundBuild).isNotNull();
        assertThat(fundBuild.buildStatus()).isEqualTo(BuildStatusDTO.SUCCESS);

        // fetch the log
        List<LogEntryDTO> buildLog = assertDoesNotThrow(
                () -> componentBuildService.findLogForBuild(buildId)
        );
        assertThat(buildLog).isNotEmpty();

        // delete build
        assertDoesNotThrow(
                () -> componentBuildService.deleteBuild(buildId)
        );

        // check for resource has been deleted
        BuildNotFound buildNotFoundException = assertThrows(
                BuildNotFound.class,
                () -> componentBuildService.findBuildById(buildId)
        );
        assertThat(buildNotFoundException).isNotNull();

        // check that no more are present
        List<LogEntryDTO> buildLogEmpty = assertDoesNotThrow(
                () -> componentBuildService.findLogForBuild(buildId)
        );
        assertThat(buildLogEmpty).isEmpty();
    }
}
