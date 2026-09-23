package com.careerpath.learning.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LearningResourceRepository extends JpaRepository<LearningResource, UUID> {
    List<LearningResource> findByRoadmapStepIdOrderByDisplayOrder(UUID roadmapStepId);
    boolean existsByRoadmapStepIdAndDisplayOrder(UUID roadmapStepId, int displayOrder);
    Optional<LearningResource> findByRoadmapStepIdAndDisplayOrder(UUID roadmapStepId, int displayOrder);
}
