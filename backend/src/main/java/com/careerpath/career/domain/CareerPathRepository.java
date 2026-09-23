package com.careerpath.career.domain;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CareerPathRepository extends JpaRepository<CareerPath, UUID> {
}

