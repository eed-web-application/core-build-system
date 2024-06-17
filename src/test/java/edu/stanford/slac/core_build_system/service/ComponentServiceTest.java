package edu.stanford.slac.core_build_system.service;

import edu.stanford.slac.ad.eed.baselib.exception.ControllerLogicException;
import edu.stanford.slac.core_build_system.api.v1.dto.*;
import edu.stanford.slac.core_build_system.config.GitHubClient;
import edu.stanford.slac.core_build_system.exception.ComponentAlreadyExists;
import edu.stanford.slac.core_build_system.exception.ComponentNotFound;
import edu.stanford.slac.core_build_system.model.CommandTemplate;
import edu.stanford.slac.core_build_system.model.Component;
import org.junit.jupiter.api.BeforeAll;
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

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@AutoConfigureMockMvc
@SpringBootTest()
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
@ActiveProfiles({"test"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class ComponentServiceTest {
    @MockBean
    GitHubClient.GHInstancer ghInstancer;
    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    ComponentService componentService;
    @Autowired
    CommandTemplateService commandTemplateService;

    @BeforeEach
    public void cleanCollection() {
        mongoTemplate.remove(new Query(), Component.class);
    }

    @Test
    public void createNewOK() {
        var boostComponentId = assertDoesNotThrow(
                () -> componentService.create(
                        NewComponentDTO
                                .builder()
                                .name("boost libraries")
                                .description("boost libraries for c++ applications")
                                .organization("boost")
                                .url("https://www.boost.org/")
                                .approvalRule("rule1")
                                .testingCriteria("criteria1")
                                .approvalIdentity(Set.of("user1@slac.stanford.edu"))
                                .build()
                )
        );
        assertThat(boostComponentId).isNotNull();

        var fullComponentFound = assertDoesNotThrow(
                () -> componentService.findById(boostComponentId)
        );
        assertThat(fullComponentFound).isNotNull();
        assertThat(fullComponentFound.name()).isEqualTo("boost-libraries");
        assertThat(fullComponentFound.description()).isEqualTo("boost libraries for c++ applications");
        assertThat(fullComponentFound.organization()).isEqualTo("boost");
        assertThat(fullComponentFound.url()).isEqualTo("https://www.boost.org/");
        assertThat(fullComponentFound.approvalRule()).isEqualTo("rule1");
        assertThat(fullComponentFound.testingCriteria()).isEqualTo("criteria1");
        assertThat(fullComponentFound.approvalIdentity()).isNotNull();
        assertThat(fullComponentFound.approvalIdentity().size()).isEqualTo(1);
        assertThat(fullComponentFound.componentToken()).isNotEmpty();
    }

    @Test
    public void updateNewOK() {
        var boostComponentId = assertDoesNotThrow(
                () -> componentService.create(
                        NewComponentDTO
                                .builder()
                                .name("boost libraries")
                                .description("boost libraries for c++ applications")
                                .organization("boost")
                                .url("https://www.boost.org/")
                                .approvalRule("rule1")
                                .testingCriteria("criteria1")
                                .approvalIdentity(Set.of("user1@slac.stanford.edu"))
                                .build()
                )
        );
        assertThat(boostComponentId).isNotNull();

        assertDoesNotThrow(
                () -> componentService.updateById(
                        boostComponentId,
                        UpdateComponentDTO
                                .builder()
                                .name("boost libraries")
                                .description("boost libraries for c++ applications update")
                                .organization("boost updated")
                                .url("https://www.boost.org/updated")
                                .approvalRule("rule1 updated")
                                .testingCriteria("criteria1 updated")
                                .build()
                )
        );

        var boostComponent = assertDoesNotThrow(
                () -> componentService.findById(boostComponentId)
        );
        assertThat(boostComponent).isNotNull();
        assertThat(boostComponent.name()).isEqualTo("boost-libraries");
        assertThat(boostComponent.description()).isEqualTo("boost libraries for c++ applications update");
        assertThat(boostComponent.organization()).isEqualTo("boost updated");
        assertThat(boostComponent.url()).isEqualTo("https://www.boost.org/updated");
        assertThat(boostComponent.approvalRule()).isEqualTo("rule1 updated");
        assertThat(boostComponent.testingCriteria()).isEqualTo("criteria1 updated");
    }

    @Test
    public void failSameName() {
        var boostComponentId = assertDoesNotThrow(
                () -> componentService.create(
                        NewComponentDTO
                                .builder()
                                .name("boost libraries")
                                .description("boost libraries for c++ applications")
                                .organization("boost")
                                .url("https://www.boost.org/")
                                .approvalRule("rule1")
                                .testingCriteria("criteria1")
                                .approvalIdentity(Set.of("user1@slac.stanford.edu"))
                                .build()
                )
        );
        assertThat(boostComponentId).isNotNull();

        var sameNameException = assertThrows(
                ComponentAlreadyExists.class,
                () -> componentService.create(
                        NewComponentDTO
                                .builder()
                                .name("boost libraries")
                                .description("boost libraries for c++ applications")
                                .organization("boost")
                                .url("https://www.boost.org/")
                                .approvalRule("rule1")
                                .testingCriteria("criteria1")
                                .approvalIdentity(Set.of("user1@slac.stanford.edu"))
                                .build()
                )
        );
        assertThat(sameNameException).isNotNull();
    }

    @Test
    public void updateFailSameNameOK() {
        var boostComponentId = assertDoesNotThrow(
                () -> componentService.create(
                        NewComponentDTO
                                .builder()
                                .name("boost libraries")
                                .description("boost libraries for c++ applications")
                                .organization("boost")
                                .url("https://www.boost.org/")
                                .approvalRule("rule1")
                                .testingCriteria("criteria1")
                                .approvalIdentity(Set.of("user1@slac.stanford.edu"))
                                .build()
                )
        );
        assertThat(boostComponentId).isNotNull();

        var customCompId = assertDoesNotThrow(
                () -> componentService.create(
                        NewComponentDTO
                                .builder()
                                .name("custom component")
                                .description("custom component for c++ applications")
                                .organization("custom")
                                .url("https://www.custom.org/")
                                .approvalRule("rule1")
                                .testingCriteria("criteria1")
                                .approvalIdentity(Set.of("user1@slac.stanford.edu"))
                                .build()
                )
        );
        assertThat(customCompId).isNotNull();

        var componentLAlreadyExists = assertThrows(
                ComponentAlreadyExists.class,
                () -> componentService.updateById(
                        boostComponentId,
                        UpdateComponentDTO
                                .builder()
                                .name("custom component")
                                .description("boost libraries for c++ applications update")
                                .organization("boost updated")
                                .url("https://www.boost.org/updated")
                                .approvalRule("rule1 updated")
                                .testingCriteria("criteria1 updated")
                                .build()
                )
        );
        assertThat(componentLAlreadyExists).isNotNull();
        assertThat(componentLAlreadyExists.getErrorCode()).isEqualTo(-1);
    }

    @Test
    public void createWithRightDependenceOK() {
        var boostComponentId = assertDoesNotThrow(
                () -> componentService.create(
                        NewComponentDTO
                                .builder()
                                .name("boost libraries")
                                .description("boost libraries for c++ applications")
                                .organization("boost")
                                .url("https://www.boost.org/")
                                .approvalRule("rule1")
                                .testingCriteria("criteria1")
                                .approvalIdentity(Set.of("user1@slac.stanford.edu"))
                                .build()
                )
        );
        assertThat(boostComponentId).isNotNull();

        var customAppComponentId = assertDoesNotThrow(
                () -> componentService.create(
                        NewComponentDTO
                                .builder()
                                .name("custom app 1")
                                .description("custom app 1 for c++ applications")
                                .organization("custom")
                                .url("https://www.custom.org/")
                                .approvalRule("rule1")
                                .testingCriteria("criteria1")
                                .approvalIdentity(Set.of("user1@slac.stanford.edu"))
                                .dependOn(
                                        Set.of(
                                                ComponentDependencyDTO
                                                        .builder()
                                                        .componentName("boost Libraries")
                                                        .tagName("1_82_0")
                                                        .build()
                                        )
                                )
                                .build()
                )
        );
        assertThat(customAppComponentId).isNotNull();
    }

    @Test
    public void createWithWrongDependenceFail() {
        var componentNotFoundException = assertThrows(
                ComponentNotFound.class,
                () -> componentService.create(
                        NewComponentDTO
                                .builder()
                                .name("custom app 1")
                                .description("custom app 1 for c++ applications")
                                .organization("custom")
                                .url("https://www.custom.org/")
                                .approvalRule("rule1")
                                .testingCriteria("criteria1")
                                .approvalIdentity(Set.of("user1@slac.stanford.edu"))
                                .dependOn(
                                        Set.of(
                                                ComponentDependencyDTO
                                                        .builder()
                                                        .componentName("boost Libraries")
                                                        .tagName("1_82_0")
                                                        .build()
                                        )
                                )
                                .build()
                )
        );
        assertThat(componentNotFoundException).isNotNull();
        assertThat(componentNotFoundException.getErrorCode()).isEqualTo(-1);
    }

    @Test
    public void createVersionOK() {
        var boostComponentId = assertDoesNotThrow(
                () -> componentService.create(
                        NewComponentDTO
                                .builder()
                                .name("boost libraries")
                                .description("boost libraries for c++ applications")
                                .organization("boost")
                                .url("https://www.boost.org/")
                                .approvalRule("rule1")
                                .testingCriteria("criteria1")
                                .approvalIdentity(Set.of("user1@slac.stanford.edu"))
                                .build()
                )
        );
        assertThat(boostComponentId).isNotNull();
        var versionAddResult = assertDoesNotThrow(
                () -> componentService.addNewVersion(
                        "boost-libraries",
                        NewVersionDTO
                                .builder()
                                .label("1_83_0")
                                .build()
                )
        );
        assertThat(versionAddResult).isNotNull();
        assertThat(versionAddResult).isTrue();

        // fetch full component
        var boostComponent = assertDoesNotThrow(
                () -> componentService.findById(boostComponentId)
        );
        assertThat(boostComponent).isNotNull();
        assertThat(boostComponent.name()).isEqualTo("boost-libraries");
        assertThat(boostComponent.versions()).isNotNull();
        assertThat(boostComponent.versions().size()).isEqualTo(1);
        assertThat(boostComponent.versions().get(0).label()).isEqualTo("1_83_0");
    }

    @Test
    public void createVersionFailTwoSameLabelOK() {
        var boostComponentId = assertDoesNotThrow(
                () -> componentService.create(
                        NewComponentDTO
                                .builder()
                                .name("boost libraries")
                                .description("boost libraries for c++ applications")
                                .organization("boost")
                                .url("https://www.boost.org/")
                                .approvalRule("rule1")
                                .testingCriteria("criteria1")
                                .approvalIdentity(Set.of("user1@slac.stanford.edu"))
                                .build()
                )
        );
        assertThat(boostComponentId).isNotNull();
        var versionAddResult = assertDoesNotThrow(
                () -> componentService.addNewVersion(
                        "boost-libraries",
                        NewVersionDTO
                                .builder()
                                .label("1_83_0")
                                .build()
                )
        );
        assertThat(versionAddResult).isNotNull();
        assertThat(versionAddResult).isTrue();

        // fetch full component
        var sameLabelException = assertThrows(
                ControllerLogicException.class,
                () -> componentService.addNewVersion(
                        "boost-libraries",
                        NewVersionDTO
                                .builder()
                                .label("1_83_0")
                                .build()
                )
        );
        assertThat(sameLabelException).isNotNull();
        assertThat(sameLabelException.getErrorCode()).isEqualTo(-3);
    }

    @Test
    public void createBranchOK() {
        var boostComponentId = assertDoesNotThrow(
                () -> componentService.create(
                        NewComponentDTO
                                .builder()
                                .name("boost libraries")
                                .description("boost libraries for c++ applications")
                                .organization("boost")
                                .url("https://www.boost.org/")
                                .approvalRule("rule1")
                                .testingCriteria("criteria1")
                                .approvalIdentity(Set.of("user1@slac.stanford.edu"))
                                .build()
                )
        );
        assertThat(boostComponentId).isNotNull();


        var branchAddResult = assertDoesNotThrow(
                () -> componentService.addNewBranch(
                        "boost-libraries",
                        BranchDTO
                                .builder()
                                .type("feature")
                                .branchName("add-new-channel")
                                .branchPoint("main")
                                .build()
                )
        );
        assertThat(branchAddResult).isNotNull();
        assertThat(branchAddResult).isTrue();

        // fetch full component
        var boostComponent = assertDoesNotThrow(
                () -> componentService.findById(boostComponentId)
        );
        assertThat(boostComponent).isNotNull();
        assertThat(boostComponent.name()).isEqualTo("boost-libraries");
        assertThat(boostComponent.branches()).isNotNull();
        assertThat(boostComponent.branches().size()).isEqualTo(1);
        assertThat(boostComponent.branches().getFirst().branchName()).isEqualTo("add-new-channel");
        assertThat(boostComponent.branches().getFirst().branchPoint()).isEqualTo("main");
        assertThat(boostComponent.branches().getFirst().type()).isEqualTo("feature");
    }

    @Test
    public void createBrancFailWithSameBranch() {
        var boostComponentId = assertDoesNotThrow(
                () -> componentService.create(
                        NewComponentDTO
                                .builder()
                                .name("boost libraries")
                                .description("boost libraries for c++ applications")
                                .organization("boost")
                                .url("https://www.boost.org/")
                                .approvalRule("rule1")
                                .testingCriteria("criteria1")
                                .approvalIdentity(Set.of("user1@slac.stanford.edu"))
                                .build()
                )
        );
        assertThat(boostComponentId).isNotNull();


        var branchAddResult = assertDoesNotThrow(
                () -> componentService.addNewBranch(
                        "boost-libraries",
                        BranchDTO
                                .builder()
                                .type("feature")
                                .branchName("add-new-channel")
                                .branchPoint("main")
                                .build()
                )
        );
        assertThat(branchAddResult).isNotNull();
        assertThat(branchAddResult).isTrue();

        ControllerLogicException branchAlreadyExists = assertThrows(
                ControllerLogicException.class,
                () -> componentService.addNewBranch(
                        "boost-libraries",
                        BranchDTO
                                .builder()
                                .type("feature")
                                .branchName("add-new-channel")
                                .branchPoint("main")
                                .build()
                )
        );

        assertThat(branchAlreadyExists).isNotNull();
        assertThat(branchAlreadyExists.getErrorCode()).isEqualTo(-2);
    }
}
