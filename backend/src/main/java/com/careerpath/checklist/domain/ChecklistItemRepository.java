package com.careerpath.checklist.domain;
import java.util.*; import org.springframework.data.jpa.repository.JpaRepository;
public interface ChecklistItemRepository extends JpaRepository<ChecklistItem,UUID>{List<ChecklistItem> findByCareerNameOrderByOrderIndex(String careerName);boolean existsByCareerNameAndOrderIndex(String careerName,int orderIndex);}
