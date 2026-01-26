package com.odc.hub.filrouge.repository;

import com.odc.hub.filrouge.model.WorkItemDocument;
import com.odc.hub.filrouge.enums.WorkItemStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.Instant;
import java.util.List;

public interface WorkItemRepository extends MongoRepository<WorkItemDocument, String> {

    List<WorkItemDocument> findBySprintId(String sprintId);

    List<WorkItemDocument> findByProjectId(String projectId);

    List<WorkItemDocument> findByAssignedUserIdsContaining(String userId);

    List<WorkItemDocument> findBySprintIdAndStatus(String sprintId, WorkItemStatus status);

    List<WorkItemDocument> findByDeadlineBeforeAndStatusNot(Instant now, WorkItemStatus status);
}
