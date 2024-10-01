package edu.stanford.slac.core_build_system.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.stanford.slac.ad.eed.baselib.api.v1.dto.ApiResultResponse;
import edu.stanford.slac.ad.eed.baselib.auth.JWTHelper;
import edu.stanford.slac.ad.eed.baselib.config.AppProperties;
import edu.stanford.slac.core_build_system.api.v1.dto.*;
import edu.stanford.slac.core_build_system.model.Component;
import org.mapstruct.Builder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@Service()
public class TestControllerHelperService {
    private final JWTHelper jwtHelper;
    private final AppProperties appProperties;

    public TestControllerHelperService(JWTHelper jwtHelper, AppProperties appProperties) {
        this.jwtHelper = jwtHelper;
        this.appProperties = appProperties;

    }

    /**
     * Create a new component
     *
     * @param mockMvc         MockMvc
     * @param resultMatcher   ResultMatcher
     * @param userInfo        Optional<String>
     * @param newComponentDTO NewComponentDTO
     * @return ApiResultResponse<String>
     */
    public ApiResultResponse<String> componentControllerCreate(
            MockMvc mockMvc,
            ResultMatcher resultMatcher,
            Optional<String> userInfo,
            NewComponentDTO newComponentDTO) throws Exception {
        var requestBuilder = post("/v1/component")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(newComponentDTO));

