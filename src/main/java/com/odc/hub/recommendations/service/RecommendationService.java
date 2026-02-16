package com.odc.hub.recommendations.service;

import com.odc.hub.recommendations.model.Module;
import com.odc.hub.recommendations.model.ModuleRecommendation;
import com.odc.hub.recommendations.model.RecommendedResource;
import com.odc.hub.recommendations.model.Resource;
import com.odc.hub.recommendations.repository.ModuleRecommendationRepository;
import com.odc.hub.recommendations.repository.ModuleRepository;
import com.odc.hub.recommendations.repository.ResourcesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final ModuleRecommendationRepository recommendationRepository;
    private final ResourcesRepository resourceRepository;
    private final ModuleRepository moduleRepository;

    public List<Resource> getRecommendationsByModuleId(String moduleId) {

        // 1️⃣ Get module (to know its category)
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() ->
                        new RuntimeException("Module not found: " + moduleId));

        // 2️⃣ Get ML recommendation document
        ModuleRecommendation recommendation = recommendationRepository
                .findByModuleId(moduleId)
                .orElseThrow(() ->
                        new RuntimeException("No recommendations found for module: " + moduleId));

        List<RecommendedResource> rankedResources = recommendation.getRecommendedResources();

        if (rankedResources == null || rankedResources.isEmpty()) {
            return Collections.emptyList();
        }

        // 3️⃣ Extract resource IDs in ranking order
        List<String> resourceIds = rankedResources.stream()
                .map(RecommendedResource::getResourceId)
                .toList();

        // 4️⃣ Fetch resources from DB
        List<Resource> resources = resourceRepository.findByIdIn(resourceIds);

        // 5️⃣ Map by custom ID (not Mongo _id)
        Map<String, Resource> resourceMap = resources.stream()
                .filter(r -> r.getId() != null)
                .collect(Collectors.toMap(
                        Resource::getId,
                        r -> r,
                        (existing, replacement) -> existing
                ));

        // 6️⃣ Preserve ranking + filter by category
        return rankedResources.stream()
                .map(r -> resourceMap.get(r.getResourceId()))
                .filter(Objects::nonNull)
                .filter(resource ->
                        resource.getCategory() != null &&
                                resource.getCategory().equalsIgnoreCase(module.getCategory())
                )
                .toList();
    }
}
