package com.careerpath.readiness.service;

import com.careerpath.assessment.domain.AssessmentAnswerRepository;
import com.careerpath.auth.domain.AppUser;
import com.careerpath.checklist.domain.ChecklistProgressRepository;
import com.careerpath.portfolio.domain.PortfolioSettingsRepository;
import com.careerpath.profile.domain.StudentProfile;
import com.careerpath.profile.domain.StudentProfileRepository;
import com.careerpath.project.domain.StudentProject;
import com.careerpath.project.domain.StudentProjectRepository;
import com.careerpath.readiness.domain.ProgressEvidence;
import com.careerpath.readiness.domain.ProgressEvidenceRepository;
import com.careerpath.readiness.domain.ReadinessAssessment;
import com.careerpath.readiness.domain.ReadinessAssessmentRepository;
import com.careerpath.roadmap.domain.RoadmapProgressRepository;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReadinessService {
    private final StudentProfileRepository profiles;
    private final AssessmentAnswerRepository answers;
    private final StudentProjectRepository projects;
    private final PortfolioSettingsRepository portfolios;
    private final RoadmapProgressRepository roadmapProgress;
    private final ChecklistProgressRepository checklistProgress;
    private final ProgressEvidenceRepository evidence;
    private final ReadinessAssessmentRepository assessments;

    public ReadinessService(StudentProfileRepository profiles, AssessmentAnswerRepository answers,
            StudentProjectRepository projects, PortfolioSettingsRepository portfolios,
            RoadmapProgressRepository roadmapProgress, ChecklistProgressRepository checklistProgress,
            ProgressEvidenceRepository evidence, ReadinessAssessmentRepository assessments) {
        this.profiles = profiles; this.answers = answers; this.projects = projects; this.portfolios = portfolios;
        this.roadmapProgress = roadmapProgress; this.checklistProgress = checklistProgress;
        this.evidence = evidence; this.assessments = assessments;
    }

    @Transactional
    public ReadinessResponse recalculate(AppUser user) {
        StudentProfile profile = profiles.findByUser(user).orElseThrow();
        List<Rule> rules = new ArrayList<>();
        rules.add(rule("profile", 15, profileComplete(profile), "Complete your profile", "Add your name, university, academic year, and target career."));
        rules.add(rule("assessment", 10, answers.findByUser(user).size() >= 3, "Career assessment", "Complete the career assessment to confirm a direction."));
        rules.add(rule("github", 10, present(profile.getGithubUrl()), "GitHub profile", "Add a GitHub URL to show your code and project history."));
        rules.add(rule("linkedin", 10, present(profile.getLinkedinUrl()), "LinkedIn profile", "Add your LinkedIn URL so recruiters can find you."));
        long qualifyingProjects = projects.findByUserOrderByCreatedAtDesc(user).stream().filter(this::qualifyingProject).count();
        rules.add(rule("projects", 20, qualifyingProjects >= 2, "Two qualifying projects", "Add two projects with a description, technologies, and a GitHub repository or live demo."));
        boolean published = portfolios.findByUser(user).map(p -> p.isPublished()).orElse(false);
        rules.add(rule("portfolio", 15, published, "Published portfolio", "Publish your portfolio after adding your strongest work."));
        long completedRoadmap = roadmapProgress.findByUser(user).stream().filter(p -> p.isCompleted()).count();
        rules.add(rule("roadmap", 10, completedRoadmap >= 2, "Roadmap progress", "Complete two practical roadmap steps to build momentum."));
        long completedChecklist = checklistProgress.findByUser(user).stream().filter(p -> p.isCompleted()).count();
        rules.add(rule("job-preparation", 10, completedChecklist >= 4, "Job preparation", "Complete four readiness tasks including CV, LinkedIn, and interview preparation."));

        evidence.deleteByUser(user);
        int score = 0;
        List<EvidenceResponse> evidenceResponses = new ArrayList<>();
        List<ActionResponse> nextActions = new ArrayList<>();
        for (Rule rule : rules) {
            if (rule.complete()) {
                score += rule.points();
                ProgressEvidence saved = evidence.save(new ProgressEvidence(user, rule.key(), rule.title(), "Verified automatically from your CareerPath workspace."));
                evidenceResponses.add(new EvidenceResponse(saved.getEvidenceKey(), saved.getTitle(), saved.getDetail(), saved.getCompletionSource(), saved.getVerifiedAt()));
            } else {
                nextActions.add(new ActionResponse(rule.key(), rule.title(), rule.action(), rule.points()));
            }
        }
        ReadinessAssessment assessment = assessments.findByUser(user).orElseGet(() -> new ReadinessAssessment(user));
        assessment.updateScore(score);
        assessments.save(assessment);
        String status = score >= 80 ? "ready_to_apply" : score >= 50 ? "building_evidence" : "getting_started";
        return new ReadinessResponse(score, status, profile.getTargetCareer(), nextActions, evidenceResponses, assessment.getEvaluatedAt());
    }

    private Rule rule(String key, int points, boolean complete, String title, String action) { return new Rule(key, points, complete, title, action); }
    private boolean profileComplete(StudentProfile profile) { return present(profile.getFullName()) && !"Student".equals(profile.getFullName()) && present(profile.getUniversity()) && present(profile.getAcademicYear()) && present(profile.getTargetCareer()); }
    private boolean present(String value) { return value != null && !value.isBlank(); }
    private boolean qualifyingProject(StudentProject project) { return present(project.getName()) && present(project.getDescription()) && present(project.getTechnologies()) && (present(project.getGithubUrl()) || present(project.getLiveDemoUrl())); }

    private record Rule(String key, int points, boolean complete, String title, String action) { }
    public record ActionResponse(String key, String title, String action, int points) { }
    public record EvidenceResponse(String key, String title, String detail, String source, Instant verifiedAt) { }
    public record ReadinessResponse(int score, String status, String targetCareer, List<ActionResponse> nextActions, List<EvidenceResponse> evidence, Instant evaluatedAt) { }
}
