package edu.stanford.slac.core_build_system.service;

import edu.stanford.slac.core_build_system.api.v1.dto.*;
import edu.stanford.slac.core_build_system.config.CoreBuildProperties;
import edu.stanford.slac.core_build_system.config.GitHubClient;
import edu.stanford.slac.core_build_system.exception.BranchNotFound;
import edu.stanford.slac.core_build_system.exception.BuildOSMissing;
import edu.stanford.slac.core_build_system.model.Component;
import edu.stanford.slac.core_build_system.model.ComponentBranchBuild;
import edu.stanford.slac.core_build_system.model.LogEntry;
import edu.stanford.slac.core_build_system.repository.KubernetesRepository;
import org.assertj.core.api.AssertionsForClassTypes;
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

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@AutoConfigureMockMvc
@SpringBootTest()
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
@ActiveProfiles({"test", "async-build-processing"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class ComponentBuildServiceFailTest {
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

    @BeforeEach
    public void cleanBuild() {
        mongoTemplate.remove(new Query(), Component.class);
        mongoTemplate.remove(new Query(), ComponentBranchBuild.class);
        mongoTemplate.remove(new Query(), LogEntry.class);
    }


    @Test
    public void testbuildFailWithNoBuildOs() throws Exception {
        // create component
        var componentId = assertDoesNotThrow(
                () -> componentService.create(
                        NewComponentDTO
                                .builder()
                                .name("boost libraries")
                                .description("boost libraries for c++ applications")
                                .organization("boost")
                                .approvalRule("rule1")
                                .url("url")
                                .testingCriteria("criteria1")
                                .approvalIdentity(Set.of("user1@slac.stanford.edu"))
                                .build()
                )
        );

        assertThat(componentId).isNotNull();

        // start build
        BuildOSMissing buildOSMissing = assertThrows(
                BuildOSMissing.class,
                () -> componentBuildService.startBuild("boost-libraries", "branch1", buildVariables)
        );
        assertThat(buildOSMissing).isNotNull();
        assertThat(buildOSMissing.getErrorCode()).isEqualTo(-3);
    }

    @Test
    public void testbuildFailWithNoRepository() throws Exception {
        // create component
        var componentId = assertDoesNotThrow(
                () -> componentService.create(
                        NewComponentDTO
                                .builder()
                                .name("boost libraries")
                                .description("boost libraries for c++ applications")
                                .organization("boost")
                                .approvalRule("rule1")
                                .url("url")
                                .buildOs(List.of(BuildOSDTO.RHEL5))
                                .testingCriteria("criteria1")
                                .approvalIdentity(Set.of("user1@slac.stanford.edu"))
                                .build()
                )
        );

        assertThat(componentId).isNotNull();

        // start build
        BranchNotFound branchNotFound = assertThrows(
                BranchNotFound.class,
                () -> componentBuildService.startBuild("boost-libraries", "branch1", buildVariables)
        );
        AssertionsForClassTypes.assertThat(branchNotFound).isNotNull();
        AssertionsForClassTypes.assertThat(branchNotFound.getErrorCode()).isEqualTo(-4);
    }
}
