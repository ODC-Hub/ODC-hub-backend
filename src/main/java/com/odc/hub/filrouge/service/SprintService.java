package com.odc.hub.filrouge.service;

import com.odc.hub.filrouge.dto.CreateSprintRequest;
import com.odc.hub.filrouge.enums.SprintStatus;
import com.odc.hub.filrouge.enums.WorkItemStatus;
import com.odc.hub.filrouge.model.SprintDocument;
import com.odc.hub.filrouge.model.WorkItemDocument;
import com.odc.hub.filrouge.repository.SprintRepository;
import com.odc.hub.filrouge.repository.WorkItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SprintService {

    private final SprintRepository sprintRepository;
    private final WorkItemRepository workItemRepository;

    /* CREATE SPRINT */
    public SprintDocument createSprint(String projectId, CreateSprintRequest request) {

        SprintDocument sprint = new SprintDocument();
        sprint.setProjectId(projectId);
        sprint.setName(request.name());
        sprint.setStartDate(request.startDate());
        sprint.setEndDate(request.endDate());
        sprint.setStatus(SprintStatus.PLANNED);
        sprint.setPlannedEffort(0);

        return sprintRepository.save(sprint);
    }

    /* UPDATE SPRINT */
    public SprintDocument updateSprint(String sprintId, CreateSprintRequest request) {

        SprintDocument sprint = getSprintOrThrow(sprintId);

        if (sprint.getStatus() != SprintStatus.PLANNED) {
            throw new IllegalStateException("Only PLANNED sprint can be updated");
        }

        sprint.setName(request.name());
        sprint.setStartDate(request.startDate());
        sprint.setEndDate(request.endDate());

        return sprintRepository.save(sprint);
    }

    /* START SPRINT */
    public SprintDocument startSprint(String sprintId) {

        SprintDocument sprint = getSprintOrThrow(sprintId);

        if (sprint.getStatus() != SprintStatus.PLANNED) {
            throw new IllegalStateException("Only PLANNED sprint can be started");
        }

        sprintRepository.findByProjectIdAndStatus(
                sprint.getProjectId(), SprintStatus.ACTIVE).ifPresent(s -> {
                    throw new IllegalStateException("Another sprint is already ACTIVE");
                });

        List<WorkItemDocument> items = workItemRepository.findBySprintId(sprintId);

        int plannedEffort = items.stream()
                .mapToInt(WorkItemDocument::getEffort)
                .sum();

        sprint.setPlannedEffort(plannedEffort);
        sprint.setStatus(SprintStatus.ACTIVE);

        return sprintRepository.save(sprint);
    }

    /* CLOSE SPRINT */
    public void closeSprint(String sprintId, String nextSprintId) {

        SprintDocument sprint = getSprintOrThrow(sprintId);

        if (sprint.getStatus() != SprintStatus.ACTIVE) {
            throw new IllegalStateException("Only ACTIVE sprint can be closed");
        }

        List<WorkItemDocument> items = workItemRepository.findBySprintId(sprintId);

        for (WorkItemDocument oldItem : items) {
            if (oldItem.getStatus() != WorkItemStatus.DONE) {

                oldItem.setCarryCount(oldItem.getCarryCount() + 1);
                workItemRepository.save(oldItem);

                WorkItemDocument newItem = new WorkItemDocument();
                newItem.setProjectId(oldItem.getProjectId());
                newItem.setSprintId(nextSprintId);
                newItem.setTitle(oldItem.getTitle());
                newItem.setDescription(oldItem.getDescription());
                newItem.setType(oldItem.getType());
                newItem.setEffort(oldItem.getEffort());
                newItem.setDeadline(oldItem.getDeadline());
                newItem.setAssignedUserIds(oldItem.getAssignedUserIds());
                newItem.setStatus(WorkItemStatus.TODO);
                newItem.setCarryCount(0);

                workItemRepository.save(newItem);
            }
        }

        sprint.setStatus(SprintStatus.CLOSED);
        sprintRepository.save(sprint);
    }

    private SprintDocument getSprintOrThrow(String sprintId) {
        return sprintRepository.findById(sprintId)
                .orElseThrow(() -> new IllegalStateException("Sprint not found"));
    }

    public List<SprintDocument> getSprintsByProject(String projectId) {
        return sprintRepository.findByProjectId(projectId);
    }

    public SprintDocument updateSprint(String sprintId, CreateSprintRequest request) {
        SprintDocument sprint = getSprintOrThrow(sprintId);
        sprint.setName(request.name());
        sprint.setStartDate(request.startDate());
        sprint.setEndDate(request.endDate());
        return sprintRepository.save(sprint);
    }
}
