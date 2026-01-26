package com.odc.hub.filrouge.service;

import com.odc.hub.filrouge.dto.SprintRetrospectiveResponse;
import com.odc.hub.filrouge.enums.WorkItemStatus;
import com.odc.hub.filrouge.model.SprintDocument;
import com.odc.hub.filrouge.model.WorkItemDocument;
import com.odc.hub.filrouge.repository.SprintRepository;
import com.odc.hub.filrouge.repository.WorkItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SprintRetrospectiveService {

    private final SprintRepository sprintRepository;
    private final WorkItemRepository workItemRepository;

    public SprintRetrospectiveResponse generate(String sprintId) {

        SprintDocument sprint = sprintRepository.findById(sprintId)
                .orElseThrow(() -> new IllegalStateException("Sprint not found"));

        List<WorkItemDocument> items =
                workItemRepository.findBySprintId(sprintId);

        int completedEffort = items.stream()
                .filter(i -> i.getStatus() == WorkItemStatus.DONE)
                .mapToInt(WorkItemDocument::getEffort)
                .sum();

        int carryoverTasks = (int) items.stream()
                .filter(i -> i.getCarryCount() > 0)
                .count();

        int overdueTasks = (int) items.stream()
                .filter(i ->
                        i.getDeadline().isBefore(Instant.now()) &&
                                i.getStatus() != WorkItemStatus.DONE
                )
                .count();

        double reliability =
                sprint.getPlannedEffort() == 0 ? 0 :
                        (double) completedEffort / sprint.getPlannedEffort() * 100;

        return new SprintRetrospectiveResponse(
                sprintId,
                sprint.getPlannedEffort(),
                completedEffort,
                carryoverTasks,
                overdueTasks,
                reliability
        );
    }
}
