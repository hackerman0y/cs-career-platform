package com.careerpath.portfolio.domain;
import com.careerpath.auth.domain.AppUser; import jakarta.persistence.*; import java.util.UUID;
@Entity @Table(name="portfolio_settings")
public class PortfolioSettings {
 @Id private UUID id; @OneToOne(optional=false) @JoinColumn(name="user_id",unique=true) private AppUser user;
 @Column(nullable=false,unique=true,length=50) private String username; @Column(nullable=false,length=120) private String headline;
 @Column(nullable=false,length=1200) private String bio; @Column(nullable=false) private boolean published;
 protected PortfolioSettings(){} public PortfolioSettings(AppUser u,String username){id=UUID.randomUUID();user=u;this.username=username;headline="Computer science student building useful products";bio="Welcome to my portfolio. Here are the projects and skills I am developing.";}
 public void update(String u,String h,String b,boolean p){username=u;headline=h;bio=b;published=p;}
 public AppUser getUser(){return user;} public String getUsername(){return username;} public String getHeadline(){return headline;} public String getBio(){return bio;} public boolean isPublished(){return published;}
}
