package com.odc.hub.filrouge.repository;

import com.odc.hub.filrouge.model.SprintDocument;
import com.odc.hub.filrouge.enums.SprintStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface SprintRepository extends MongoRepository<SprintDocument, String> {

    List<SprintDocument> findByProjectId(String projectId);

    Optional<SprintDocument> findByProjectIdAndStatus(String projectId, SprintStatus status);
}
