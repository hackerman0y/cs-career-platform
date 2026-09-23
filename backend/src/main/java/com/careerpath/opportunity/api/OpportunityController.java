package com.careerpath.opportunity.api;

import com.careerpath.auth.domain.AppUser;
import com.careerpath.auth.domain.AppUserRepository;
import com.careerpath.opportunity.domain.Opportunity;
import com.careerpath.opportunity.domain.OpportunityRepository;
import com.careerpath.profile.domain.StudentProfileRepository;
import com.careerpath.project.domain.StudentProjectRepository;
import com.careerpath.opportunity.service.OpportunityDiscoveryService;
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
    private final StudentProfileRepository profiles; private final StudentProjectRepository projects; private final OpportunityDiscoveryService discovery;
    public OpportunityController(OpportunityRepository opportunities, AppUserRepository users, StudentProfileRepository profiles, StudentProjectRepository projects, OpportunityDiscoveryService discovery) { this.opportunities = opportunities; this.users = users; this.profiles = profiles; this.projects = projects; this.discovery = discovery; }

    @GetMapping public List<OpportunityResponse> all(Principal principal) { return opportunities.findByUserOrderByCreatedAtDesc(user(principal)).stream().map(this::response).toList(); }
    @GetMapping("/discover") public List<OpportunityDiscoveryService.DiscoveryLink> discover(Principal principal) { return discovery.forStudent(user(principal)); }
    @PostMapping @org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.CREATED)
    public OpportunityResponse create(@Valid @RequestBody OpportunityRequest request, Principal principal) { AppUser user = user(principal); return response(opportunities.save(new Opportunity(user, request.title(), request.company(), request.opportunityType(), clean(request.url()), request.description(), request.requirements()))); }
    @GetMapping("/{id}") public OpportunityResponse one(@PathVariable UUID id, Principal principal) { return response(find(id, user(principal))); }
    @PutMapping("/{id}") public OpportunityResponse update(@PathVariable UUID id, @Valid @RequestBody OpportunityRequest request, Principal principal) { Opportunity opportunity = find(id, user(principal)); opportunity.update(request.title(), request.company(), request.opportunityType(), clean(request.url()), request.description(), request.requirements()); return response(opportunities.save(opportunity)); }
    @DeleteMapping("/{id}") @org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable UUID id, Principal principal) { opportunities.delete(find(id, user(principal))); }
    @GetMapping("/{id}/fit") public FitResponse fit(@PathVariable UUID id, Principal principal) {
        AppUser user = user(principal); Opportunity opportunity = find(id, user);
        List<String> requirements = split(opportunity.getRequirements());
        String evidence = projects.findByUserOrderByCreatedAtDesc(user).stream().map(p -> p.getTechnologies() + " " + p.getName() + " " + p.getDescription()).collect(Collectors.joining(" ")).toLowerCase(Locale.ROOT);
        String career = profiles.findByUser(user).orElseThrow().getTargetCareer().toLowerCase(Locale.ROOT);
        List<String> matched = requirements.stream().filter(r -> evidence.contains(r.toLowerCase(Locale.ROOT)) || career.contains(r.toLowerCase(Locale.ROOT))).toList();
        List<String> missing = requirements.stream().filter(r -> !matched.contains(r)).toList();
        int score = requirements.isEmpty() ? 50 : Math.round(matched.size() * 100f / requirements.size());
        String decision = score >= 70 ? "apply" : score >= 40 ? "prepare" : "build_evidence";
        return new FitResponse(score, decision, matched, missing, nextAction(decision, missing));
    }
    private String nextAction(String decision, List<String> missing) { return switch (decision) { case "apply" -> "Your current evidence matches this role. Tailor your CV and apply."; case "prepare" -> "Strengthen your application with evidence for: " + String.join(", ", missing) + "."; default -> "Build a relevant project or roadmap evidence before applying. Missing: " + String.join(", ", missing) + "."; }; }
    private List<String> split(String value) { return Arrays.stream(value.split(",")).map(String::trim).filter(v -> !v.isBlank()).distinct().toList(); }
    private Opportunity find(UUID id, AppUser user) { return opportunities.findByIdAndUser(id, user).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)); }
    private AppUser user(Principal principal) { return users.findByEmail(principal.getName()).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED)); }
    private String clean(String value) { return value == null ? "" : value.trim(); }
    private OpportunityResponse response(Opportunity opportunity) { return new OpportunityResponse(opportunity.getId(), opportunity.getTitle(), opportunity.getCompany(), opportunity.getOpportunityType(), opportunity.getUrl(), opportunity.getDescription(), opportunity.getRequirements(), opportunity.getCreatedAt()); }
    public record OpportunityRequest(@NotBlank @Size(max = 160) String title, @NotBlank @Size(max = 160) String company, @NotBlank @Pattern(regexp = "job|internship") String opportunityType, @Size(max = 700) String url, @NotBlank @Size(max = 1600) String description, @NotBlank @Size(max = 700) String requirements) { }
    public record OpportunityResponse(UUID id, String title, String company, String opportunityType, String url, String description, String requirements, Instant createdAt) { }
    public record FitResponse(int score, String decision, List<String> matchedRequirements, List<String> missingRequirements, String nextAction) { }
}
