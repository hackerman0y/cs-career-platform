package com.careerpath.readiness.domain;

import com.careerpath.auth.domain.AppUser;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProgressEvidenceRepository extends JpaRepository<ProgressEvidence, UUID> {
    List<ProgressEvidence> findByUserOrderByVerifiedAtDesc(AppUser user);
    void deleteByUser(AppUser user);
}
