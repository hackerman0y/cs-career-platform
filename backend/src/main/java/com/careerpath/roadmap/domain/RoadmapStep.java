package com.careerpath.roadmap.domain;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "roadmap_steps", uniqueConstraints = @UniqueConstraint(columnNames = {"career_name", "order_index"}))
public class RoadmapStep {
    @Id private UUID id;
    @Column(name="career_name", nullable=false, length=80) private String careerName;
    @Column(nullable=false, length=120) private String title;
    @Column(nullable=false, length=500) private String description;
    @Column(name="order_index", nullable=false) private int orderIndex;
    protected RoadmapStep() {}
    public RoadmapStep(String careerName, String title, String description, int orderIndex) {
        this.id=UUID.randomUUID(); this.careerName=careerName; this.title=title; this.description=description; this.orderIndex=orderIndex;
    }
    public UUID getId(){return id;} public String getCareerName(){return careerName;} public String getTitle(){return title;}
    public String getDescription(){return description;} public int getOrderIndex(){return orderIndex;}
}
