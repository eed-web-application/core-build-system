package edu.stanford.slac.core_build_system;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.stanford.slac.ad.eed.baselib.api.v1.dto.ApiResultResponse;
import edu.stanford.slac.core_build_system.api.v1.dto.ComponentDTO;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.http.MediaType;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest()
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
@ActiveProfiles(profiles = "test")
public class ComponentControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void fetchQueryParameter() throws Exception {
        MvcResult get_result = mockMvc.perform(
                        get("/v1/component")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
        ApiResultResponse<List<ComponentDTO>> api_result = new ObjectMapper().readValue(
                get_result.getResponse().getContentAsString(),
                new TypeReference<>() {
                });

        assertThat(api_result).isNotNull();
        assertThat(api_result.getErrorCode()).isEqualTo(0);
        assertThat(api_result.getPayload())
                .hasSize(3)
                .extracting(ComponentDTO::id)
                .contains("1", "2", "3");
    }
}
