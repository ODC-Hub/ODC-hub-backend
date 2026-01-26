package com.odc.hub.fileRouge.service;

import com.odc.hub.filrouge.dto.SprintRetrospectiveResponse;
import com.odc.hub.filrouge.enums.WorkItemStatus;
import com.odc.hub.filrouge.model.SprintDocument;
import com.odc.hub.filrouge.model.WorkItemDocument;
import com.odc.hub.filrouge.repository.SprintRepository;
import com.odc.hub.filrouge.repository.WorkItemRepository;
import com.odc.hub.filrouge.service.SprintRetrospectiveService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SprintRetrospectiveServiceTest {

    @Mock
    SprintRepository sprintRepository;

    @Mock
    WorkItemRepository workItemRepository;

    @InjectMocks
    SprintRetrospectiveService service;

    @Test
    void generate_shouldComputeRetrospective() {
        SprintDocument sprint = new SprintDocument();
        sprint.setId("s1");
        sprint.setPlannedEffort(10);

        when(sprintRepository.findById("s1"))
                .thenReturn(Optional.of(sprint));

        WorkItemDocument done = new WorkItemDocument();
        done.setEffort(5);
        done.setStatus(WorkItemStatus.DONE);
        done.setCarryCount(0); // ✅ IMPORTANT
        done.setDeadline(Instant.now().plusSeconds(3600));

        WorkItemDocument carryover = new WorkItemDocument();
        carryover.setEffort(3);
        carryover.setCarryCount(1);
        carryover.setStatus(WorkItemStatus.TODO);
        carryover.setDeadline(Instant.now().minusSeconds(3600));

        when(workItemRepository.findBySprintId("s1"))
                .thenReturn(List.of(done, carryover));

        SprintRetrospectiveResponse response =
                service.generate("s1");

        assertThat(response.completedEffort()).isEqualTo(5);
        assertThat(response.carryoverTasks()).isEqualTo(1);
        assertThat(response.overdueTasks()).isEqualTo(1);
        assertThat(response.reliabilityScore()).isEqualTo(50.0);
    }

}
