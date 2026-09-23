package com.careerpath.roadmap.domain;
import com.careerpath.auth.domain.AppUser;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
public interface RoadmapProgressRepository extends JpaRepository<RoadmapProgress, UUID>{
    List<RoadmapProgress> findByUser(AppUser user);
    Optional<RoadmapProgress> findByUserAndStep(AppUser user, RoadmapStep step);
}
