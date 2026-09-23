package com.careerpath.checklist.domain;
import com.careerpath.auth.domain.AppUser; import java.util.*; import org.springframework.data.jpa.repository.JpaRepository;
public interface ChecklistProgressRepository extends JpaRepository<ChecklistProgress,UUID>{List<ChecklistProgress> findByUser(AppUser user);Optional<ChecklistProgress> findByUserAndItem(AppUser user,ChecklistItem item);}
