package com.careerpath.project.domain;
import com.careerpath.auth.domain.AppUser;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
@Entity @Table(name="student_projects")
public class StudentProject {
 @Id private UUID id; @ManyToOne(optional=false) @JoinColumn(name="user_id") private AppUser user;
 @Column(nullable=false,length=120) private String name; @Column(nullable=false,length=1200) private String description;
 @Column(nullable=false,length=300) private String technologies; @Column(length=400) private String githubUrl; @Column(length=400) private String liveDemoUrl;
 @Column(nullable=false,updatable=false) private Instant createdAt; @Column(nullable=false) private Instant updatedAt;
 protected StudentProject(){} public StudentProject(AppUser u,String n,String d,String t,String g,String l){id=UUID.randomUUID();user=u;update(n,d,t,g,l);createdAt=Instant.now();}
 @PrePersist @PreUpdate void timestamps(){if(createdAt==null)createdAt=Instant.now();updatedAt=Instant.now();}
 public void update(String n,String d,String t,String g,String l){name=n;description=d;technologies=t;githubUrl=g;liveDemoUrl=l;}
 public UUID getId(){return id;} public AppUser getUser(){return user;} public String getName(){return name;} public String getDescription(){return description;}
 public String getTechnologies(){return technologies;} public String getGithubUrl(){return githubUrl;} public String getLiveDemoUrl(){return liveDemoUrl;} public Instant getCreatedAt(){return createdAt;}
}
