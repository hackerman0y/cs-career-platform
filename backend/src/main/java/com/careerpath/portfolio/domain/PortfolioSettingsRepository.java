package com.careerpath.portfolio.domain;
import com.careerpath.auth.domain.AppUser; import java.util.*; import org.springframework.data.jpa.repository.JpaRepository;
public interface PortfolioSettingsRepository extends JpaRepository<PortfolioSettings,UUID>{Optional<PortfolioSettings> findByUser(AppUser user);Optional<PortfolioSettings> findByUsernameIgnoreCase(String username);boolean existsByUsernameIgnoreCaseAndUserNot(String username,AppUser user);}
