package edu.stanford.slac.core_build_system.service;

import edu.stanford.slac.core_build_system.api.v1.dto.*;
import edu.stanford.slac.core_build_system.config.GitHubClient;
import edu.stanford.slac.core_build_system.exception.CommandTemplateNotFound;
import edu.stanford.slac.core_build_system.model.CommandTemplate;
import edu.stanford.slac.core_build_system.model.CommandTemplateParameter;
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

import java.util.List;
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
public class CommandServiceTest {
    @MockBean
    GitHubClient.GHInstancer ghInstancer;
    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    CommandTemplateService commandTemplateService;

    @BeforeEach
    public void cleanCollection() {
        mongoTemplate.remove(new Query(), CommandTemplate.class);
    }

    @Test
    public void createNewOK() {
        var newCommandTemplateId = assertDoesNotThrow(
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
        assertThat(newCommandTemplateId).isNotNull();
    }

    @Test
    public void updateOK() {
        var newCommandTemplateId = assertDoesNotThrow(
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
        assertDoesNotThrow(
                () -> commandTemplateService.updateById(
                        newCommandTemplateId,
                        UpdateCommandTemplateDTO.builder()
                                .name("copy updated")
                                .description("copy file or directory from source to destination updated")
                                .parameters(
                                        Set.of(
                                                CommandTemplateParameterDTO.builder()
                                                        .name("source updated")
                                                        .description("the source file or directory updated")
                                                        .build(),
                                                CommandTemplateParameterDTO.builder()
                                                        .name("destination_dir updated")
                                                        .description("the destination directory updated")
                                                        .build()
                                        )
                                )
                                .commandExecutionsLayers(
                                        Set.of(
                                                ExecutionPipelineDTO.builder()
                                                        .engine("shell updated")
                                                        .architecture(List.of("linux updated"))
                                                        .operatingSystem(List.of("ubuntu"))
                                                        .executionCommands(List.of("cp -R ${source} ${destination_dir}"))
                                                        .build()
                                        )
                                )
                                .build()
                )
        );

        var foundCommandTemplate = assertDoesNotThrow(
                ()->commandTemplateService.findById(newCommandTemplateId)
        );
        assertThat(foundCommandTemplate).isNotNull();
        assertThat(foundCommandTemplate.name()).isEqualTo("copy updated");
        assertThat(foundCommandTemplate.description()).isEqualTo("copy file or directory from source to destination updated");
        assertThat(foundCommandTemplate.parameters()).hasSize(2);
        assertThat(foundCommandTemplate.parameters().stream().map(CommandTemplateParameterDTO::name)).containsExactlyInAnyOrder("source updated", "destination_dir updated");
        assertThat(foundCommandTemplate.commandExecutionsLayers()).hasSize(1);
        assertThat(foundCommandTemplate.commandExecutionsLayers().stream().map(ExecutionPipelineDTO::engine)).containsExactlyInAnyOrder("shell updated");
    }

    @Test
    public void deleteOk() {
        var newCommandTemplateId = assertDoesNotThrow(
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
        assertThat(newCommandTemplateId).isNotNull();
        var foundCommandTemplate = assertDoesNotThrow(
                ()->commandTemplateService.findById(newCommandTemplateId)
        );
        assertThat(foundCommandTemplate).isNotNull();
        assertDoesNotThrow(
                ()->commandTemplateService.deleteById(newCommandTemplateId)
        );
        var notFoundCommandTemplate = assertThrows(
                CommandTemplateNotFound.class,
                ()->commandTemplateService.findById(newCommandTemplateId)
        );
        assertThat(notFoundCommandTemplate).isNotNull();
    }
}
