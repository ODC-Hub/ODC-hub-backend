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

        ModuleRecommendation recommendation = recommendationRepository
                .findByModuleId(moduleId)
                .orElseThrow(() ->
                        new RuntimeException("No recommendations found for module: " + moduleId)
                );

        List<RecommendedResource> recommendedResources =
                recommendation.getRecommendedResources();

        List<String> resourceIds = recommendedResources.stream()
                .map(RecommendedResource::getResourceId)
                .toList();

        List<Resource> resources = resourceRepository.findByIdIn(resourceIds);

        Map<String, Resource> resourceMap = resources.stream()
                .collect(Collectors.toMap(
                        Resource::getId,
                        r -> r
                ));

        return recommendedResources.stream()
                .map(r -> resourceMap.get(r.getResourceId()))
                .filter(Objects::nonNull)
                .toList();
    }

    public Module getModuleById(String moduleId) {
        return moduleRepository.findById(moduleId)
                .orElseThrow(() ->
                        new RuntimeException("Module not found: " + moduleId));
    }

    public List<Module> getAllModules() {
        return moduleRepository.findAll();
    }


}
