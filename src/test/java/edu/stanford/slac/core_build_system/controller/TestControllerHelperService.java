package edu.stanford.slac.core_build_system.controller;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.stanford.slac.ad.eed.baselib.api.v1.dto.ApiResultResponse;
import edu.stanford.slac.ad.eed.baselib.auth.JWTHelper;
import edu.stanford.slac.ad.eed.baselib.config.AppProperties;
import edu.stanford.slac.core_build_system.api.v1.dto.*;
import edu.stanford.slac.core_build_system.model.Component;
import org.mapstruct.Builder;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

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
