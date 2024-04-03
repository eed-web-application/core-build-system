package edu.stanford.slac.core_build_system.controller;

import edu.stanford.slac.ad.eed.baselib.api.v1.dto.ApiResultResponse;
import edu.stanford.slac.core_build_system.api.v1.dto.*;
import edu.stanford.slac.core_build_system.model.CommandTemplate;
import edu.stanford.slac.core_build_system.model.Component;
import edu.stanford.slac.core_build_system.service.CommandTemplateService;
import edu.stanford.slac.core_build_system.service.ComponentService;
import org.junit.jupiter.api.BeforeAll;
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
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest()
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
@ActiveProfiles({"test"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class EngineControllerTest {
    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    CommandTemplateService commandTemplateService;
    @Autowired
    ComponentService componentService;
    @Autowired
    TestControllerHelperService testControllerHelperService;
    @Autowired
    private MockMvc mockMvc;
    private String installComponentId;
    private String newInstallCommandId;
    private String wgetToolComponentId;

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
                                                .name("update_package_manager")
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
    public void testDockerArtifact() {
        var downloadedArtifact = assertDoesNotThrow(
                () -> testControllerHelperService.engineControllerGenerateComponentArtifact(
                        mockMvc,
                        status().isOk(),
                        Optional.empty(),
                        "docker",
                        List.of(wgetToolComponentId),
                        Optional.of(Map.of("osType", "ubuntu"))
                )
        );
        assertThat(downloadedArtifact).isNotNull();
        assertThat(downloadedArtifact.getFileName()).contains("Dockerfile");
        String content =  new String(downloadedArtifact.getContent());
        assertThat(content).contains("RUN apt-get update");
        assertThat(content).contains("RUN apt-get install -y wget");
    }

    @Test
    public void testAnsibleArtifact() {
        var downloadedArtifact = assertDoesNotThrow(
                () -> testControllerHelperService.engineControllerGenerateComponentArtifact(
                        mockMvc,
                        status().isOk(),
                        Optional.empty(),
                        "ansible",
                        List.of(wgetToolComponentId),
                        Optional.of(
                                Map.of(
                                        "osType", "ubuntu",
                                        "host","192.168.1.1"
                                )
                        )
                )
        );
        assertThat(downloadedArtifact).isNotNull();
        assertThat(downloadedArtifact.getFileName()).contains("ansible.yml");
        String content =  new String(downloadedArtifact.getContent());
        assertThat(content).contains("- hosts: 192.168.1.1");
        assertThat(content).contains("apt-get update");
        assertThat(content).contains("apt-get install -y wget");
    }

    @Test
    public void getAllName() {
        ApiResultResponse<Set<String>> engineNames = assertDoesNotThrow(
                () -> testControllerHelperService.engineControllerFindAllName(
                        mockMvc,
                        status().isOk(),
                        Optional.empty()
                )
        );
        assertThat(engineNames).isNotNull();
        assertThat(engineNames.getPayload()).isNotNull();
        assertThat(engineNames.getPayload()).contains("docker", "ansible");
    }
}
