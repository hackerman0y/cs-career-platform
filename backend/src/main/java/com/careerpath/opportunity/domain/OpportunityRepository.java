package com.careerpath.opportunity.domain;

import com.careerpath.auth.domain.AppUser;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OpportunityRepository extends JpaRepository<Opportunity, UUID> {
    List<Opportunity> findByUserOrderByCreatedAtDesc(AppUser user);
    Optional<Opportunity> findByIdAndUser(UUID id, AppUser user);
}
