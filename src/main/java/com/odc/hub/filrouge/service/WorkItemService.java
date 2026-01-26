package com.odc.hub.filrouge.service;

import com.odc.hub.filrouge.dto.CreateWorkItemRequest;
import com.odc.hub.filrouge.dto.UpdateWorkItemStatusRequest;
import com.odc.hub.filrouge.enums.WorkItemStatus;
import com.odc.hub.filrouge.model.ProjectDocument;
import com.odc.hub.filrouge.model.WorkItemDocument;
import com.odc.hub.filrouge.repository.ProjectRepository;
import com.odc.hub.filrouge.repository.WorkItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class WorkItemService {

    private final WorkItemRepository workItemRepository;
    private final ProjectRepository projectRepository;

    /* CREATE WORK ITEM */
    public WorkItemDocument createWorkItem(
            String projectId,
            String sprintId,
            CreateWorkItemRequest request
    ) {

        if (request.effort() == null || request.effort() <= 0) {
            throw new IllegalStateException("Effort must be > 0");
        }

        if (request.deadline() == null ||
                request.deadline().isBefore(Instant.now())) {
            throw new IllegalStateException("Invalid deadline");
        }

        ProjectDocument project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalStateException("Project not found"));

        for (String userId : request.assignedUserIds()) {
            if (!project.getMembers().contains(userId)) {
                throw new IllegalStateException("Assigned user not in project");
            }
        }

        WorkItemDocument item = new WorkItemDocument();
        item.setProjectId(projectId);
        item.setSprintId(sprintId);
        item.setTitle(request.title());
        item.setDescription(request.description());
        item.setType(request.type());
        item.setEffort(request.effort());
        item.setDeadline(request.deadline());
        item.setAssignedUserIds(request.assignedUserIds());
        item.setStatus(WorkItemStatus.TODO);
        item.setCarryCount(0);

        return workItemRepository.save(item);
    }

    /* UPDATE STATUS WITH RULES */
    public WorkItemDocument updateStatus(
            String workItemId,
            UpdateWorkItemStatusRequest request
    ) {

        WorkItemDocument item = workItemRepository.findById(workItemId)
                .orElseThrow(() -> new IllegalStateException("WorkItem not found"));

        WorkItemStatus current = item.getStatus();
        WorkItemStatus next = request.status();

        if (!isValidTransition(current, next)) {
            throw new IllegalStateException("Invalid status transition");
        }

        item.setStatus(next);
        return workItemRepository.save(item);
    }

    private boolean isValidTransition(WorkItemStatus from, WorkItemStatus to) {
        return (from == WorkItemStatus.TODO && to == WorkItemStatus.DOING)
                || (from == WorkItemStatus.DOING && to == WorkItemStatus.DONE);
    }
}
