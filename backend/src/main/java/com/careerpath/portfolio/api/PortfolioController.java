package com.careerpath.portfolio.api;
import com.careerpath.auth.domain.*; import com.careerpath.portfolio.domain.*; import com.careerpath.profile.domain.*; import com.careerpath.project.domain.*;
import jakarta.validation.Valid; import jakarta.validation.constraints.*; import java.security.Principal; import java.util.*; import org.springframework.http.*; import org.springframework.web.bind.annotation.*; import org.springframework.web.server.ResponseStatusException;
@RestController @RequestMapping("/api/portfolio")
public class PortfolioController {
 private final PortfolioSettingsRepository settings; private final AppUserRepository users; private final StudentProfileRepository profiles; private final StudentProjectRepository projects;
 public PortfolioController(PortfolioSettingsRepository s,AppUserRepository u,StudentProfileRepository p,StudentProjectRepository pr){settings=s;users=u;profiles=p;projects=pr;}
 @GetMapping public PortfolioResponse mine(Principal p){return response(getOrCreate(user(p)),true);}
 @PutMapping public PortfolioResponse update(@Valid @RequestBody PortfolioRequest r,Principal p){AppUser u=user(p);String username=r.username().toLowerCase();if(settings.existsByUsernameIgnoreCaseAndUserNot(username,u))throw new ResponseStatusException(HttpStatus.CONFLICT,"Username is already used");var s=getOrCreate(u);s.update(username,r.headline(),r.bio(),r.published());return response(settings.save(s),true);}
 @GetMapping("/public/{username}") public PortfolioResponse publicPortfolio(@PathVariable String username){var s=settings.findByUsernameIgnoreCase(username).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));if(!s.isPublished())throw new ResponseStatusException(HttpStatus.NOT_FOUND);return response(s,false);}
 private PortfolioSettings getOrCreate(AppUser u){return settings.findByUser(u).orElseGet(()->settings.save(new PortfolioSettings(u,availableUsername(u))));}
 private String availableUsername(AppUser u){String base=u.getEmail().split("@")[0].toLowerCase().replaceAll("[^a-z0-9_-]","");if(base.length()<3)base="student";String candidate=base;int n=2;while(settings.findByUsernameIgnoreCase(candidate).isPresent())candidate=base+n++;return candidate;}
 private PortfolioResponse response(PortfolioSettings s,boolean owner){var profile=profiles.findByUser(s.getUser()).orElseThrow();var cards=projects.findByUserOrderByCreatedAtDesc(s.getUser()).stream().map(p->new ProjectCard(p.getName(),p.getDescription(),p.getTechnologies(),p.getGithubUrl(),p.getLiveDemoUrl())).toList();return new PortfolioResponse(s.getUsername(),profile.getFullName(),profile.getTargetCareer(),profile.getGithubUrl(),profile.getLinkedinUrl(),s.getHeadline(),s.getBio(),s.isPublished(),owner,cards);}
 private AppUser user(Principal p){return users.findByEmail(p.getName()).orElseThrow();}
 public record PortfolioRequest(@NotBlank @Pattern(regexp="[a-zA-Z0-9_-]{3,50}") String username,@NotBlank @Size(max=120) String headline,@NotBlank @Size(max=1200) String bio,boolean published){}
 public record ProjectCard(String name,String description,String technologies,String githubUrl,String liveDemoUrl){}
 public record PortfolioResponse(String username,String fullName,String targetCareer,String githubUrl,String linkedinUrl,String headline,String bio,boolean published,boolean owner,List<ProjectCard> projects){}
}
