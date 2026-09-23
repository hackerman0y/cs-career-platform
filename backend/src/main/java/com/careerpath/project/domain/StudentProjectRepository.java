package com.careerpath.project.domain;
import com.careerpath.auth.domain.AppUser;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
public interface StudentProjectRepository extends JpaRepository<StudentProject,UUID>{List<StudentProject> findByUserOrderByCreatedAtDesc(AppUser user); Optional<StudentProject> findByIdAndUser(UUID id,AppUser user); boolean existsByUserAndGithubUrl(AppUser user, String githubUrl);}
