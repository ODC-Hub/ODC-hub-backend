package com.odc.hub.recommendations.repository;

import com.odc.hub.recommendations.model.Module;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ModuleRepository extends MongoRepository<Module, String> {
}