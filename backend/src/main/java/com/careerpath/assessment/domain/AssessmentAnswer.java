package com.careerpath.assessment.domain;

import com.careerpath.auth.domain.AppUser;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.UUID;

@Entity
@Table(name = "assessment_answers", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "question_id"}))
public class AssessmentAnswer {
    @Id
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "question_id", nullable = false)
    private AssessmentQuestion question;

    @Column(nullable = false, length = 40)
    private String answerValue;

    protected AssessmentAnswer() {
    }

    public AssessmentAnswer(AppUser user, AssessmentQuestion question, String answerValue) {
        this.id = UUID.randomUUID();
        this.user = user;
        this.question = question;
        this.answerValue = answerValue;
    }

    public AssessmentQuestion getQuestion() { return question; }
    public String getAnswerValue() { return answerValue; }

    public void updateAnswer(String answerValue) {
        this.answerValue = answerValue;
    }
}
