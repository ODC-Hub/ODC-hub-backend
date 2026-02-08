package com.odc.hub.ressourcemanager.repository;

import com.odc.hub.ressourcemanager.model.Resource;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ResourceRepository extends MongoRepository<Resource, String> {

    List<Resource> findByModuleIdAndValidatedTrue(String moduleId);

    List<Resource> findByModuleId(String moduleId);
}
