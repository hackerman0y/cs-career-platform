package com.careerpath.opportunity.api;

import com.careerpath.auth.domain.AppUser;
import com.careerpath.auth.domain.AppUserRepository;
import com.careerpath.opportunity.domain.Opportunity;
import com.careerpath.opportunity.domain.OpportunityRepository;
import com.careerpath.profile.domain.StudentProfileRepository;
import com.careerpath.project.domain.StudentProjectRepository;
import com.careerpath.skill.domain.StudentSkillRepository;
import com.careerpath.skill.domain.StudentSkill;
import com.careerpath.project.domain.StudentProject;
import com.careerpath.opportunity.service.OpportunityDiscoveryService;
import com.careerpath.opportunity.service.JobSkillAnalyzer;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.security.Principal;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/opportunities")
public class OpportunityController {
    private final OpportunityRepository opportunities; private final AppUserRepository users;
    private final StudentProfileRepository profiles; private final StudentProjectRepository projects; private final StudentSkillRepository skills; private final OpportunityDiscoveryService discovery; private final JobSkillAnalyzer analyzer;
    public OpportunityController(OpportunityRepository opportunities, AppUserRepository users, StudentProfileRepository profiles, StudentProjectRepository projects, StudentSkillRepository skills, OpportunityDiscoveryService discovery, JobSkillAnalyzer analyzer) { this.opportunities = opportunities; this.users = users; this.profiles = profiles; this.projects = projects; this.skills = skills; this.discovery = discovery; this.analyzer = analyzer; }

