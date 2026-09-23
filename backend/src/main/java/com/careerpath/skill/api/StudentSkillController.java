package com.careerpath.skill.api;
import com.careerpath.auth.domain.*;
import com.careerpath.skill.domain.*;
import jakarta.validation.Valid; import jakarta.validation.constraints.*;
import java.security.Principal; import java.util.*;
import org.springframework.http.*; import org.springframework.web.bind.annotation.*; import org.springframework.web.server.ResponseStatusException;

@RestController @RequestMapping("/api/skills")
public class StudentSkillController {
 private final StudentSkillRepository skills; private final AppUserRepository users;
 public StudentSkillController(StudentSkillRepository skills, AppUserRepository users) { this.skills=skills; this.users=users; }
 @GetMapping public List<SkillResponse> all(Principal principal) { return skills.findByUserOrderByNameAsc(user(principal)).stream().map(this::response).toList(); }
 @PostMapping @ResponseStatus(HttpStatus.CREATED) public SkillResponse add(@Valid @RequestBody SkillRequest request, Principal principal) { AppUser user=user(principal); StudentSkill skill=skills.findByUserAndNameIgnoreCase(user, request.name().trim()).orElseGet(()->new StudentSkill(user, request.name().trim(), request.level())); skill.update(request.level()); return response(skills.save(skill)); }
 @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable UUID id, Principal principal) { skills.delete(skills.findByIdAndUser(id,user(principal)).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND))); }
 private AppUser user(Principal p) { return users.findByEmail(p.getName()).orElseThrow(()->new ResponseStatusException(HttpStatus.UNAUTHORIZED)); }
 private SkillResponse response(StudentSkill skill) { return new SkillResponse(skill.getId(),skill.getName(),skill.getLevel(),skill.getSource()); }
 public record SkillRequest(@NotBlank @Size(max=80) String name,@NotBlank @Pattern(regexp="beginner|intermediate|advanced") String level) { }
 public record SkillResponse(UUID id,String name,String level,String source) { }
}
