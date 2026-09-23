package com.careerpath.opportunity.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "application_events")
public class ApplicationEvent {
    @Id private UUID id;
    @ManyToOne(optional = false) @JoinColumn(name = "application_id") private JobApplication application;
    @Column(nullable = false, length = 30) private String status;
    @Column(nullable = false) private Instant occurredAt;
    protected ApplicationEvent() { }
    public ApplicationEvent(JobApplication application, String status) { this.id = UUID.randomUUID(); this.application = application; this.status = status; this.occurredAt = Instant.now(); }
    public String getStatus() { return status; } public Instant getOccurredAt() { return occurredAt; }
}
