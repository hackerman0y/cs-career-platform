package com.careerpath.profile.domain;

import com.careerpath.auth.domain.AppUser;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "student_profiles")
public class StudentProfile {
    @Id
    private UUID id;

    @OneToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private AppUser user;

    @Column(nullable = false, length = 80)
    private String fullName;

    @Column(length = 120)
    private String university;

    @Column(nullable = false, length = 30)
    private String academicYear;

    @Column(length = 300)
    private String githubUrl;

    @Column(length = 300)
    private String linkedinUrl;

    @Column(nullable = false, length = 60)
    private String targetCareer;

    @Column(length = 180)
    private String cvFileName;

    @Lob
    private String cvText;

    protected StudentProfile() {
    }

    public static StudentProfile forUser(AppUser user) {
        StudentProfile profile = new StudentProfile();
        profile.id = UUID.randomUUID();
        profile.user = user;
        profile.fullName = "Student";
        profile.university = "";
        profile.academicYear = "Year 1";
        profile.githubUrl = "";
        profile.linkedinUrl = "";
        profile.targetCareer = "Frontend Developer";
        profile.cvFileName = "";
        profile.cvText = "";
        return profile;
    }

    public UUID getId() { return id; }
    public AppUser getUser() { return user; }
    public String getFullName() { return fullName; }
    public String getUniversity() { return university; }
    public String getAcademicYear() { return academicYear; }
    public String getGithubUrl() { return githubUrl; }
    public String getLinkedinUrl() { return linkedinUrl; }
    public String getTargetCareer() { return targetCareer; }
    public String getCvFileName() { return cvFileName; }
    public String getCvText() { return cvText; }

    public void update(String fullName, String university, String academicYear, String githubUrl, String linkedinUrl,
            String targetCareer) {
        this.fullName = fullName;
        this.university = university;
        this.academicYear = academicYear;
        this.githubUrl = githubUrl;
        this.linkedinUrl = linkedinUrl;
        this.targetCareer = targetCareer;
    }
    public void attachCv(String cvFileName) { this.cvFileName = cvFileName; }
    public void updateCvText(String cvText) { this.cvText = cvText; }
}
