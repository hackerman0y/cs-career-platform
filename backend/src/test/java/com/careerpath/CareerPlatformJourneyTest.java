package com.careerpath;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CareerPlatformJourneyTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;

    @Test
    void completeStudentJourneyPersistsAndPublishes() throws Exception {
        String registration = mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"journey@example.test\",\"password\":\"Password123!\"}"))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        String token = json.readTree(registration).get("token").asText();
        String bearer = "Bearer " + token;

        mvc.perform(put("/api/profile").header("Authorization", bearer).contentType(MediaType.APPLICATION_JSON).content("""
                {"fullName":"Journey Student","university":"Test University","academicYear":"Year 3","githubUrl":"https://github.com/journey","linkedinUrl":"","targetCareer":"Backend Developer"}
                """)).andExpect(status().isOk()).andExpect(jsonPath("$.targetCareer").value("Backend Developer"));

        String roadmap = mvc.perform(get("/api/roadmap").header("Authorization", bearer)).andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(7)).andReturn().getResponse().getContentAsString();
        String stepId = json.readTree(roadmap).get(0).get("id").asText();
        mvc.perform(put("/api/roadmap/{id}", stepId).header("Authorization", bearer).contentType(MediaType.APPLICATION_JSON)
                .content("{\"completed\":true}")).andExpect(status().isOk()).andExpect(jsonPath("$.completed").value(true));

        mvc.perform(get("/api/assessment/result").header("Authorization", bearer))
                .andExpect(status().isOk()).andExpect(jsonPath("$.recommendations.length()").value(0));

        String created = mvc.perform(post("/api/projects").header("Authorization", bearer).contentType(MediaType.APPLICATION_JSON).content("""
                {"name":"Test API","description":"A tested project","technologies":"Java, Spring Boot","githubUrl":"https://github.com/journey/api","liveDemoUrl":""}
                """)).andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        assertThat(json.readTree(created).get("name").asText()).isEqualTo("Test API");

        mvc.perform(get("/api/readiness").header("Authorization", bearer)).andExpect(status().isOk())
                .andExpect(jsonPath("$.score").isNumber()).andExpect(jsonPath("$.evidence.length()").value(2));

        mvc.perform(get("/api/opportunities/discover").header("Authorization", bearer)).andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2)).andExpect(jsonPath("$[0].provider").value("LinkedIn"));

        String resources = mvc.perform(get("/api/roadmap/{id}/resources", stepId).header("Authorization", bearer))
                .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1))
                .andReturn().getResponse().getContentAsString();
        assertThat(json.readTree(resources).get(0).get("provider").asText()).isEqualTo("YouTube");

        String opportunity = mvc.perform(post("/api/opportunities").header("Authorization", bearer).contentType(MediaType.APPLICATION_JSON).content("""
                {"title":"Backend Internship","company":"CareerPath Labs","opportunityType":"internship","url":"https://example.test/role","description":"Build and test APIs.","requirements":"Java, Spring Boot, SQL"}
                """)).andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        String opportunityId = json.readTree(opportunity).get("id").asText();
        mvc.perform(get("/api/opportunities/{id}/fit", opportunityId).header("Authorization", bearer)).andExpect(status().isOk())
                .andExpect(jsonPath("$.score").value(67)).andExpect(jsonPath("$.decision").value("prepare"));
        mvc.perform(post("/api/applications").header("Authorization", bearer).contentType(MediaType.APPLICATION_JSON).content("""
                {"opportunityId":"%s","status":"applied","appliedAt":"2026-09-23","followUpDate":"2026-09-30","notes":"Tailored CV sent.","outcome":""}
                """.formatted(opportunityId))).andExpect(status().isCreated()).andExpect(jsonPath("$.status").value("applied"));
        mvc.perform(get("/api/applications").header("Authorization", bearer)).andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        mvc.perform(get("/api/portfolio").header("Authorization", bearer)).andExpect(status().isOk());
        mvc.perform(put("/api/portfolio").header("Authorization", bearer).contentType(MediaType.APPLICATION_JSON).content("""
                {"username":"journey-student","headline":"Backend Developer","bio":"Building useful APIs.","published":true}
                """)).andExpect(status().isOk()).andExpect(jsonPath("$.published").value(true));
        mvc.perform(get("/api/portfolio/public/journey-student")).andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Journey Student")).andExpect(jsonPath("$.projects.length()").value(1));
        mvc.perform(post("/api/auth/logout").header("Authorization", bearer)).andExpect(status().isNoContent());
    }
}
