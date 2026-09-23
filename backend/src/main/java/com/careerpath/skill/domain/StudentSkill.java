package com.careerpath.skill.domain;

import com.careerpath.auth.domain.AppUser;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity @Table(name = "student_skills", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "name"}))
public class StudentSkill {
    @Id private UUID id;
    @ManyToOne(optional = false) @JoinColumn(name = "user_id", nullable = false) private AppUser user;
    @Column(nullable = false, length = 80) private String name;
    @Column(nullable = false, length = 20) private String level;
    @Column(nullable = false, length = 30) private String source;
    @Column(nullable = false) private Instant createdAt;
    protected StudentSkill() { }
    public StudentSkill(AppUser user, String name, String level) { this(user, name, level, "manual"); }
    public StudentSkill(AppUser user, String name, String level, String source) { this.id = UUID.randomUUID(); this.user = user; this.name = name; this.level = level; this.source = source; this.createdAt = Instant.now(); }
    public UUID getId() { return id; } public AppUser getUser() { return user; } public String getName() { return name; } public String getLevel() { return level; } public String getSource() { return source; }
    public void update(String level) { this.level = level; }
}
