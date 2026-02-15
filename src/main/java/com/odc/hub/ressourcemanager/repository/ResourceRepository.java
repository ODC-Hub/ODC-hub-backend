package com.odc.hub.ressourcemanager.repository;

import com.odc.hub.ressourcemanager.model.Resource;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ResourceRepository extends MongoRepository<Resource, String> {

    List<Resource> findByModuleId(String moduleId);

    List<Resource> findByModuleIdAndValidatedTrue(String moduleId);

    List<Resource> findByValidatedTrue();

    // Find resources where assignedTo array contains this userId (same pattern as
    // PlanningItemRepository)
    List<Resource> findByAssignedToContaining(String userId);

    List<Resource> findByModuleIdAndAssignedToContaining(String moduleId, String userId);
}
