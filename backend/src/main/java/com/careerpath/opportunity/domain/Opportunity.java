package com.careerpath.opportunity.domain;

import com.careerpath.auth.domain.AppUser;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "opportunities")
public class Opportunity {
    @Id private UUID id;
    @ManyToOne(optional = false) @JoinColumn(name = "user_id") private AppUser user;
    @Column(nullable = false, length = 160) private String title;
    @Column(nullable = false, length = 160) private String company;
    @Column(nullable = false, length = 30) private String opportunityType;
    @Column(length = 700) private String url;
    @Column(nullable = false, length = 1600) private String description;
    @Column(nullable = false, length = 700) private String requirements;
    @Column(nullable = false, updatable = false) private Instant createdAt;

    protected Opportunity() { }
    public Opportunity(AppUser user, String title, String company, String opportunityType, String url, String description, String requirements) {
        this.id = UUID.randomUUID(); this.user = user; update(title, company, opportunityType, url, description, requirements); this.createdAt = Instant.now();
    }
    public void update(String title, String company, String opportunityType, String url, String description, String requirements) { this.title = title; this.company = company; this.opportunityType = opportunityType; this.url = url; this.description = description; this.requirements = requirements; }
    public UUID getId() { return id; } public AppUser getUser() { return user; } public String getTitle() { return title; } public String getCompany() { return company; }
    public String getOpportunityType() { return opportunityType; } public String getUrl() { return url; } public String getDescription() { return description; } public String getRequirements() { return requirements; } public Instant getCreatedAt() { return createdAt; }
}
