package com.careerpath.readiness.api;

import com.careerpath.auth.domain.AppUser;
import com.careerpath.auth.domain.AppUserRepository;
import com.careerpath.readiness.service.ReadinessService;
import java.security.Principal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/readiness")
public class ReadinessController {
    private final ReadinessService readiness; private final AppUserRepository users;
    public ReadinessController(ReadinessService readiness, AppUserRepository users) { this.readiness = readiness; this.users = users; }
    @GetMapping public ReadinessService.ReadinessResponse get(Principal principal) { return readiness.recalculate(user(principal)); }
    @PostMapping("/recalculate") public ReadinessService.ReadinessResponse recalculate(Principal principal) { return readiness.recalculate(user(principal)); }
    private AppUser user(Principal principal) { return users.findByEmail(principal.getName()).orElseThrow(); }
}
