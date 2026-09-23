package com.careerpath.checklist.api;
import com.careerpath.auth.domain.*; import com.careerpath.checklist.domain.*; import com.careerpath.profile.domain.*;
import java.security.Principal; import java.util.*; import org.springframework.http.*; import org.springframework.transaction.annotation.Transactional; import org.springframework.web.bind.annotation.*; import org.springframework.web.server.ResponseStatusException;
@RestController @RequestMapping("/api/checklist")
public class ChecklistController {
 private final ChecklistItemRepository items; private final ChecklistProgressRepository progress; private final AppUserRepository users; private final StudentProfileRepository profiles;
 public ChecklistController(ChecklistItemRepository i,ChecklistProgressRepository p,AppUserRepository u,StudentProfileRepository pr){items=i;progress=p;users=u;profiles=pr;}
 @GetMapping public List<ItemResponse> get(Principal p){AppUser u=user(p);String c=profiles.findByUser(u).orElseThrow().getTargetCareer();Set<UUID> done=new HashSet<>();progress.findByUser(u).stream().filter(ChecklistProgress::isCompleted).forEach(x->done.add(x.getItem().getId()));return items.findByCareerNameOrderByOrderIndex(c).stream().map(i->new ItemResponse(i.getId(),i.getTitle(),i.getDescription(),i.getOrderIndex(),done.contains(i.getId()))).toList();}
 @PutMapping("/{id}") @Transactional public ItemResponse update(@PathVariable UUID id,@RequestBody UpdateRequest r,Principal p){AppUser u=user(p);ChecklistItem i=items.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));var x=progress.findByUserAndItem(u,i).orElseGet(()->new ChecklistProgress(u,i));x.setCompleted(r.completed());progress.save(x);return new ItemResponse(i.getId(),i.getTitle(),i.getDescription(),i.getOrderIndex(),x.isCompleted());}
 private AppUser user(Principal p){return users.findByEmail(p.getName()).orElseThrow();} public record UpdateRequest(boolean completed){} public record ItemResponse(UUID id,String title,String description,int orderIndex,boolean completed){}
}
