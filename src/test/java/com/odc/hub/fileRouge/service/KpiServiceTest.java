package com.odc.hub.fileRouge.service;

import com.odc.hub.filrouge.dto.ProjectKpiResponse;
import com.odc.hub.filrouge.dto.SprintKpiResponse;
import com.odc.hub.filrouge.enums.WorkItemStatus;
import com.odc.hub.filrouge.model.SprintDocument;
import com.odc.hub.filrouge.model.WorkItemDocument;
import com.odc.hub.filrouge.repository.SprintRepository;
import com.odc.hub.filrouge.repository.WorkItemRepository;
import com.odc.hub.filrouge.service.KpiService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KpiServiceTest {

    @Mock
    SprintRepository sprintRepository;

    @Mock
    WorkItemRepository workItemRepository;

    @InjectMocks
    KpiService kpiService;

    @Test
    void computeProjectKpis_shouldAggregateSprintKpis() {
        SprintDocument sprint = new SprintDocument();
        sprint.setId("s1");
        sprint.setPlannedEffort(10);

        when(sprintRepository.findByProjectId("p1"))
                .thenReturn(List.of(sprint));

        WorkItemDocument done = new WorkItemDocument();
        done.setEffort(6);
        done.setStatus(WorkItemStatus.DONE);
        done.setDeadline(Instant.now().minusSeconds(3600));

        WorkItemDocument todo = new WorkItemDocument();
        todo.setEffort(4);
        todo.setStatus(WorkItemStatus.TODO);
        todo.setDeadline(Instant.now().plusSeconds(3600));

        when(workItemRepository.findBySprintId("s1"))
                .thenReturn(List.of(done, todo));

        ProjectKpiResponse response =
                kpiService.computeProjectKpis("p1");

        assertThat(response.globalProgress()).isEqualTo(60.0);
        assertThat(response.sprintKpis()).hasSize(1);

        SprintKpiResponse sprintKpi = response.sprintKpis().get(0);

        assertThat(sprintKpi.completedEffort()).isEqualTo(6);
        assertThat(sprintKpi.overdueItems()).isEqualTo(0);
        assertThat(sprintKpi.progressPercentage()).isEqualTo(60.0);

    }
}
