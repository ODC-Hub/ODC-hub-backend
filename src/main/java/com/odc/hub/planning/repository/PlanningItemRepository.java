package com.odc.hub.planning.repository;

import com.odc.hub.planning.model.PlanningItem;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface PlanningItemRepository extends MongoRepository<PlanningItem, String> {

    // MongoDB: find where userIds array contains this userId
    List<PlanningItem> findByUserIdsContaining(String userId);

}
