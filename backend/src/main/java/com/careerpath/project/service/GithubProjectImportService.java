package com.careerpath.project.service;

import com.careerpath.auth.domain.AppUser;
import com.careerpath.profile.domain.StudentProfile;
import com.careerpath.profile.domain.StudentProfileRepository;
import com.careerpath.project.domain.StudentProject;
import com.careerpath.project.domain.StudentProjectRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.KeyStore;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class GithubProjectImportService {
    private final StudentProfileRepository profiles;
    private final StudentProjectRepository projects;
    private final ObjectMapper objectMapper;
    private final HttpClient github;

    public GithubProjectImportService(StudentProfileRepository profiles, StudentProjectRepository projects, ObjectMapper objectMapper) {
        this.profiles = profiles; this.projects = projects; this.objectMapper = objectMapper; this.github = createGithubClient();
    }

    public List<GithubRepository> preview(AppUser user) {
        StudentProfile profile = profiles.findByUser(user).orElseThrow();
        String username = githubUsername(profile.getGithubUrl());
        Set<String> importedUrls = new LinkedHashSet<>();
        projects.findByUserOrderByCreatedAtDesc(user).forEach(project -> importedUrls.add(project.getGithubUrl()));
        return publicRepositories(username).stream().map(repository -> repository.withImported(importedUrls.contains(repository.githubUrl()))).toList();
    }

    public ImportResult importSelected(AppUser user, List<GithubRepositoryInput> requestedRepositories) {
        StudentProfile profile = profiles.findByUser(user).orElseThrow();
        String username = githubUsername(profile.getGithubUrl());
        List<StudentProject> saved = new ArrayList<>();
        Set<String> seenUrls = new LinkedHashSet<>();
        for (GithubRepositoryInput repository : requestedRepositories) {
            validateRepositoryUrl(username, repository.githubUrl());
            if (!seenUrls.add(repository.githubUrl()) || projects.existsByUserAndGithubUrl(user, repository.githubUrl())) continue;
            saved.add(projects.save(new StudentProject(user, repository.name(), descriptionFor(repository), technologiesFor(repository), repository.githubUrl(), usableUrl(repository.homepage()))));
        }
        return new ImportResult(saved.size(), saved.stream().map(project -> new ImportedProject(project.getId(), project.getName(), project.getGithubUrl())).toList());
    }

    private String githubUsername(String githubUrl) {
        if (githubUrl == null || githubUrl.isBlank()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Add your public GitHub profile URL in Profile first.");
        try {
            URI uri = URI.create(githubUrl.trim());
            String host = uri.getHost();
            String path = uri.getPath();
            if (host == null || !(host.equalsIgnoreCase("github.com") || host.equalsIgnoreCase("www.github.com")) || path == null) throw new IllegalArgumentException();
            String[] parts = path.split("/");
            for (String part : parts) if (!part.isBlank() && part.matches("[A-Za-z0-9-]{1,39}")) return part;
        } catch (IllegalArgumentException ignored) { }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Use a public GitHub profile URL such as https://github.com/your-name.");
    }
    private List<GithubRepository> publicRepositories(String username) {
        try {
            HttpRequest request = HttpRequest.newBuilder(URI.create("https://api.github.com/users/" + username + "/repos?type=owner&sort=updated&direction=desc&per_page=100"))
                    .header("Accept", "application/vnd.github+json").header("User-Agent", "CareerPath/1.0").GET().build();
            HttpResponse<String> result = github.send(request, HttpResponse.BodyHandlers.ofString());
            if (result.statusCode() >= 400) throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "GitHub could not return repositories right now. Check the public profile URL and try again later.");
            JsonNode response = objectMapper.readTree(result.body());
            if (response == null || !response.isArray()) throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "GitHub did not return a repository list.");
            List<GithubRepository> repositories = new ArrayList<>();
            for (JsonNode repository : response) {
                if (repository.path("fork").asBoolean(false) || repository.path("archived").asBoolean(false)) continue;
                repositories.add(new GithubRepository(repository.path("name").asText(), text(repository, "description"), text(repository, "html_url"), text(repository, "homepage"), text(repository, "language"), topics(repository), false));
            }
            return repositories;
        } catch (ResponseStatusException exception) { throw exception;
        } catch (Exception exception) { throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "GitHub could not be reached. Try again in a moment."); }
    }
    private HttpClient createGithubClient() {
        try {
            KeyStore windowsRoot = KeyStore.getInstance("Windows-ROOT"); windowsRoot.load(null, null);
            TrustManagerFactory trustManagers = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm()); trustManagers.init(windowsRoot);
            SSLContext sslContext = SSLContext.getInstance("TLS"); sslContext.init(null, trustManagers.getTrustManagers(), null);
            return HttpClient.newBuilder().sslContext(sslContext).build();
        } catch (Exception ignored) { return HttpClient.newHttpClient(); }
    }
    private String text(JsonNode node, String field) { return node.path(field).isNull() ? "" : node.path(field).asText(""); }
    private List<String> topics(JsonNode node) { List<String> result = new ArrayList<>(); node.path("topics").forEach(topic -> result.add(topic.asText())); return result; }
    private void validateRepositoryUrl(String username, String githubUrl) {
        try {
            URI uri = URI.create(githubUrl);
            String[] parts = uri.getPath().split("/");
            if ((uri.getHost().equalsIgnoreCase("github.com") || uri.getHost().equalsIgnoreCase("www.github.com"))
                    && parts.length >= 3 && parts[1].equalsIgnoreCase(username) && parts[2].matches("[A-Za-z0-9_.-]+")) return;
        } catch (Exception ignored) { }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A repository must belong to the public GitHub profile saved in Profile.");
    }
    private String descriptionFor(GithubRepositoryInput repository) { return repository.description().isBlank() ? repository.name() + " imported from GitHub. Add the problem, solution, and result to make it a stronger case study." : repository.description(); }
    private String technologiesFor(GithubRepositoryInput repository) { List<String> values = new ArrayList<>(); if (!repository.language().isBlank()) values.add(repository.language()); values.addAll(repository.topics()); return values.isEmpty() ? "Review repository" : String.join(", ", values); }
    private String usableUrl(String url) { return url != null && (url.startsWith("https://") || url.startsWith("http://")) ? url : ""; }

    public record GithubRepositoryInput(String name, String description, String githubUrl, String homepage, String language, List<String> topics) { }
    public record GithubRepository(String name, String description, String githubUrl, String homepage, String language, List<String> topics, boolean imported) { GithubRepository withImported(boolean imported) { return new GithubRepository(name, description, githubUrl, homepage, language, topics, imported); } }
    public record ImportedProject(java.util.UUID id, String name, String githubUrl) { }
    public record ImportResult(int importedCount, List<ImportedProject> projects) { }
}
