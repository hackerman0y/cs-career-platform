package com.careerpath.roadmap.api;
import com.careerpath.auth.domain.*;
import com.careerpath.profile.domain.*;
import com.careerpath.roadmap.domain.*;
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
    private final AppUserRepository users; private final StudentProfileRepository profiles;
    public RoadmapController(RoadmapStepRepository s,RoadmapProgressRepository p,AppUserRepository u,StudentProfileRepository pr){steps=s;progress=p;users=u;profiles=pr;}
    @GetMapping public List<StepResponse> get(Principal principal){
        AppUser user=user(principal); String career=profiles.findByUser(user).orElseThrow().getTargetCareer();
        Map<UUID,RoadmapProgress> done=new HashMap<>(); progress.findByUser(user).forEach(p->done.put(p.getStep().getId(),p));
        return steps.findByCareerNameOrderByOrderIndex(career).stream().map(s->{var p=done.get(s.getId());return new StepResponse(s.getId(),s.getTitle(),s.getDescription(),s.getOrderIndex(),p!=null&&p.isCompleted(),p==null?null:p.getCompletedAt());}).toList();
    }
    @PutMapping("/{stepId}") @Transactional public StepResponse update(@PathVariable UUID stepId,@RequestBody UpdateRequest request,Principal principal){
        AppUser user=user(principal); RoadmapStep step=steps.findById(stepId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
        RoadmapProgress saved=progress.findByUserAndStep(user,step).orElseGet(()->new RoadmapProgress(user,step)); saved.setCompleted(request.completed()); progress.save(saved);
        return new StepResponse(step.getId(),step.getTitle(),step.getDescription(),step.getOrderIndex(),saved.isCompleted(),saved.getCompletedAt());
    }
    private AppUser user(Principal p){return users.findByEmail(p.getName()).orElseThrow(()->new ResponseStatusException(HttpStatus.UNAUTHORIZED));}
    public record UpdateRequest(boolean completed){} public record StepResponse(UUID id,String title,String description,int orderIndex,boolean completed,Instant completedAt){}
}
