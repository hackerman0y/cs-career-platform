package com.careerpath.readiness.domain;

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
@Table(name = "progress_evidence")
public class ProgressEvidence {
    @Id private UUID id;
    @ManyToOne(optional = false) @JoinColumn(name = "user_id") private AppUser user;
    @Column(nullable = false, length = 60) private String evidenceKey;
    @Column(nullable = false, length = 160) private String title;
    @Column(nullable = false, length = 600) private String detail;
    @Column(nullable = false, length = 30) private String completionSource;
    @Column(nullable = false) private Instant verifiedAt;

    protected ProgressEvidence() { }

    public ProgressEvidence(AppUser user, String evidenceKey, String title, String detail) {
        this.id = UUID.randomUUID();
        this.user = user;
        this.evidenceKey = evidenceKey;
        this.title = title;
        this.detail = detail;
        this.completionSource = "automatic";
        this.verifiedAt = Instant.now();
    }

    public AppUser getUser() { return user; }
    public String getEvidenceKey() { return evidenceKey; }
    public String getTitle() { return title; }
    public String getDetail() { return detail; }
    public String getCompletionSource() { return completionSource; }
    public Instant getVerifiedAt() { return verifiedAt; }
}
