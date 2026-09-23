package com.careerpath.profile.api;

import com.careerpath.profile.service.ProfileService;
import com.careerpath.project.service.GithubProjectImportService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.security.Principal;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {
    private final ProfileService profileService;
    private final GithubProjectImportService githubImport;

    public ProfileController(ProfileService profileService, GithubProjectImportService githubImport) {
        this.profileService = profileService;
        this.githubImport = githubImport;
    }

    @GetMapping
    public ProfileResponse getProfile(Principal principal) {
        return profileService.getProfile(principal.getName());
    }

    @PutMapping
    public ProfileResponse updateProfile(Principal principal, @Valid @RequestBody UpdateProfileRequest request) {
        return profileService.updateProfile(principal.getName(), request);
    }

    @GetMapping("/github-repositories")
    public List<GithubProjectImportService.GithubRepository> githubRepositories(Principal principal) {
        return githubImport.preview(profileService.userForEmail(principal.getName()));
    }

    @org.springframework.web.bind.annotation.PostMapping("/import-github")
    public GithubProjectImportService.ImportResult importGithub(@Valid @RequestBody GithubImportRequest request, Principal principal) {
        return githubImport.importSelected(profileService.userForEmail(principal.getName()), request.repositories());
    }

    public record UpdateProfileRequest(
            @NotBlank @Size(max = 80) String fullName,
            @Size(max = 120) String university,
            @NotBlank @Size(max = 30) String academicYear,
            @Size(max = 300) String githubUrl,
            @Size(max = 300) String linkedinUrl,
            @NotBlank @Size(max = 60) String targetCareer) {
    }

    public record ProfileResponse(String fullName, String university, String academicYear,
            String githubUrl, String linkedinUrl, String targetCareer) {
    }

    public record GithubImportRequest(@jakarta.validation.constraints.NotEmpty List<GithubProjectImportService.GithubRepositoryInput> repositories) {
    }
}
