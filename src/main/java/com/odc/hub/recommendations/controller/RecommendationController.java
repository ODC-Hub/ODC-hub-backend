package com.odc.hub.recommendations.controller;

import com.odc.hub.recommendations.model.Resource;
import com.odc.hub.recommendations.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/modules")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    @GetMapping("/{moduleId}/recommendations")
    public ResponseEntity<List<Resource>> getRecommendations(
            @PathVariable String moduleId) {

        return ResponseEntity.ok(
                recommendationService.getRecommendationsByModuleId(moduleId)
        );
    }
}
