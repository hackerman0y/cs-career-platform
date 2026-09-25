package com.careerpath;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.careerpath.auth.domain.AppUser;
import com.careerpath.auth.domain.AppUserRepository;
import com.careerpath.checklist.domain.ChecklistItem;
import com.careerpath.checklist.domain.ChecklistItemRepository;
import com.careerpath.checklist.domain.ChecklistProgressRepository;
import com.careerpath.project.domain.StudentProject;
import com.careerpath.project.domain.StudentProjectRepository;
import com.careerpath.roadmap.domain.RoadmapProgressRepository;
import com.careerpath.roadmap.domain.RoadmapStep;
import com.careerpath.roadmap.domain.RoadmapStepRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class P0StabilizationTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;
    @Autowired AppUserRepository users;
    @Autowired RoadmapStepRepository roadmapSteps;
    @Autowired RoadmapProgressRepository roadmapProgress;
    @Autowired ChecklistItemRepository checklistItems;
    @Autowired ChecklistProgressRepository checklistProgress;
    @Autowired StudentProjectRepository projects;

    @Test
    void studentCannotCompleteRoadmapOrChecklistItemsFromAnotherCareer() throws Exception {
        String email = "p0-boundary@example.test";
        String bearer = register(email);
        AppUser user = users.findByEmail(email).orElseThrow();
        RoadmapStep backendStep = roadmapSteps.findByCareerNameOrderByOrderIndex("Backend Developer").getFirst();
        ChecklistItem backendItem = checklistItems.findByCareerNameOrderByOrderIndex("Backend Developer").getFirst();

        mvc.perform(put("/api/roadmap/{id}", backendStep.getId())
                        .header("Authorization", bearer)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"completed\":true}"))
                .andExpect(status().isNotFound());
        mvc.perform(put("/api/checklist/{id}", backendItem.getId())
                        .header("Authorization", bearer)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"completed\":true}"))
                .andExpect(status().isNotFound());

        assertThat(roadmapProgress.findByUserAndStep(user, backendStep)).isEmpty();
        assertThat(checklistProgress.findByUserAndItem(user, backendItem)).isEmpty();
    }

    @Test
    void projectTimestampsArePersistedOnCreateAndUpdate() throws Exception {
        String email = "p0-project-timestamps@example.test";
        register(email);
        AppUser user = users.findByEmail(email).orElseThrow();

        StudentProject project = projects.saveAndFlush(new StudentProject(user, "Project", "A project with evidence", "Java", "", ""));
        Instant createdAt = project.getCreatedAt();
        Instant firstUpdatedAt = project.getUpdatedAt();

        assertThat(createdAt).isNotNull();
        assertThat(firstUpdatedAt).isNotNull();
        assertThat(firstUpdatedAt).isAfterOrEqualTo(createdAt);

        project.update("Project", "An updated project with evidence", "Java, Spring Boot", "", "");
        projects.saveAndFlush(project);
        StudentProject reloaded = projects.findById(project.getId()).orElseThrow();

        assertThat(reloaded.getCreatedAt()).isEqualTo(createdAt.truncatedTo(ChronoUnit.MICROS));
        assertThat(reloaded.getUpdatedAt()).isNotNull().isAfterOrEqualTo(firstUpdatedAt);
    }

    private String register(String email) throws Exception {
        String response = mvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"%s\",\"password\":\"Password123!\"}".formatted(email)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return "Bearer " + json.readTree(response).get("token").asText();
    }
}
