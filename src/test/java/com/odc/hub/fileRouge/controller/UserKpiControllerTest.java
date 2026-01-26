package com.odc.hub.fileRouge.controller;

import com.odc.hub.auth.security.JwtAuthenticationFilter;
import com.odc.hub.filrouge.controller.UserKpiController;
import com.odc.hub.filrouge.dto.UserKpiResponse;
import com.odc.hub.filrouge.service.UserKpiService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = UserKpiController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtAuthenticationFilter.class
        )
)
class UserKpiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserKpiService userKpiService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void getUserKpis_shouldReturnUserKpis() throws Exception {

        UserKpiResponse response =
                new UserKpiResponse("u1", 20, 15, 1, 75.0);

        when(userKpiService.computeUserKpis("u1"))
                .thenReturn(response);

        mockMvc.perform(get("/api/filrouge/kpis/users/u1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value("u1"))
                .andExpect(jsonPath("$.deliveryScore").value(75.0));
    }
}
