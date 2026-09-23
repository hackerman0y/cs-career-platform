package com.careerpath.assessment.domain;

import com.careerpath.auth.domain.AppUser;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssessmentAnswerRepository extends JpaRepository<AssessmentAnswer, UUID> {
    List<AssessmentAnswer> findByUser(AppUser user);
}
