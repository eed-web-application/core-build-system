package edu.stanford.slac.core_build_system.repository;

import edu.stanford.slac.core_build_system.api.v1.dto.ComponentDependencyDTO;
import edu.stanford.slac.core_build_system.api.v1.dto.NewComponentDTO;
import edu.stanford.slac.core_build_system.model.Component;
import edu.stanford.slac.core_build_system.service.ComponentService;
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

import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

//@AutoConfigureMockMvc
//@SpringBootTest()
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//@ExtendWith(MockitoExtension.class)
//@ActiveProfiles({"test"})
//@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class GithubRepositoryTest {
//    @Autowired
//    private GitServerRepository gitServerRepository;
//    @Autowired
//    private ComponentRepository componentRepository;
//    @Autowired
//    private MongoTemplate mongoTemplate;
//    @Autowired
//    private ComponentService componentService;
//
//    @BeforeEach
//    public void clean() {
//        mongoTemplate.remove(new Query(), Component.class);
//    }
//
//    @Test
//    public void testGithub() {
//        var customAppComponentId = assertDoesNotThrow(
//                () -> componentService.create(
//                        NewComponentDTO
//                                .builder()
//                                .name("test-ioc")
//                                .description("custom app 1 for c++ applications")
//                                .organization("custom")
//                                .url("https://github.com/ad-build-test/test-ioc")
//                                .approvalRule("rule1")
//                                .testingCriteria("criteria1")
//                                .approvalIdentity(Set.of("user1@slac.stanford.edu"))
//                                .build()
//                )
//        );
//        assertThat(customAppComponentId).isNotNull();
//        // get full component
//        var customAppComponent = assertDoesNotThrow(
//                () -> componentRepository.findById(customAppComponentId)
//        );
//        assertDoesNotThrow(
//                () -> gitServerRepository.downLoadRepository(customAppComponent.get(), "main", "/tmp/custom-app-1")
//        );
//    }
}
