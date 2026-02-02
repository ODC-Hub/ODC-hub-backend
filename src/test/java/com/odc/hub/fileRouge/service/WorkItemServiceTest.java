package com.odc.hub.fileRouge.service;

import com.odc.hub.filrouge.dto.CreateWorkItemRequest;
import com.odc.hub.filrouge.enums.WorkItemType;
import com.odc.hub.filrouge.model.ProjectDocument;
import com.odc.hub.filrouge.model.WorkItemDocument;
import com.odc.hub.filrouge.repository.ProjectRepository;
import com.odc.hub.filrouge.repository.WorkItemRepository;
import com.odc.hub.filrouge.service.WorkItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkItemServiceTest {

    @Mock
    private WorkItemRepository workItemRepository;

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private WorkItemService workItemService;

    private ProjectDocument project;

    @BeforeEach
    void setUp() {
        project = new ProjectDocument();
        project.setId("proj1");
        project.setMembers(List.of("user1", "user2"));
    }

    @Test
    void createWorkItem_Success() {
        CreateWorkItemRequest request = new CreateWorkItemRequest(
                "Test Task",
                "Desc",
                WorkItemType.TASK,
                5,
                Instant.now().plusSeconds(3600),
                List.of("user1"));

        when(projectRepository.findById("proj1")).thenReturn(Optional.of(project));
        when(workItemRepository.save(any(WorkItemDocument.class))).thenAnswer(i -> i.getArguments()[0]);

        WorkItemDocument result = workItemService.createWorkItem("proj1", "sprint1", request);

        assertNotNull(result);
        assertEquals("Test Task", result.getTitle());
        assertEquals(5, result.getEffort());
        verify(workItemRepository).save(any());
    }

    @Test
    void createWorkItem_InvalidDeadline_NearNow_Success() {
        // Test that the 5-minute buffer works (e.g., 1 minute in the past)
        CreateWorkItemRequest request = new CreateWorkItemRequest(
                "Test Task",
                "Desc",
                WorkItemType.TASK,
                5,
                Instant.now().minusSeconds(60),
                List.of("user1"));

        when(projectRepository.findById("proj1")).thenReturn(Optional.of(project));
        when(workItemRepository.save(any(WorkItemDocument.class))).thenAnswer(i -> i.getArguments()[0]);

        WorkItemDocument result = workItemService.createWorkItem("proj1", "sprint1", request);

        assertNotNull(result);
        verify(workItemRepository).save(any());
    }

    @Test
    void createWorkItem_InvalidDeadline_FarPast_ThrowsException() {
        CreateWorkItemRequest request = new CreateWorkItemRequest(
                "Test Task",
                "Desc",
                WorkItemType.TASK,
                5,
                Instant.now().minusSeconds(600), // 10 minutes ago (beyond 5 min buffer)
                List.of("user1"));

        assertThrows(IllegalStateException.class, () -> workItemService.createWorkItem("proj1", "sprint1", request));
    }

    @Test
    void createWorkItem_UserNotInProject_ThrowsException() {
        CreateWorkItemRequest request = new CreateWorkItemRequest(
                "Test Task",
                "Desc",
                WorkItemType.TASK,
                5,
                Instant.now().plusSeconds(3600),
                List.of("user_stranger"));

        when(projectRepository.findById("proj1")).thenReturn(Optional.of(project));

        assertThrows(IllegalStateException.class, () -> workItemService.createWorkItem("proj1", "sprint1", request));
    }
}
