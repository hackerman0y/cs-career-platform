package com.careerpath.roadmap.domain;
import com.careerpath.auth.domain.AppUser;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
@Entity @Table(name="roadmap_progress", uniqueConstraints=@UniqueConstraint(columnNames={"user_id","step_id"}))
public class RoadmapProgress {
    @Id private UUID id;
    @ManyToOne(optional=false) @JoinColumn(name="user_id") private AppUser user;
    @ManyToOne(optional=false) @JoinColumn(name="step_id") private RoadmapStep step;
    @Column(nullable=false) private boolean completed;
    private Instant completedAt;
    protected RoadmapProgress() {}
    public RoadmapProgress(AppUser user, RoadmapStep step){this.id=UUID.randomUUID();this.user=user;this.step=step;}
    public void setCompleted(boolean completed){this.completed=completed;this.completedAt=completed?Instant.now():null;}
    public RoadmapStep getStep(){return step;} public boolean isCompleted(){return completed;} public Instant getCompletedAt(){return completedAt;}
}
