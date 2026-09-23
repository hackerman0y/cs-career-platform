package com.careerpath.skill.domain;
import com.careerpath.auth.domain.AppUser;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
public interface StudentSkillRepository extends JpaRepository<StudentSkill, UUID> { List<StudentSkill> findByUserOrderByNameAsc(AppUser user); Optional<StudentSkill> findByIdAndUser(UUID id, AppUser user); Optional<StudentSkill> findByUserAndNameIgnoreCase(AppUser user, String name); }
