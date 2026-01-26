package com.odc.hub.filrouge.service;

import com.odc.hub.filrouge.dto.UserKpiResponse;
import com.odc.hub.filrouge.enums.WorkItemStatus;
import com.odc.hub.filrouge.model.WorkItemDocument;
import com.odc.hub.filrouge.repository.WorkItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserKpiService {

    private final WorkItemRepository workItemRepository;

    public UserKpiResponse computeUserKpis(String userId) {

        List<WorkItemDocument> items =
                workItemRepository.findByAssignedUserIdsContaining(userId);

        int assignedEffort = items.stream()
                .mapToInt(WorkItemDocument::getEffort)
                .sum();

        int completedEffort = items.stream()
                .filter(i -> i.getStatus() == WorkItemStatus.DONE)
                .mapToInt(WorkItemDocument::getEffort)
                .sum();

        int overdueTasks = (int) items.stream()
                .filter(i ->
                        i.getDeadline().isBefore(Instant.now()) &&
                                i.getStatus() != WorkItemStatus.DONE
                )
                .count();

        double deliveryScore =
                assignedEffort == 0 ? 0 :
                        (double) completedEffort / assignedEffort * 100;

        return new UserKpiResponse(
                userId,
                assignedEffort,
                completedEffort,
                overdueTasks,
                deliveryScore
        );
    }
}
