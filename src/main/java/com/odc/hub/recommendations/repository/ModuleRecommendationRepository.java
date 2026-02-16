package com.odc.hub.recommendations.repository;

import com.odc.hub.recommendations.model.ModuleRecommendation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ModuleRecommendationRepository
        extends MongoRepository<ModuleRecommendation, String> {

    Optional<ModuleRecommendation> findByModuleId(String moduleId);
}
