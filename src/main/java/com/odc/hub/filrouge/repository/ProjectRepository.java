package com.odc.hub.filrouge.repository;

import com.odc.hub.filrouge.model.ProjectDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ProjectRepository extends MongoRepository<ProjectDocument, String> {

    List<ProjectDocument> findByMembersContaining(String userId);

    List<ProjectDocument> findByCreatedBy(String userId);
}
