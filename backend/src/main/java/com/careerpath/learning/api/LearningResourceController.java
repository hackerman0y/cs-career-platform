package com.careerpath.learning.api;

import com.careerpath.learning.domain.LearningResource;
import com.careerpath.learning.domain.LearningResourceRepository;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/roadmap")
public class LearningResourceController {
    private final LearningResourceRepository resources;
    public LearningResourceController(LearningResourceRepository resources) { this.resources = resources; }
    @GetMapping("/{stepId}/resources")
    public List<ResourceResponse> forStep(@PathVariable UUID stepId) {
        return resources.findByRoadmapStepIdOrderByDisplayOrder(stepId).stream().map(this::response).toList();
    }
    private ResourceResponse response(LearningResource r) { return new ResourceResponse(r.getId(), r.getTitle(), r.getProvider(), r.getResourceType(), r.getUrl(), r.getLanguage(), r.getCostType(), r.getDifficulty(), r.getEstimatedDuration(), r.getDescription(), r.isVerified(), r.getLastCheckedAt()); }
    public record ResourceResponse(UUID id, String title, String provider, String resourceType, String url, String language, String costType, String difficulty, String estimatedDuration, String description, boolean verified, Instant lastCheckedAt) { }
}
