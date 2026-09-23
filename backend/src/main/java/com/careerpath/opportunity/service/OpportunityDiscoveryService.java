package com.careerpath.opportunity.service;

import com.careerpath.auth.domain.AppUser;
import com.careerpath.profile.domain.StudentProfile;
import com.careerpath.profile.domain.StudentProfileRepository;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class OpportunityDiscoveryService {
    private final StudentProfileRepository profiles;
    public OpportunityDiscoveryService(StudentProfileRepository profiles) { this.profiles = profiles; }

    public List<DiscoveryLink> forStudent(AppUser user) {
        StudentProfile profile = profiles.findByUser(user).orElseThrow();
        String career = profile.getTargetCareer();
        return List.of(
                link("Internships for " + career, "See current LinkedIn internship results in Egypt for your target path.", career + " internship", "internship"),
                link("Entry-level " + career + " roles", "See current LinkedIn job results in Egypt for junior and entry-level roles.", career + " junior entry level", "job"));
    }

    private DiscoveryLink link(String title, String description, String keywords, String type) {
        String url = "https://www.linkedin.com/jobs/search/?keywords=" + encode(keywords) + "&location=" + encode("Egypt") + "&f_E=2";
        return new DiscoveryLink(title, description, "LinkedIn", type, url);
    }
    private String encode(String input) { return URLEncoder.encode(input, StandardCharsets.UTF_8); }
    public record DiscoveryLink(String title, String description, String provider, String opportunityType, String url) { }
}
