package com.careerpath.project.api;
import com.careerpath.auth.domain.*; import com.careerpath.project.domain.*; import jakarta.validation.Valid; import jakarta.validation.constraints.*;
import java.security.Principal; import java.time.Instant; import java.util.*; import org.springframework.http.*; import org.springframework.web.bind.annotation.*; import org.springframework.web.server.ResponseStatusException;
@RestController @RequestMapping("/api/projects")
public class ProjectController {
 private final StudentProjectRepository projects; private final AppUserRepository users;
 public ProjectController(StudentProjectRepository p,AppUserRepository u){projects=p;users=u;}
 @GetMapping public List<ProjectResponse> all(Principal p){return projects.findByUserOrderByCreatedAtDesc(user(p)).stream().map(this::response).toList();}
 @PostMapping @ResponseStatus(HttpStatus.CREATED) public ProjectResponse create(@Valid @RequestBody ProjectRequest r,Principal p){return response(projects.save(new StudentProject(user(p),r.name(),r.description(),r.technologies(),clean(r.githubUrl()),clean(r.liveDemoUrl()))));}
 @GetMapping("/{id}") public ProjectResponse one(@PathVariable UUID id,Principal p){return response(find(id,user(p)));}
 @PutMapping("/{id}") public ProjectResponse update(@PathVariable UUID id,@Valid @RequestBody ProjectRequest r,Principal p){var project=find(id,user(p));project.update(r.name(),r.description(),r.technologies(),clean(r.githubUrl()),clean(r.liveDemoUrl()));return response(projects.save(project));}
 @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable UUID id,Principal p){projects.delete(find(id,user(p)));}
 private StudentProject find(UUID id,AppUser u){return projects.findByIdAndUser(id,u).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));}
 private AppUser user(Principal p){return users.findByEmail(p.getName()).orElseThrow();} private String clean(String s){return s==null?"":s.trim();}
 private ProjectResponse response(StudentProject p){return new ProjectResponse(p.getId(),p.getName(),p.getDescription(),p.getTechnologies(),p.getGithubUrl(),p.getLiveDemoUrl(),p.getCreatedAt());}
 public record ProjectRequest(@NotBlank @Size(max=120) String name,@NotBlank @Size(max=1200) String description,@NotBlank @Size(max=300) String technologies,@Size(max=400) String githubUrl,@Size(max=400) String liveDemoUrl){}
 public record ProjectResponse(UUID id,String name,String description,String technologies,String githubUrl,String liveDemoUrl,Instant createdAt){}
}
