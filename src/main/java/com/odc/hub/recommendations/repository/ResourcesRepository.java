package com.odc.hub.recommendations.repository;

import com.odc.hub.recommendations.model.Resource;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ResourcesRepository extends MongoRepository<Resource, ObjectId> {

    List<Resource> findByIdIn(List<String> ids);
}
