package com.odc.hub.filrouge.controller;

import com.odc.hub.filrouge.dto.CreateProjectRequest;
import com.odc.hub.filrouge.dto.ProjectResponse;
import com.odc.hub.filrouge.mapper.ProjectMapper;
import com.odc.hub.filrouge.model.ProjectDocument;
import com.odc.hub.filrouge.service.ProjectService;
import com.odc.hub.user.model.User;
import com.odc.hub.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/filrouge/projects")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class ProjectController {

    private final ProjectService projectService;
    private final UserService userService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','FORMATEUR','BOOTCAMPER')")
    public ProjectResponse createProject(
            @RequestBody CreateProjectRequest request) {
        User currentUser = userService.getCurrentAuthenticatedUser();

        ProjectDocument project = projectService.createProject(request, currentUser.getId());

        return ProjectMapper.toResponse(project);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','FORMATEUR','BOOTCAMPER')")
    public java.util.List<ProjectResponse> getAllProjects() {
          User currentUser = userService.getCurrentAuthenticatedUser();
        return projectService.getAllProjectsForUser(currentUser).stream()
                .map(ProjectMapper::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','FORMATEUR','BOOTCAMPER')")
    public ProjectResponse getProject(@PathVariable String id) {
      User currentUser = userService.getCurrentAuthenticatedUser();
        ProjectDocument project = projectService.getProjectIfAllowed(id, currentUser);
        return ProjectMapper.toResponse(project);
    }
    @PostMapping("/{id}/members/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN','FORMATEUR','BOOTCAMPER')")
    public ProjectResponse addMember(@PathVariable String id, @PathVariable String userId) {
        ProjectDocument project = projectService.addMember(id, userId);
        return ProjectMapper.toResponse(project);
    }

    @DeleteMapping("/{id}/members/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN','FORMATEUR','BOOTCAMPER')")
    public ProjectResponse removeMember(@PathVariable String id, @PathVariable String userId) {
        ProjectDocument project = projectService.removeMember(id, userId);
        return ProjectMapper.toResponse(project);
    }
}
