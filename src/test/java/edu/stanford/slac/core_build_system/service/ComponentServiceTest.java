package edu.stanford.slac.core_build_system.service;

import edu.stanford.slac.core_build_system.api.v1.dto.*;
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
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@AutoConfigureMockMvc
@SpringBootTest()
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
@ActiveProfiles({"test"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class ComponentServiceTest {
    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    ComponentService componentService;
    @Autowired
    CommandTemplateService commandTemplateService;
    private String copyCommandId;
    private String downloadCommandId;

    @BeforeAll
    public void initCommand() {
        mongoTemplate.remove(new Query(), CommandTemplate.class);
        copyCommandId = assertDoesNotThrow(
                () ->
                        commandTemplateService.create(
                                NewCommandTemplateDTO.builder()
                                        .name("copy")
                                        .description("copy file or directory from source to destination")
                                        .parameters(
                                                Set.of(
                                                        CommandTemplateParameterDTO.builder()
                                                                .name("source")
                                                                .description("the source file or directory")
                                                                .build(),
                                                        CommandTemplateParameterDTO.builder()
                                                                .name("destination_dir")
                                                                .description("the destination directory")
                                                                .build()
                                                )
                                        )
                                        .commandExecutionsLayers(
                                                Set.of(
                                                        ExecutionPipelineDTO.builder()
                                                                .engine("shell")
                                                                .architecture(List.of("linux"))
                                                                .operatingSystem(List.of("ubuntu", "redhat"))
                                                                .executionCommands(List.of("cp ${source} ${destination_dir}"))
                                                                .build()
                                                )
                                        )
                                        .build()
                        )
        );
        downloadCommandId = assertDoesNotThrow(
                () ->
                        commandTemplateService.create(
                                NewCommandTemplateDTO.builder()
                                        .name("download")
                                        .description("download file from source to destination folder")
                                        .parameters(
                                                Set.of(
                                                        CommandTemplateParameterDTO.builder()
                                                                .name("source_url")
                                                                .description("the source url")
                                                                .build(),
                                                        CommandTemplateParameterDTO.builder()
                                                                .name("destination_dir")
                                                                .description("the destination directory")
                                                                .build()
                                                )
                                        )
                                        .commandExecutionsLayers(
                                                Set.of(
                                                        ExecutionPipelineDTO.builder()
                                                                .engine("shell")
                                                                .architecture(List.of("linux"))
                                                                .operatingSystem(List.of("ubuntu", "redhat"))
                                                                .executionCommands(
                                                                        List.of(
                                                                                "mkdir -P ${destination_dir}",
                                                                                "curl -o ${destination_dir} ${source_url}"
                                                                        )
                                                                )
                                                                .build()
                                                )
                                        )
                                        .build()
                        )
        );
    }

    @BeforeEach
    public void cleanCollection() {
        mongoTemplate.remove(new Query(), Component.class);
    }

    @Test
    public void createNewOK() {
        // add install package component
        var installComponentId = componentService.create(
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
        var newInstallCommandId = commandTemplateService.create(
                NewCommandTemplateDTO.builder()
                        .name("install_package")
                        .description("Install a specified package using the system's package manager.")
                        // this need the install_component component to be executed first
                        .dependOnComponentIds(Set.of(installComponentId))
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

        // install wget tools
        var wgetToolComponentId = componentService.create(
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

        // download Command
        var downloadCommandId = commandTemplateService.create(
                NewCommandTemplateDTO.builder()
                        .name("download")
                        .description("download file from source to destination folder")
                        // this need the install_component component to be executed first
                        .dependOnComponentIds(Set.of(wgetToolComponentId))
                        .parameters(Set.of(
                                CommandTemplateParameterDTO.builder()
                                        .name("url")
                                        .description("The URL of the file to download")
                                        .build(),
                                CommandTemplateParameterDTO.builder()
                                        .name("dest_folder")
                                        .description("The URL of the file to download")
                                        .build()
                        ))
                        .commandExecutionsLayers(Set.of(
                                ExecutionPipelineDTO.builder()
                                        .engine("shell")
                                        .architecture(List.of("linux"))
                                        .operatingSystem(List.of("any"))
                                        .executionCommands(List.of(
                                                "wget -O ${dest_folder} ${url}"
                                        ))
                                        .build()
                        ))
                        .build()
        );

        //unzip component
        var unzipToolComponentId = componentService.create(
                NewComponentDTO.builder()
                        .name("UnzipTool")
                        .description("Manages the installation of the unzip tool.")
                        .commandTemplatesInstances(
                                List.of(
                                        CommandTemplateInstanceDTO.builder()
                                                // the install command depends on the install_package component
                                                .id(newInstallCommandId)
                                                .parameters(Map.of("package_name", "unzip"))
                                                .build()
                                )
                        )
                        .build()
        );
        assertThat(newInstallCommandId).isNotNull();

        // unzip Command
        var unzipCommandId = commandTemplateService.create(
                NewCommandTemplateDTO.builder()
                        .name("download")
                        .description("download file from source to destination folder")
                        // this need the install_component component to be executed first
                        .dependOnComponentIds(Set.of(unzipToolComponentId))
                        .parameters(Set.of(
                                CommandTemplateParameterDTO.builder()
                                        .name("src_file")
                                        .description("The path of the file to unzip")
                                        .build(),
                                CommandTemplateParameterDTO.builder()
                                        .name("dest_folder")
                                        .description("The path of the destination folder")
                                        .build()
                        ))
                        .commandExecutionsLayers(Set.of(
                                ExecutionPipelineDTO.builder()
                                        .engine("shell")
                                        .architecture(List.of("linux"))
                                        .operatingSystem(List.of("any"))
                                        .executionCommands(List.of(
                                                "unzip ${src_file} -d ${dest_folder}"
                                        ))
                                        .build()
                        ))
                        .build()
        );



        var boostComponentId = componentService.create(
                NewComponentDTO
                        .builder()
                        .name("boost libraries")
                        .description("boost libraries for c++ applications")
                        .version("1_82_0")
                        .commandTemplatesInstances(
                                List.of(
                                        CommandTemplateInstanceDTO
                                                .builder()
                                                .id(downloadCommandId)
                                                .parameters(
                                                        Map.of(
                                                                "source_url", "https://boostorg.jfrog.io/artifactory/main/release/1.82.0/source/boost_1_82_0.zip",
                                                                "destination_dir", "/tmp/build/"
                                                        )
                                                )
                                                .build(),
                                        CommandTemplateInstanceDTO.builder()
                                                .id(unzipCommandId)
                                                .parameters(
                                                        Map.of(
                                                                "src_file", "/tmp/build/boost_1_82_0.zip",
                                                                "dest_folder", "/tmp/build/"
                                                        )
                                                )
                                                .build()
                                )
                        )
                        .commandTemplates(
                                List.of(
                                        CommandTemplateDTO
                                                .builder()
                                                .commandExecutionsLayers(
                                                        Set.of(
                                                                ExecutionPipelineDTO
                                                                        .builder()
                                                                        .engine("shell")
                                                                        .architecture(List.of("linux"))
                                                                        .operatingSystem(List.of("any"))
                                                                        .executionCommands(
                                                                                List.of(
                                                                                        "cd /tmp/build/boost_1_82_0 && ./bootstrap.sh --prefix=/usr/local",
                                                                                        "cd /tmp/build/boost_1_82_0 && ./b2 install"
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
        assertThat(boostComponentId).isNotNull();
    }
}