    @GetMapping public List<OpportunityResponse> all(Principal principal) { return opportunities.findByUserOrderByCreatedAtDesc(user(principal)).stream().map(this::response).toList(); }
    @GetMapping("/discover") public List<OpportunityDiscoveryService.DiscoveryLink> discover(Principal principal) { return discovery.forStudent(user(principal)); }
    @PostMapping @org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.CREATED)
    public OpportunityResponse create(@Valid @RequestBody OpportunityRequest request, Principal principal) { AppUser user = user(principal); return response(opportunities.save(new Opportunity(user, request.title(), request.company(), request.opportunityType(), clean(request.url()), request.description(), analyzedRequirements(request)))); }
    @GetMapping("/{id}") public OpportunityResponse one(@PathVariable UUID id, Principal principal) { return response(find(id, user(principal))); }
    @PutMapping("/{id}") public OpportunityResponse update(@PathVariable UUID id, @Valid @RequestBody OpportunityRequest request, Principal principal) { Opportunity opportunity = find(id, user(principal)); opportunity.update(request.title(), request.company(), request.opportunityType(), clean(request.url()), request.description(), analyzedRequirements(request)); return response(opportunities.save(opportunity)); }
    @DeleteMapping("/{id}") @org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable UUID id, Principal principal) { opportunities.delete(find(id, user(principal))); }
    @GetMapping("/{id}/fit") public FitResponse fit(@PathVariable UUID id, Principal principal) {
        AppUser user = user(principal); Opportunity opportunity = find(id, user);
        List<String> requirements = analyzer.extract(opportunity.getRequirements() + " " + opportunity.getDescription());
        List<StudentProject> studentProjects = projects.findByUserOrderByCreatedAtDesc(user);
        List<StudentSkill> studentSkills = skills.findByUserOrderByNameAsc(user);
        String projectEvidence = studentProjects.stream().map(p -> p.getTechnologies() + " " + p.getName() + " " + p.getDescription()).collect(Collectors.joining(" "));
        String skillEvidence = studentSkills.stream().map(skill -> skill.getName() + " " + skill.getLevel()).collect(Collectors.joining(" "));
        var profile = profiles.findByUser(user).orElseThrow(); String githubEvidence = profile.getGithubUrl();
        String evidence = projectEvidence + " " + skillEvidence + " " + githubEvidence + " " + profile.getCvText();
        String career = profiles.findByUser(user).orElseThrow().getTargetCareer().toLowerCase(Locale.ROOT);
        List<SkillEvidence> matched = requirements.stream().map(skill -> evidenceFor(skill, studentProjects, studentSkills, profile, career)).filter(java.util.Optional::isPresent).map(java.util.Optional::get).toList();
        List<String> matchedSkills = matched.stream().map(SkillEvidence::skill).toList();
        List<SkillGap> missing = requirements.stream().filter(r -> !matchedSkills.contains(r)).map(skill -> new SkillGap(skill, gapAction(skill))).toList();
        int score = requirements.isEmpty() ? 50 : Math.round(matched.size() * 100f / requirements.size());
        boolean cvAttached = profile.getCvFileName() != null && !profile.getCvFileName().isBlank();
        String decision = score >= 70 && cvAttached ? "apply" : score >= 40 ? "prepare" : "build_evidence";
        return new FitResponse(score, decision, matched, missing, nextAction(decision, missing.stream().map(SkillGap::skill).toList(), cvAttached), cvAttached);
    }
    private String nextAction(String decision, List<String> missing, boolean cvAttached) { if (!cvAttached) return "Attach your PDF CV in Profile before you apply, then tailor it to this role."; return switch (decision) { case "apply" -> "Your current evidence matches this role. Tailor your CV and apply."; case "prepare" -> "Strengthen your application with evidence for: " + String.join(", ", missing) + "."; default -> "Build a relevant project or roadmap evidence before applying. Missing: " + String.join(", ", missing) + "."; }; }
    private String analyzedRequirements(OpportunityRequest request) { return String.join(", ", analyzer.extract(request.description() + " " + clean(request.requirements()))); }
    private java.util.Optional<SkillEvidence> evidenceFor(String skill, List<StudentProject> projects, List<StudentSkill> skills, com.careerpath.profile.domain.StudentProfile profile, String career) {
        for (StudentSkill studentSkill : skills) if (analyzer.matches(studentSkill.getName(), skill)) return java.util.Optional.of(new SkillEvidence(skill, studentSkill.getSource(), studentSkill.getLevel() + " level in your Skills Profile"));
        for (StudentProject project : projects) if (analyzer.matches(project.getTechnologies() + " " + project.getName() + " " + project.getDescription(), skill)) return java.util.Optional.of(new SkillEvidence(skill, "project", project.getName()));
        if (analyzer.matches(profile.getCvText(), skill)) return java.util.Optional.of(new SkillEvidence(skill, "cv", "Found in your pasted CV text"));
        if (skill.equals("Git") && profile.getGithubUrl() != null && !profile.getGithubUrl().isBlank()) return java.util.Optional.of(new SkillEvidence(skill, "github", "Public GitHub profile linked"));
        if (analyzer.matches(career, skill)) return java.util.Optional.of(new SkillEvidence(skill, "career", "Matches your selected career direction"));
        return java.util.Optional.empty();
    }
    private String gapAction(String skill) { return switch (skill) { case "Kubernetes", "Docker", "CI/CD", "AWS" -> "Complete a deployment roadmap step and add a project proving " + skill + "."; case "Machine Learning", "TensorFlow", "PyTorch", "Python" -> "Complete a practical AI/ML project using " + skill + "."; default -> "Add evidence through a project, roadmap step, or your Skills Profile."; }; }
    private Opportunity find(UUID id, AppUser user) { return opportunities.findByIdAndUser(id, user).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)); }
    private AppUser user(Principal principal) { return users.findByEmail(principal.getName()).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED)); }
    private String clean(String value) { return value == null ? "" : value.trim(); }
    private OpportunityResponse response(Opportunity opportunity) { return new OpportunityResponse(opportunity.getId(), opportunity.getTitle(), opportunity.getCompany(), opportunity.getOpportunityType(), opportunity.getUrl(), opportunity.getDescription(), opportunity.getRequirements(), opportunity.getCreatedAt()); }
    public record OpportunityRequest(@NotBlank @Size(max = 160) String title, @NotBlank @Size(max = 160) String company, @NotBlank @Pattern(regexp = "job|internship") String opportunityType, @Size(max = 700) String url, @NotBlank @Size(max = 5000) String description, @Size(max = 1000) String requirements) { }
    public record OpportunityResponse(UUID id, String title, String company, String opportunityType, String url, String description, String requirements, Instant createdAt) { }
    public record SkillEvidence(String skill, String source, String detail) { }
    public record SkillGap(String skill, String action) { }
    public record FitResponse(int score, String decision, List<SkillEvidence> matchedRequirements, List<SkillGap> missingRequirements, String nextAction, boolean cvAttached) { }
}
