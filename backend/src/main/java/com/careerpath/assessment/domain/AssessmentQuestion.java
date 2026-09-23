package com.careerpath.assessment.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "assessment_questions")
public class AssessmentQuestion {
    @Id
    private UUID id;

    @Column(nullable = false, unique = true, length = 60)
    private String questionKey;

    @Column(nullable = false, length = 300)
    private String prompt;

    protected AssessmentQuestion() {
    }

    public AssessmentQuestion(String questionKey, String prompt) {
        this.id = UUID.randomUUID();
        this.questionKey = questionKey;
        this.prompt = prompt;
    }

    public UUID getId() { return id; }
    public String getQuestionKey() { return questionKey; }
    public String getPrompt() { return prompt; }
}

