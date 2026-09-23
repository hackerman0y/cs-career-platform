package com.careerpath.learning.domain;

import com.careerpath.roadmap.domain.RoadmapStep;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "learning_resources")
public class LearningResource {
    @Id private UUID id;
    @ManyToOne(optional = false) @JoinColumn(name = "roadmap_step_id") private RoadmapStep roadmapStep;
    @Column(nullable = false, length = 160) private String title;
    @Column(nullable = false, length = 80) private String provider;
    @Column(nullable = false, length = 30) private String resourceType;
    @Column(nullable = false, length = 700) private String url;
    @Column(nullable = false, length = 20) private String language;
    @Column(nullable = false, length = 20) private String costType;
    @Column(nullable = false, length = 30) private String difficulty;
    @Column(nullable = false, length = 60) private String estimatedDuration;
    @Column(nullable = false, length = 500) private String description;
    @Column(name = "display_order", nullable = false) private int displayOrder;
    @Column(nullable = false) private boolean verified;
    @Column(nullable = false) private Instant lastCheckedAt;

    protected LearningResource() { }
    public LearningResource(RoadmapStep roadmapStep, String title, String provider, String resourceType, String url,
            String language, String costType, String difficulty, String estimatedDuration, String description, int displayOrder) {
        this.id = UUID.randomUUID(); this.roadmapStep = roadmapStep; this.title = title; this.provider = provider;
        this.resourceType = resourceType; this.url = url; this.language = language; this.costType = costType;
        this.difficulty = difficulty; this.estimatedDuration = estimatedDuration; this.description = description;
        this.displayOrder = displayOrder; this.verified = true; this.lastCheckedAt = Instant.now();
    }
    public UUID getId() { return id; } public RoadmapStep getRoadmapStep() { return roadmapStep; }
    public String getTitle() { return title; } public String getProvider() { return provider; } public String getResourceType() { return resourceType; }
    public String getUrl() { return url; } public String getLanguage() { return language; } public String getCostType() { return costType; }
    public String getDifficulty() { return difficulty; } public String getEstimatedDuration() { return estimatedDuration; }
    public String getDescription() { return description; } public int getDisplayOrder() { return displayOrder; }
    public boolean isVerified() { return verified; } public Instant getLastCheckedAt() { return lastCheckedAt; }
    public void refresh(String title, String provider, String resourceType, String url, String language, String costType, String difficulty, String estimatedDuration, String description) {
        this.title = title; this.provider = provider; this.resourceType = resourceType; this.url = url; this.language = language; this.costType = costType; this.difficulty = difficulty; this.estimatedDuration = estimatedDuration; this.description = description; this.verified = true; this.lastCheckedAt = Instant.now();
    }
}
