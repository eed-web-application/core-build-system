package edu.stanford.slac.core_build_system.controller;

import edu.stanford.slac.core_build_system.api.v1.dto.ComponentDTO;
import edu.stanford.slac.core_build_system.api.v1.dto.NewComponentDTO;
import edu.stanford.slac.core_build_system.model.CommandTemplate;
import edu.stanford.slac.core_build_system.model.Component;
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
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.Set;


import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@AutoConfigureMockMvc
@SpringBootTest()
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
@ActiveProfiles({"test"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class ComponentControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private TestControllerHelperService testControllerHelperService;
    @BeforeEach
    public void cleanCollection() {
        mongoTemplate.remove(new Query(), Component.class);
        mongoTemplate.remove(new Query(), CommandTemplate.class);
    }

    @Test
    public void createNewComponentOK() throws Exception {
        var result = testControllerHelperService.componentControllerCreate(
                mockMvc,
                status().isCreated(),
                Optional.empty(),
                NewComponentDTO
                        .builder()
                        .name("name")
                        .version("version")
                        .url("url")
                        .buildCommandTemplateIds(Set.of("id1", "id2"))
                        .build()
        );
        assertThat(result.getPayload()).isNotEmpty();
    }

    @Test
    public void findAll() throws Exception {
        var result1 = testControllerHelperService.componentControllerCreate(
                mockMvc,
                status().isCreated(),
                Optional.empty(),
                NewComponentDTO
                        .builder()
                        .name("name1")
                        .version("version1")
                        .url("url")
                        .buildCommandTemplateIds(Set.of("id1", "id2"))
                        .build()
        );
        assertThat(result1.getPayload()).isNotEmpty();
        var result2 = testControllerHelperService.componentControllerCreate(
                mockMvc,
                status().isCreated(),
                Optional.empty(),
                NewComponentDTO
                        .builder()
                        .name("name2")
                        .version("version2")
                        .url("url")
                        .buildCommandTemplateIds(Set.of("id1", "id2"))
                        .build()
        );
        assertThat(result2.getPayload()).isNotEmpty();

        var findAllResult = testControllerHelperService.componentControllerFindAll(
                mockMvc,
                status().isOk(),
                Optional.empty()
        );
        assertThat(findAllResult.getPayload())
                .hasSize(2)
                .extracting(ComponentDTO::name)
                .contains("name1", "name2");

    }
}
