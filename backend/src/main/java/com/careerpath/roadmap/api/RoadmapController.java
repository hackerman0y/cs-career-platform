package com.careerpath.roadmap.api;
import com.careerpath.auth.domain.*;
import com.careerpath.profile.domain.*;
import com.careerpath.roadmap.domain.*;
import com.careerpath.skill.domain.*;
import java.security.Principal;
import java.time.Instant;
import java.util.*;
import org.springframework.http.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
@RestController @RequestMapping("/api/roadmap")
public class RoadmapController {
    private final RoadmapStepRepository steps; private final RoadmapProgressRepository progress;
    private final AppUserRepository users; private final StudentProfileRepository profiles; private final StudentSkillRepository skills;
    public RoadmapController(RoadmapStepRepository s,RoadmapProgressRepository p,AppUserRepository u,StudentProfileRepository pr,StudentSkillRepository skills){steps=s;progress=p;users=u;profiles=pr;this.skills=skills;}
    @GetMapping public List<StepResponse> get(Principal principal){
        AppUser user=user(principal); String career=profiles.findByUser(user).orElseThrow().getTargetCareer();
        Map<UUID,RoadmapProgress> done=new HashMap<>(); progress.findByUser(user).forEach(p->done.put(p.getStep().getId(),p));
        return steps.findByCareerNameOrderByOrderIndex(career).stream().map(s->{var p=done.get(s.getId());return new StepResponse(s.getId(),s.getTitle(),s.getDescription(),s.getOrderIndex(),p!=null&&p.isCompleted(),p==null?null:p.getCompletedAt());}).toList();
    }
    @PutMapping("/{stepId}") @Transactional public StepResponse update(@PathVariable UUID stepId,@RequestBody UpdateRequest request,Principal principal){
        AppUser user=user(principal); String career=profiles.findByUser(user).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND)).getTargetCareer();
        RoadmapStep step=steps.findById(stepId).filter(candidate -> candidate.getCareerName().equals(career)).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
        RoadmapProgress saved=progress.findByUserAndStep(user,step).orElseGet(()->new RoadmapProgress(user,step)); saved.setCompleted(request.completed()); progress.save(saved);
        if(request.completed()) inferredSkills(step).forEach(name -> skills.findByUserAndNameIgnoreCase(user,name).orElseGet(()->skills.save(new StudentSkill(user,name,"beginner","roadmap"))));
        return new StepResponse(step.getId(),step.getTitle(),step.getDescription(),step.getOrderIndex(),saved.isCompleted(),saved.getCompletedAt());
    }
    private AppUser user(Principal p){return users.findByEmail(p.getName()).orElseThrow(()->new ResponseStatusException(HttpStatus.UNAUTHORIZED));}
    private List<String> inferredSkills(RoadmapStep step){String text=(step.getTitle()+" "+step.getDescription()).toLowerCase(Locale.ROOT); Map<String,String> map=Map.ofEntries(Map.entry("java","Java"),Map.entry("sql","SQL"),Map.entry("spring","Spring Boot"),Map.entry("python","Python"),Map.entry("angular","Angular"),Map.entry("typescript","TypeScript"),Map.entry("javascript","JavaScript"),Map.entry("html","HTML"),Map.entry("css","CSS"),Map.entry("docker","Docker"),Map.entry("git","Git"),Map.entry("linux","Linux"),Map.entry("security","Cybersecurity"),Map.entry("cloud","Cloud"),Map.entry("api","REST APIs"),Map.entry("testing","Testing")); return map.entrySet().stream().filter(entry->text.contains(entry.getKey())).map(Map.Entry::getValue).distinct().toList();}
    public record UpdateRequest(boolean completed){} public record StepResponse(UUID id,String title,String description,int orderIndex,boolean completed,Instant completedAt){}
}
