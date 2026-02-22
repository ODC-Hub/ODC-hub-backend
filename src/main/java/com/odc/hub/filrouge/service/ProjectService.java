package com.odc.hub.filrouge.service;

import com.odc.hub.filrouge.dto.CreateProjectRequest;
import com.odc.hub.filrouge.model.ProjectDocument;
import com.odc.hub.filrouge.repository.ProjectRepository;
import com.odc.hub.user.model.Role;
import com.odc.hub.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectNotificationService projectNotificationService;
    private final UserRepository userRepository;

    public ProjectDocument createProject(CreateProjectRequest request, String creatorUserId) {

        ProjectDocument project = new ProjectDocument();
        project.setName(request.name());
        project.setDescription(request.description());
        project.setCreatedBy(creatorUserId);

        ArrayList<String> members = new ArrayList<>(request.memberIds());
        if (!members.contains(creatorUserId)) {
            members.add(creatorUserId);
        }

        project.setMembers(members);
        project.setCreatedAt(Instant.now());

        ProjectDocument saved = projectRepository.save(project);

        userRepository.findById(creatorUserId).ifPresent(creator ->
                projectNotificationService.onProjectCreated(saved, creator)
        );

        return saved;    }

    public ProjectDocument getProjectOrThrow(String projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found"));
    }

    public List<ProjectDocument> getAllProjectsForUser(com.odc.hub.user.model.User user) {
        if (user.getRole() == Role.ADMIN || user.getRole() == Role.FORMATEUR) {
            return projectRepository.findAll();
        }
        return projectRepository.findByMembersContaining(user.getId());
    }

    public ProjectDocument getProjectIfAllowed(String projectId, com.odc.hub.user.model.User user) {
        ProjectDocument project = getProjectOrThrow(projectId);

        if (user.getRole() == Role.ADMIN || user.getRole() == Role.FORMATEUR) {
            return project;
        }

        if (project.getMembers() != null && project.getMembers().contains(user.getId())) {
            return project;
        }

        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to view this project");
    }

    public List<ProjectDocument> getAllProjects() {
        return projectRepository.findAll();
    }

    public ProjectDocument addMember(String projectId, String userId) {
        ProjectDocument project = getProjectOrThrow(projectId);
        ArrayList<String> members = new ArrayList<>(project.getMembers());
        if (!members.contains(userId)) {
            members.add(userId);
            project.setMembers(members);
            return projectRepository.save(project);
        }
        return project;
    }

    public ProjectDocument removeMember(String projectId, String userId) {
        ProjectDocument project = getProjectOrThrow(projectId);
        ArrayList<String> members = new ArrayList<>(project.getMembers());
        if (members.contains(userId)) {
            members.remove(userId);
            project.setMembers(members);
            return projectRepository.save(project);
        }
        return project;
    }
}
