package com.odc.hub.fileRouge.service;

import com.odc.hub.filrouge.dto.SprintVelocityResponse;
import com.odc.hub.filrouge.enums.WorkItemStatus;
import com.odc.hub.filrouge.model.WorkItemDocument;
import com.odc.hub.filrouge.repository.WorkItemRepository;
import com.odc.hub.filrouge.service.VelocityService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VelocityServiceTest {

    @Mock
    WorkItemRepository workItemRepository;

    @InjectMocks
    VelocityService service;

    @Test
    void computeSprintVelocity_shouldSumDoneEffort() {
        WorkItemDocument done1 = new WorkItemDocument();
        done1.setEffort(5);
        done1.setStatus(WorkItemStatus.DONE);

        WorkItemDocument done2 = new WorkItemDocument();
        done2.setEffort(3);
        done2.setStatus(WorkItemStatus.DONE);

        when(workItemRepository.findBySprintId("s1"))
                .thenReturn(List.of(done1, done2));

        SprintVelocityResponse response =
                service.computeSprintVelocity("s1");

        assertThat(response.completedEffort()).isEqualTo(8);
    }
}
