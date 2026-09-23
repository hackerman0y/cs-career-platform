package com.careerpath.opportunity.domain;

import com.careerpath.auth.domain.AppUser;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobApplicationRepository extends JpaRepository<JobApplication, UUID> {
    List<JobApplication> findByUserOrderByFollowUpDateAsc(AppUser user);
    Optional<JobApplication> findByIdAndUser(UUID id, AppUser user);
}
