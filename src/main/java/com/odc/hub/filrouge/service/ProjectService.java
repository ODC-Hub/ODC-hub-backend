package com.odc.hub.filrouge.service;

import com.odc.hub.filrouge.dto.CreateProjectRequest;
import com.odc.hub.filrouge.model.ProjectDocument;
import com.odc.hub.filrouge.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectDocument createProject(CreateProjectRequest request, String creatorUserId) {

        ProjectDocument project = new ProjectDocument();
        project.setName(request.name());
        project.setDescription(request.description());
        project.setCreatedBy(creatorUserId);

        List<String> members = new ArrayList<>(request.memberIds());
        if (!members.contains(creatorUserId)) {
            members.add(creatorUserId);
        }

        project.setMembers(members);
        project.setCreatedAt(Instant.now());

        return projectRepository.save(project);
    }

    public ProjectDocument getProjectOrThrow(String projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalStateException("Project not found"));
    }
}
