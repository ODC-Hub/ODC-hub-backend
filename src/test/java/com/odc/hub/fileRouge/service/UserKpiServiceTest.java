package com.odc.hub.fileRouge.service;

import com.odc.hub.filrouge.dto.UserKpiResponse;
import com.odc.hub.filrouge.enums.WorkItemStatus;
import com.odc.hub.filrouge.model.WorkItemDocument;
import com.odc.hub.filrouge.repository.WorkItemRepository;
import com.odc.hub.filrouge.service.UserKpiService;
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
class UserKpiServiceTest {

    @Mock
    WorkItemRepository workItemRepository;

    @InjectMocks
    UserKpiService service;

    @Test
    void computeUserKpis_shouldComputeDeliveryScore() {
        WorkItemDocument done = new WorkItemDocument();
        done.setEffort(6);
        done.setStatus(WorkItemStatus.DONE);
        done.setDeadline(Instant.now().minusSeconds(3600));

        WorkItemDocument late = new WorkItemDocument();
        late.setEffort(4);
        late.setStatus(WorkItemStatus.TODO);
        late.setDeadline(Instant.now().minusSeconds(3600));

        when(workItemRepository.findByAssignedUserIdsContaining("u1"))
                .thenReturn(List.of(done, late));

        UserKpiResponse response =
                service.computeUserKpis("u1");

        assertThat(response.assignedEffort()).isEqualTo(10);
        assertThat(response.completedEffort()).isEqualTo(6);
        assertThat(response.overdueTasks()).isEqualTo(1);
        assertThat(response.deliveryScore()).isEqualTo(60.0);
    }
}
