package com.careerpath.opportunity.domain;

import com.careerpath.auth.domain.AppUser;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "job_applications")
public class JobApplication {
    @Id private UUID id;
    @ManyToOne(optional = false) @JoinColumn(name = "user_id") private AppUser user;
    @ManyToOne(optional = false) @JoinColumn(name = "opportunity_id") private Opportunity opportunity;
    @Column(nullable = false, length = 30) private String status;
    private LocalDate appliedAt;
    private LocalDate followUpDate;
    @Column(nullable = false, length = 1600) private String notes;
    @Column(nullable = false, length = 700) private String outcome;

    protected JobApplication() { }
    public JobApplication(AppUser user, Opportunity opportunity, String status, LocalDate appliedAt, LocalDate followUpDate, String notes, String outcome) { this.id = UUID.randomUUID(); this.user = user; this.opportunity = opportunity; update(status, appliedAt, followUpDate, notes, outcome); }
    public void update(String status, LocalDate appliedAt, LocalDate followUpDate, String notes, String outcome) { this.status = status; this.appliedAt = appliedAt; this.followUpDate = followUpDate; this.notes = notes; this.outcome = outcome; }
    public UUID getId() { return id; } public AppUser getUser() { return user; } public Opportunity getOpportunity() { return opportunity; } public String getStatus() { return status; } public LocalDate getAppliedAt() { return appliedAt; } public LocalDate getFollowUpDate() { return followUpDate; } public String getNotes() { return notes; } public String getOutcome() { return outcome; }
}
