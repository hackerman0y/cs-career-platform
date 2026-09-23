package com.careerpath.guide.api;
import java.util.*; import org.springframework.http.*; import org.springframework.web.bind.annotation.*; import org.springframework.web.server.ResponseStatusException;
@RestController @RequestMapping("/api/guides/freelancing")
public class FreelancingGuideController {
 private static final List<Guide> GUIDES=List.of(
  new Guide("getting-started","Freelancing foundations","Understand services, clients, scope, pricing, and the habits that build trust.",List.of("Choose one clear service you can deliver well.","Create two relevant examples before looking for clients.","Define scope, timeline, revisions, and payment in writing.")),
  new Guide("evaluate-jobs","Evaluate a freelance job","Decide whether an opportunity is clear, realistic, safe, and worth your time.",List.of("Look for a concrete deliverable and realistic deadline.","Ask questions when requirements or success criteria are vague.","Avoid unpaid tests that resemble the full project and never pay to receive work.")),
  new Guide("winning-proposals","Write a useful proposal","Show that you understand the client and explain a small, credible plan.",List.of("Open with the outcome the client needs.","Reference one detail from the brief and propose the first steps.","Use relevant proof and finish with one focused question.")),
  new Guide("client-communication","Communicate with clients","Set expectations early and keep decisions visible throughout delivery.",List.of("Confirm decisions and scope changes in writing.","Send short progress updates before the client has to ask.","Raise risks early and offer options with their impact.")));
 @GetMapping public List<GuideSummary> all(){return GUIDES.stream().map(g->new GuideSummary(g.slug(),g.title(),g.summary())).toList();}
 @GetMapping("/{slug}") public Guide one(@PathVariable String slug){return GUIDES.stream().filter(g->g.slug().equals(slug)).findFirst().orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));}
 public record GuideSummary(String slug,String title,String summary){} public record Guide(String slug,String title,String summary,List<String> actions){}
}
