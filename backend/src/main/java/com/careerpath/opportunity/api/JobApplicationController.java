package com.careerpath.opportunity.api;

import com.careerpath.auth.domain.AppUser;
import com.careerpath.auth.domain.AppUserRepository;
import com.careerpath.opportunity.domain.ApplicationEvent;
import com.careerpath.opportunity.domain.ApplicationEventRepository;
import com.careerpath.opportunity.domain.JobApplication;
import com.careerpath.opportunity.domain.JobApplicationRepository;
import com.careerpath.opportunity.domain.Opportunity;
import com.careerpath.opportunity.domain.OpportunityRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;
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
@RequestMapping("/api/applications")
public class JobApplicationController {
    private static final Set<String> STATUSES = Set.of("saved", "preparing", "applied", "interview", "offer", "rejected", "withdrawn");
    private final JobApplicationRepository applications; private final ApplicationEventRepository events; private final OpportunityRepository opportunities; private final AppUserRepository users;
    public JobApplicationController(JobApplicationRepository applications, ApplicationEventRepository events, OpportunityRepository opportunities, AppUserRepository users) { this.applications = applications; this.events = events; this.opportunities = opportunities; this.users = users; }
    @GetMapping public List<ApplicationResponse> all(Principal principal) { return applications.findByUserOrderByFollowUpDateAsc(user(principal)).stream().map(this::response).toList(); }
    @PostMapping @org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.CREATED) public ApplicationResponse create(@Valid @RequestBody ApplicationRequest request, Principal principal) { AppUser user = user(principal); JobApplication saved = applications.save(new JobApplication(user, opportunity(request.opportunityId(), user), request.status(), request.appliedAt(), request.followUpDate(), clean(request.notes()), clean(request.outcome()))); events.save(new ApplicationEvent(saved, saved.getStatus())); return response(saved); }
    @PutMapping("/{id}") public ApplicationResponse update(@PathVariable UUID id, @Valid @RequestBody ApplicationRequest request, Principal principal) { AppUser user = user(principal); JobApplication application = find(id, user); String previousStatus = application.getStatus(); application.update(request.status(), request.appliedAt(), request.followUpDate(), clean(request.notes()), clean(request.outcome())); JobApplication saved = applications.save(application); if (!previousStatus.equals(saved.getStatus())) events.save(new ApplicationEvent(saved, saved.getStatus())); return response(saved); }
    @DeleteMapping("/{id}") @org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable UUID id, Principal principal) { applications.delete(find(id, user(principal))); }
    private Opportunity opportunity(UUID id, AppUser user) { return opportunities.findByIdAndUser(id, user).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)); }
    private JobApplication find(UUID id, AppUser user) { return applications.findByIdAndUser(id, user).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)); }
    private AppUser user(Principal principal) { return users.findByEmail(principal.getName()).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED)); }
    private String clean(String value) { return value == null ? "" : value.trim(); }
    private ApplicationResponse response(JobApplication application) { Opportunity opportunity = application.getOpportunity(); return new ApplicationResponse(application.getId(), opportunity.getId(), opportunity.getTitle(), opportunity.getCompany(), application.getStatus(), application.getAppliedAt(), application.getFollowUpDate(), application.getNotes(), application.getOutcome()); }
    public record ApplicationRequest(@NotNull UUID opportunityId, @NotBlank @Pattern(regexp = "saved|preparing|applied|interview|offer|rejected|withdrawn") String status, LocalDate appliedAt, LocalDate followUpDate, @Size(max = 1600) String notes, @Size(max = 700) String outcome) { }
    public record ApplicationResponse(UUID id, UUID opportunityId, String opportunityTitle, String company, String status, LocalDate appliedAt, LocalDate followUpDate, String notes, String outcome) { }
}
