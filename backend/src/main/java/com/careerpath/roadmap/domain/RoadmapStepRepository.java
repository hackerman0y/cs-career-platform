package com.careerpath.roadmap.domain;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
public interface RoadmapStepRepository extends JpaRepository<RoadmapStep, UUID> {
    List<RoadmapStep> findByCareerNameOrderByOrderIndex(String careerName);
    boolean existsByCareerNameAndOrderIndex(String careerName, int orderIndex);
}
