package com.odc.hub.fileRouge.controller;

import com.odc.hub.auth.security.JwtAuthenticationFilter;
import com.odc.hub.filrouge.controller.VelocityController;
import com.odc.hub.filrouge.dto.SprintVelocityResponse;
import com.odc.hub.filrouge.service.VelocityService;
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
        controllers = VelocityController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtAuthenticationFilter.class
        )
)
class VelocityControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VelocityService velocityService;

    @Test
    @WithMockUser(roles = "FORMATEUR")
    void getSprintVelocity_shouldReturnVelocity() throws Exception {

        SprintVelocityResponse response =
                new SprintVelocityResponse("s1", 12);

        when(velocityService.computeSprintVelocity("s1"))
                .thenReturn(response);

        mockMvc.perform(get("/api/filrouge/velocity/sprint/s1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sprintId").value("s1"))
                .andExpect(jsonPath("$.completedEffort").value(12));
    }
}
