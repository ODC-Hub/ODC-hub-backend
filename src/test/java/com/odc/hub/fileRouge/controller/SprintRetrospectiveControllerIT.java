package com.odc.hub.fileRouge.controller;

import com.odc.hub.filrouge.controller.SprintRetrospectiveController;
import com.odc.hub.filrouge.dto.SprintRetrospectiveResponse;
import com.odc.hub.filrouge.service.SprintRetrospectiveService;
import com.odc.hub.auth.security.JwtAuthenticationFilter;

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
        controllers = SprintRetrospectiveController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtAuthenticationFilter.class
        )
)
class SprintRetrospectiveControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SprintRetrospectiveService service;

    @Test
    @WithMockUser(roles = "ADMIN")
    void getSprintReport_shouldReturnRetrospective() throws Exception {

        SprintRetrospectiveResponse response =
                new SprintRetrospectiveResponse(
                        "s1", 10, 5, 1, 1, 50.0
                );

        when(service.generate("s1")).thenReturn(response);

        mockMvc.perform(get("/api/filrouge/retrospective/sprint/s1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sprintId").value("s1"))
                .andExpect(jsonPath("$.completedEffort").value(5))
                .andExpect(jsonPath("$.reliabilityScore").value(50.0));
    }
}
