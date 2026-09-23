package com.careerpath.readiness.domain;

import com.careerpath.auth.domain.AppUser;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "readiness_assessments")
public class ReadinessAssessment {
    @Id private UUID id;
    @OneToOne(optional = false) @JoinColumn(name = "user_id", unique = true) private AppUser user;
    private int score;
    private Instant evaluatedAt;

    protected ReadinessAssessment() { }
    public ReadinessAssessment(AppUser user) { this.id = UUID.randomUUID(); this.user = user; }
    public void updateScore(int score) { this.score = score; this.evaluatedAt = Instant.now(); }
    public int getScore() { return score; }
    public Instant getEvaluatedAt() { return evaluatedAt; }
}
