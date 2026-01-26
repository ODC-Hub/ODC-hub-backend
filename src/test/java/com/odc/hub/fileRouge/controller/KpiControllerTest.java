package com.odc.hub.fileRouge.controller;

import com.odc.hub.auth.service.JwtService;
import com.odc.hub.filrouge.controller.KpiController;
import com.odc.hub.filrouge.dto.ProjectKpiResponse;
import com.odc.hub.filrouge.service.KpiService;
import com.odc.hub.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(KpiController.class)
@AutoConfigureMockMvc(addFilters = false)
class KpiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private KpiService kpiService;

    // 🔐 Security dependencies (MANDATORY)
    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserRepository userRepository;

    @Test
    @WithMockUser(roles = "ADMIN")
    void getProjectKpis_shouldReturnProjectKpis() throws Exception {

        ProjectKpiResponse response =
                new ProjectKpiResponse("p1", 75.0, List.of());

        when(kpiService.computeProjectKpis("p1"))
                .thenReturn(response);

        mockMvc.perform(get("/api/filrouge/kpis/project/p1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.projectId").value("p1"))
                .andExpect(jsonPath("$.globalProgress").value(75.0));
    }
}
