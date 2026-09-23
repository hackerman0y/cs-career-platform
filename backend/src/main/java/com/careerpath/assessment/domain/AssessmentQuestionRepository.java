package com.careerpath.assessment.domain;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssessmentQuestionRepository extends JpaRepository<AssessmentQuestion, UUID> {
    List<AssessmentQuestion> findAllByOrderByQuestionKeyAsc();
    boolean existsByQuestionKey(String questionKey);
}

