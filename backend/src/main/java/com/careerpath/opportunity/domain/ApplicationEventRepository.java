package com.careerpath.opportunity.domain;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationEventRepository extends JpaRepository<ApplicationEvent, UUID> {
    List<ApplicationEvent> findByApplicationIdOrderByOccurredAtAsc(UUID applicationId);
}
