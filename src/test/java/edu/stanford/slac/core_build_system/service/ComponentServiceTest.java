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
                                                                        .operatingSystem(List.of("ubuntu", "redhat"))
                                                                        .executionCommands(
                                                                                List.of(
                                                                                        "unzip /tmp/build/boost_1_82_0.zip -d /tmp/build/",
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
