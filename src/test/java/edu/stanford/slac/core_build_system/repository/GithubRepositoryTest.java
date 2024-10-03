package edu.stanford.slac.core_build_system.repository;

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
//    @Autowired
//    private CoreBuildProperties coreBuildProperties;
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
//                () -> gitServerRepository.enableEvent(customAppComponent.get(), "https://example.com", "push")
//        );
//    }
}
