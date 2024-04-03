package edu.stanford.slac.core_build_system.service;

import edu.stanford.slac.core_build_system.api.v1.dto.*;
import edu.stanford.slac.core_build_system.model.CommandTemplate;
import edu.stanford.slac.core_build_system.model.Component;
import edu.stanford.slac.core_build_system.repository.CommandTemplateRepository;
import edu.stanford.slac.core_build_system.repository.ComponentRepository;
import edu.stanford.slac.core_build_system.service.engine.DockerEngineBuilder;
import edu.stanford.slac.core_build_system.service.engine.EngineBuilder;
import org.junit.jupiter.api.BeforeAll;
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

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@AutoConfigureMockMvc
@SpringBootTest()
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
@ActiveProfiles({"test"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class DockerEngineTest {
    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    ComponentRepository componentRepository;
    @Autowired
    ComponentService componentService;
    @Autowired
    CommandTemplateService commandTemplateService;
    @Autowired
    EngineBuilder dockerEngineBuilder;
    private String wgetToolComponentId;
    private String installComponentId;
    private String newInstallCommandId;

    @BeforeAll
    public void initCommandsAndComponent() {
        mongoTemplate.remove(new Query(), CommandTemplate.class);
        mongoTemplate.remove(new Query(), Component.class);

        installComponentId = componentService.create(
                NewComponentDTO.builder()
                        .name("install_component")
                        .description("Initialize the package manager and install the package")
                        .commandTemplates(
                                List.of(
                                        CommandTemplateDTO.builder()
                                                .commandExecutionsLayers(
                                                        Set.of(
                                                                ExecutionPipelineDTO.builder()
                                                                        .engine("shell")
                                                                        .architecture(List.of("linux"))
                                                                        .operatingSystem(List.of("ubuntu", "debian"))
                                                                        .executionCommands(
                                                                                List.of(
                                                                                        "apt-get update"
                                                                                )
                                                                        )
                                                                        .build(),
                                                                ExecutionPipelineDTO.builder()
                                                                        .engine("shell")
                                                                        .architecture(List.of("linux"))
                                                                        .operatingSystem(List.of("redhat", "centos"))
                                                                        .executionCommands(
                                                                                List.of(
                                                                                        "yum update"
                                                                                )
                                                                        )
                                                                        .build()
                                                        )
                                                )
                                                .build()
                                )
                        )
                        .build()
        );
        assertThat(installComponentId).isNotNull();

        // create command that permit the installation of tools
        newInstallCommandId = commandTemplateService.create(
                NewCommandTemplateDTO.builder()
                        .name("install_package")
                        .description("Install a specified package using the system's package manager.")
                        // this need the install_component component to be executed first
                        .dependOnComponents(Set.of(installComponentId))
                        .parameters(Set.of(
                                CommandTemplateParameterDTO.builder()
                                        .name("package_name")
                                        .description("The name of the package to install")
                                        .build()
                        ))
                        .commandExecutionsLayers(Set.of(
                                ExecutionPipelineDTO.builder()
                                        .engine("shell")
                                        .architecture(List.of("linux"))
                                        .operatingSystem(List.of("ubuntu", "debian"))
                                        .executionCommands(List.of(
                                                "apt-get install -y ${package_name}"
                                        ))
                                        .build(),
                                ExecutionPipelineDTO.builder()
                                        .engine("shell")
                                        .architecture(List.of("linux"))
                                        .operatingSystem(List.of("redhat", "centos"))
                                        .executionCommands(List.of(
                                                "yum install -y ${package_name}"
                                        ))
                                        .build()
                        ))
                        .build()
        );
        assertThat(newInstallCommandId).isNotNull();
        wgetToolComponentId = componentService.create(
                NewComponentDTO.builder()
                        .name("WgetTool")
                        .description("Manages the installation of the wget tool.")
                        .commandTemplatesInstances(
                                List.of(
                                        CommandTemplateInstanceDTO.builder()
                                                // the install command depends on the install_package component
                                                .id(newInstallCommandId)
                                                .parameters(Map.of("package_name", "wget"))
                                                .build()
                                )
                        )
                        .build()
        );
        assertThat(wgetToolComponentId).isNotNull();
    }

    @Test
    public void testBaseDockerCreation() {
        var component = componentRepository.findById(wgetToolComponentId).get();
        String dockerfileUbuntuContent = dockerEngineBuilder
                .addComponent(component)
                .addBuilderSpec(DockerEngineBuilder.SPEC_OS_TYPE, "ubuntu")
                .build();
        assertThat(dockerfileUbuntuContent).isNotNull();
        assertThat(dockerfileUbuntuContent).contains("FROM ubuntu:latest");
        assertThat(dockerfileUbuntuContent).contains("RUN apt-get update");
        assertThat(dockerfileUbuntuContent).contains("RUN apt-get install -y wget");

        String dockerfileRedhatContent = dockerEngineBuilder
                .addComponent(component)
                .addBuilderSpec(DockerEngineBuilder.SPEC_OS_TYPE, "redhat")
                .build();
        assertThat(dockerfileUbuntuContent).isNotNull();
        assertThat(dockerfileRedhatContent).contains("FROM redhat:latest");
        assertThat(dockerfileRedhatContent).contains("RUN yum update");
        assertThat(dockerfileRedhatContent).contains("RUN yum install -y wget");
    }
}