        return executeHttpRequest(
                new TypeReference<>() {
                },
                mockMvc,
                resultMatcher,
                userInfo,
                requestBuilder
        );
    }

    /**
     * Find a component by an id
     *
     * @param mockMvc       MockMvc
     * @param resultMatcher ResultMatcher
     * @param userInfo      Optional<String>
     * @param id            String
     * @return ApiResultResponse<ComponentDTO>
     */
    public ApiResultResponse<ComponentDTO> componentControllerFindById(
            MockMvc mockMvc,
            ResultMatcher resultMatcher,
            Optional<String> userInfo,
            String id) throws Exception {
        var requestBuilder = get("/v1/component/{id}", id)
                .contentType(MediaType.APPLICATION_JSON);
        return executeHttpRequest(
                new TypeReference<>() {
                },
                mockMvc,
                resultMatcher,
                userInfo,
                requestBuilder
        );
    }

    /**
     * Delete a component by his id
     *
     * @param mockMvc       MockMvc
     * @param resultMatcher ResultMatcher
     * @param userInfo      Optional<String>
     * @param id            String
     * @return ApiResultResponse<Boolean>
     */
    public ApiResultResponse<Boolean> componentControllerDeleteById(
            MockMvc mockMvc,
            ResultMatcher resultMatcher,
            Optional<String> userInfo,
            String id) throws Exception {
        var requestBuilder = delete("/v1/component/{id}", id)
                .contentType(MediaType.APPLICATION_JSON);
        return executeHttpRequest(
                new TypeReference<>() {
                },
                mockMvc,
                resultMatcher,
                userInfo,
                requestBuilder
        );
    }

    /**
     * Update a component by his id
     *
     * @param mockMvc            MockMvc
     * @param resultMatcher      ResultMatcher
     * @param userInfo           Optional<String>
     * @param id                 String
     * @param updateComponentDTO UpdateComponentDTO
     * @return ApiResultResponse<Boolean>
     */
    public ApiResultResponse<Boolean> componentControllerUpdateById(
            MockMvc mockMvc,
            ResultMatcher resultMatcher,
            Optional<String> userInfo,
            String id,
            UpdateComponentDTO updateComponentDTO) throws Exception {
        var requestBuilder = put("/v1/component/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(updateComponentDTO));
        return executeHttpRequest(
                new TypeReference<>() {
                },
                mockMvc,
                resultMatcher,
                userInfo,
                requestBuilder
        );
    }

    /**
     * List all components
     *
     * @param mockMvc       MockMvc
     * @param resultMatcher ResultMatcher
     * @param userInfo      Optional<String>
     * @return ApiResultResponse<List < ComponentSummaryDTO>>
     */
    public ApiResultResponse<List<ComponentSummaryDTO>> componentControllerFindAll(
            MockMvc mockMvc,
            ResultMatcher resultMatcher,
            Optional<String> userInfo) throws Exception {
        var requestBuilder = get("/v1/component");

        return executeHttpRequest(
                new TypeReference<>() {
                },
                mockMvc,
                resultMatcher,
                userInfo,
                requestBuilder
        );
    }

    /**
     * Create a new command
     *
     * @param mockMvc               MockMvc
     * @param resultMatcher         ResultMatcher
     * @param userInfo              Optional<String>
     * @param newCommandTemplateDTO NewCommandTemplateDTO
     * @return ApiResultResponse<String>
     */
    public ApiResultResponse<String> commandControllerCreate(
            MockMvc mockMvc,
            ResultMatcher resultMatcher,
            Optional<String> userInfo,
            NewCommandTemplateDTO newCommandTemplateDTO) throws Exception {
        var requestBuilder = post("/v1/command")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(newCommandTemplateDTO));

        return executeHttpRequest(
                new TypeReference<>() {
                },
                mockMvc,
                resultMatcher,
                userInfo,
                requestBuilder
        );
    }

    /**
     * Find a command by an id
     *
     * @param mockMvc       MockMvc
     * @param resultMatcher ResultMatcher
     * @param userInfo      Optional<String>
     * @param id            String
     * @return ApiResultResponse<CommandTemplateDTO>
     */
    public ApiResultResponse<CommandTemplateDTO> commandControllerFindById(
            MockMvc mockMvc,
            ResultMatcher resultMatcher,
            Optional<String> userInfo,
            String id) throws Exception {
        var requestBuilder = get("/v1/command/{id}", id)
                .contentType(MediaType.APPLICATION_JSON);
        return executeHttpRequest(
                new TypeReference<>() {
                },
                mockMvc,
                resultMatcher,
                userInfo,
                requestBuilder
        );
    }

    /**
     * Delete a command by his id
     *
     * @param mockMvc       MockMvc
     * @param resultMatcher ResultMatcher
     * @param userInfo      Optional<String>
     * @param id            String
     * @return ApiResultResponse<Boolean>
     */
    public ApiResultResponse<Boolean> commandControllerDeleteById(
            MockMvc mockMvc,
            ResultMatcher resultMatcher,
            Optional<String> userInfo,
            String id) throws Exception {
        var requestBuilder = delete("/v1/command/{id}", id)
                .contentType(MediaType.APPLICATION_JSON);
        return executeHttpRequest(
                new TypeReference<>() {
                },
                mockMvc,
                resultMatcher,
                userInfo,
                requestBuilder
        );
    }

    /**
     * Update a command by his id
     *
     * @param mockMvc                  MockMvc
     * @param resultMatcher            ResultMatcher
     * @param userInfo                 Optional<String>
     * @param id                       String
     * @param updateCommandTemplateDTO UpdateCommandTemplateDTO
     * @return ApiResultResponse<Boolean>
     */
    public ApiResultResponse<Boolean> commandControllerUpdateById(
            MockMvc mockMvc,
            ResultMatcher resultMatcher,
            Optional<String> userInfo,
            String id,
            UpdateCommandTemplateDTO updateCommandTemplateDTO) throws Exception {
        var requestBuilder = put("/v1/command/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(updateCommandTemplateDTO));
        return executeHttpRequest(
                new TypeReference<>() {
                },
                mockMvc,
                resultMatcher,
                userInfo,
                requestBuilder
        );
    }

    /**
     * List all commands
     *
     * @param mockMvc       MockMvc
     * @param resultMatcher ResultMatcher
     * @param userInfo      Optional<String>
     * @return ApiResultResponse<List < CommandTemplateSummaryDTO>>
     */
    public ApiResultResponse<List<CommandTemplateSummaryDTO>> commandControllerFindAll(
            MockMvc mockMvc,
            ResultMatcher resultMatcher,
            Optional<String> userInfo) throws Exception {
        var requestBuilder = get("/v1/command");

        return executeHttpRequest(
                new TypeReference<>() {
                },
                mockMvc,
                resultMatcher,
                userInfo,
                requestBuilder
        );
    }

    /**
     * Generate component artifact using a specific engine
     */
    public DownloadedFile engineControllerGenerateComponentArtifact(
            MockMvc mockMvc,
            ResultMatcher resultMatcher,
            Optional<String> userInfo,
            String engineName,
            List<String> componentIds,
            Optional<Map<String, String>> buildSpec) throws Exception {
        var requestBuilder = get("/v1/engine/generate");
        requestBuilder.param("engineName", engineName);
        componentIds.forEach(componentId -> requestBuilder.param("componentId", componentId));
        buildSpec.ifPresent(spec -> spec.forEach(requestBuilder::param));
        return executeHttpDownloadFIle(
                mockMvc,
                resultMatcher,
                userInfo,
                requestBuilder
        );
    }

    /**
     * Return all engine name
     */
    public ApiResultResponse<Set<String>> engineControllerFindAllName(
            MockMvc mockMvc,
            ResultMatcher resultMatcher,
            Optional<String> userInfo) throws Exception {
        var requestBuilder = get("/v1/engine/all");

        return executeHttpRequest(
                new TypeReference<>() {
                },
                mockMvc,
                resultMatcher,
                userInfo,
                requestBuilder
        );
    }

    /**
     * Create a new branch
     */
    public ApiResultResponse<Boolean> componentControllerCreateNewBranch(
            MockMvc mockMvc,
            ResultMatcher resultMatcher,
            Optional<String> userInfo,
            String componentName,
            BranchDTO branch
    ) throws Exception {
        var requestBuilder = put("/v1/component/{componentName}/branch", componentName)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(branch));
        return executeHttpRequest(
                new TypeReference<>() {
                },
                mockMvc,
                resultMatcher,
                userInfo,
                requestBuilder
        );
    }

    /**
     * Create a new branch
     */
    public ApiResultResponse<String> eventControllerHandlePushNewBranchEvent(
            MockMvc mockMvc,
            ResultMatcher resultMatcher,
            String componentToken,
            String projectUrl,
            String branchName
    ) throws Exception {
        String resolvedJson = loadAndResolveJson("push-new-branch.json", Map.of(
                "branch-name", branchName,
                "project_url", projectUrl
        ));
        return eventControllerHandlePushEvent(
                mockMvc,
                resultMatcher,
                generateSignature(resolvedJson, componentToken),
                "push",
                resolvedJson
        );
    }

    /**
     * Create a new branch
     */
    public ApiResultResponse<String> eventControllerHandlePREvent(
            MockMvc mockMvc,
            ResultMatcher resultMatcher,
            String componentToken,
            String projectUrl,
            String branchName
    ) throws Exception {
        String resolvedJson = loadAndResolveJsonFlat("pull-request-created.json", Map.of(
                "branch-name", branchName,
                "project_url", projectUrl
        ));
        return eventControllerHandlePushEvent(
                mockMvc,
                resultMatcher,
                generateSignature(resolvedJson, componentToken),
                "push",
                resolvedJson
        );
    }

    public ApiResultResponse<String> eventControllerHandlePREvent(
            MockMvc mockMvc,
            ResultMatcher resultMatcher,
            String componentToken,
            Map<String, Object> keyValueMap
    ) throws Exception {
        String resolvedJson = loadAndResolveJson("pull-request-created.json", keyValueMap);
        return eventControllerHandlePushEvent(
                mockMvc,
                resultMatcher,
                generateSignature(resolvedJson, componentToken),
                "pull_request",
                resolvedJson
        );
    }

    /**
     * Load a json file and resolve the values
     */
    private String loadAndResolveJsonFlat(String filePath, Map<String, String> valuesMap) throws Exception {
        ClassPathResource resource = new ClassPathResource(filePath);
        String jsonTemplate = new String(Files.readAllBytes(resource.getFile().toPath()));
        for (Map.Entry<String, String> entry : valuesMap.entrySet()) {
            jsonTemplate = jsonTemplate.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return jsonTemplate;
    }

    /**
     * Load a json file and resolve the values
     */
    private String loadAndResolveJson(String filePath, Map<String, Object> valuesMap) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        ClassPathResource resource = new ClassPathResource(filePath);
        String jsonTemplate = new String(Files.readAllBytes(resource.getFile().toPath()));
        Map<String, Object> map = objectMapper.readValue(jsonTemplate, new TypeReference<Map<String, Object>>() {
        });
        for (Map.Entry<String, Object> entry : valuesMap.entrySet()) {
            // if key is pointed separated name each part is a key that contains an object
            // so we need to go deeper on the hasmap
            if (entry.getKey().contains(".")) {
                String[] keys = entry.getKey().split("\\.");
                Map<String, Object> currentMap = map;
                for (int i = 0; i < keys.length - 1; i++) {
                    currentMap = (Map<String, Object>) currentMap.get(keys[i]);
                }
                currentMap.put(keys[keys.length - 1], entry.getValue());
            } else {
                map.put(entry.getKey(), entry.getValue());
            }
        }
        //now re-convert the hashmap to json
        return objectMapper.writeValueAsString(map);
    }

    private String generateSignature(String payload, String secret) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        mac.init(secretKeySpec);
        byte[] hash = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
        return "sha256=" + bytesToHex(hash);
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public ApiResultResponse<String> eventControllerHandlePushEvent(
            MockMvc mockMvc,
            ResultMatcher resultMatcher,
            String signature,
            String event,
            String payload
    ) throws Exception {
        var requestBuilder = post("/v1/event/gh/webhook")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Hub-Signature-256", signature)
                .header("X-GitHub-Event", event)
                .content(payload);
        return executeHttpRequest(
                new TypeReference<>() {
                },
                mockMvc,
                resultMatcher,
                Optional.empty(),
                requestBuilder
        );
    }

    /**
     * Create a new build
     */
    public ApiResultResponse<List<String>> buildControllerStartNewBuild(
            MockMvc mockMvc,
            ResultMatcher resultMatcher,
            String componentName,
            String branchName,
            Optional<Map<String, String>> buildVariables
    ) throws Exception {
        var requestBuilder = post("/v1/build/component/{componentName}/branch/{branchName}", componentName, branchName)
                .contentType(MediaType.APPLICATION_JSON);
        // add build variable as headers
        buildVariables.ifPresent(vp -> vp.forEach(requestBuilder::header));
        // executed post request
        return executeHttpRequest(
                new TypeReference<>() {
                },
                mockMvc,
                resultMatcher,
                Optional.empty(),
                requestBuilder
        );
    }

    public ApiResultResponse<List<ComponentBranchBuildSummaryDTO>> buildControllerFindByComponentNameAndBranch(
            MockMvc mockMvc,
            ResultMatcher resultMatcher,
            String componentName,
            String branchName
    ) throws Exception {
        var requestBuilder = get("/v1/build/component/{componentName}/branch/{branchName}", componentName, branchName)
                .contentType(MediaType.APPLICATION_JSON);
        return executeHttpRequest(
                new TypeReference<>() {
                },
                mockMvc,
                resultMatcher,
                Optional.empty(),
                requestBuilder
        );
    }

    public ApiResultResponse<ComponentBranchBuildDTO> buildControllerFindBuildById(
            MockMvc mockMvc,
            ResultMatcher resultMatcher,
            String buildId
    ) throws Exception {
        var requestBuilder = get("/v1/build/{buildId}", buildId)
                .contentType(MediaType.APPLICATION_JSON);
        return executeHttpRequest(
                new TypeReference<>() {
                },
                mockMvc,
                resultMatcher,
                Optional.empty(),
                requestBuilder
        );
    }

    public ApiResultResponse<Boolean> buildControllerDeleteBuildById(
            MockMvc mockMvc,
            ResultMatcher resultMatcher,
            String buildId
    ) throws Exception {
        var requestBuilder = delete("/v1/build/{buildId}", buildId)
                .contentType(MediaType.APPLICATION_JSON);
        return executeHttpRequest(
                new TypeReference<>() {
                },
                mockMvc,
                resultMatcher,
                Optional.empty(),
                requestBuilder
        );
    }

    public ApiResultResponse<List<LogEntryDTO>> buildControllerFindLogByBuildId(
            MockMvc mockMvc,
            ResultMatcher resultMatcher,
            String buildId
    ) throws Exception {
        var requestBuilder = get("/v1/build/{buildId}/log", buildId)
                .contentType(MediaType.APPLICATION_JSON);
        return executeHttpRequest(
                new TypeReference<>() {
                },
                mockMvc,
                resultMatcher,
                Optional.empty(),
                requestBuilder
        );
    }

    /**
     * Return all builds for a component/branch
     */
    public <T> ApiResultResponse<T> executeHttpRequest(
            TypeReference<ApiResultResponse<T>> typeRef,
            MockMvc mockMvc,
            ResultMatcher resultMatcher,
            Optional<String> userInfo,
            MockHttpServletRequestBuilder requestBuilder) throws Exception {
        userInfo.ifPresent(login -> requestBuilder.header(appProperties.getUserHeaderName(), jwtHelper.generateJwt(login)));

        MvcResult result = mockMvc.perform(requestBuilder)
                .andExpect(resultMatcher)
                .andReturn();

        if (result.getResolvedException() != null) {
            throw result.getResolvedException();
        }
        String contentType = result.getResponse().getContentType();

        // Assume JSON response for ApiResultResponse parsing
        return new ObjectMapper().readValue(result.getResponse().getContentAsString(), typeRef);
    }

    public DownloadedFile executeHttpDownloadFIle(MockMvc mockMvc,
                                                  ResultMatcher resultMatcher,
                                                  Optional<String> userInfo,
                                                  MockHttpServletRequestBuilder requestBuilder) throws Exception {
        userInfo.ifPresent(login -> requestBuilder.header(appProperties.getUserHeaderName(), jwtHelper.generateJwt(login)));

        MvcResult result = mockMvc.perform(requestBuilder)
                .andExpect(resultMatcher)
                .andReturn();

        if (result.getResolvedException() != null) {
            throw result.getResolvedException();
        }
        String contentType = result.getResponse().getContentType();
        byte[] fileContent = result.getResponse().getContentAsByteArray();
        String contentDisposition = result.getResponse().getHeader("Content-Disposition");
        String fileName = null;
        if (contentDisposition != null && contentDisposition.contains("filename=")) {
            fileName = contentDisposition.split("filename=")[1];
            if (fileName.startsWith("\"")) {
                fileName = fileName.substring(1, fileName.length() - 1);
            }
        }
        // Wrap the binary data in ApiResultResponse and return
        return new DownloadedFile(fileContent, fileName);
    }

    public static class DownloadedFile {
        private final byte[] content;
        private final String fileName;

        public DownloadedFile(byte[] content, String fileName) {
            this.content = content;
            this.fileName = fileName;
        }

        public byte[] getContent() {
            return content;
        }

        public String getFileName() {
            return fileName;
        }
    }
}
