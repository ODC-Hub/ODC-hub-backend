package com.odc.hub.filrouge.service;

import com.odc.hub.filrouge.dto.SprintVelocityResponse;
import com.odc.hub.filrouge.enums.WorkItemStatus;
import com.odc.hub.filrouge.model.WorkItemDocument;
import com.odc.hub.filrouge.repository.WorkItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VelocityService {

    private final WorkItemRepository workItemRepository;

    public SprintVelocityResponse computeSprintVelocity(String sprintId) {

        List<WorkItemDocument> items =
                workItemRepository.findBySprintId(sprintId);

        int completedEffort = items.stream()
                .filter(i -> i.getStatus() == WorkItemStatus.DONE)
                .mapToInt(WorkItemDocument::getEffort)
                .sum();

        return new SprintVelocityResponse(sprintId, completedEffort);
    }
}
