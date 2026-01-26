package com.odc.hub.filrouge.controller;

import com.odc.hub.filrouge.dto.CreateWorkItemRequest;
import com.odc.hub.filrouge.dto.UpdateWorkItemStatusRequest;
import com.odc.hub.filrouge.dto.WorkItemResponse;
import com.odc.hub.filrouge.mapper.WorkItemMapper;
import com.odc.hub.filrouge.model.WorkItemDocument;
import com.odc.hub.filrouge.repository.WorkItemRepository;
import com.odc.hub.filrouge.service.WorkItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/filrouge/work-items")
@RequiredArgsConstructor
public class WorkItemController {

    private final WorkItemService workItemService;
    private final WorkItemRepository workItemRepository;

    /* CREATE TASK IN SPRINT */
    @PostMapping("/project/{projectId}/sprint/{sprintId}")
    @PreAuthorize("hasAnyRole('ADMIN','FORMATEUR','BOOTCAMPER')")
    public WorkItemResponse createWorkItem(
            @PathVariable String projectId,
            @PathVariable String sprintId,
            @RequestBody CreateWorkItemRequest request
    ) {
        WorkItemDocument item =
                workItemService.createWorkItem(projectId, sprintId, request);

        return WorkItemMapper.toResponse(item);
    }

    /* UPDATE STATUS (KANBAN DRAG) */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN','FORMATEUR','BOOTCAMPER')")
    public WorkItemResponse updateStatus(
            @PathVariable String id,
            @RequestBody UpdateWorkItemStatusRequest request
    ) {
        return WorkItemMapper.toResponse(
                workItemService.updateStatus(id, request)
        );
    }

    /* KANBAN LOAD */
    @GetMapping("/sprint/{sprintId}")
    @PreAuthorize("hasAnyRole('ADMIN','FORMATEUR','BOOTCAMPER')")
    public List<WorkItemResponse> getKanban(@PathVariable String sprintId) {
        return workItemRepository.findBySprintId(sprintId)
                .stream()
                .map(WorkItemMapper::toResponse)
                .toList();
    }
}
