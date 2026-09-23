package com.careerpath.readiness.domain;

import com.careerpath.auth.domain.AppUser;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReadinessAssessmentRepository extends JpaRepository<ReadinessAssessment, UUID> {
    Optional<ReadinessAssessment> findByUser(AppUser user);
}
