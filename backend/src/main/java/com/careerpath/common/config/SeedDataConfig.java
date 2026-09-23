package com.careerpath.common.config;

import com.careerpath.assessment.domain.AssessmentQuestion;
import com.careerpath.assessment.domain.AssessmentQuestionRepository;
import com.careerpath.career.domain.CareerPath;
import com.careerpath.career.domain.CareerPathRepository;
import com.careerpath.roadmap.domain.RoadmapStep;
import com.careerpath.roadmap.domain.RoadmapStepRepository;
import com.careerpath.checklist.domain.ChecklistItem;
import com.careerpath.checklist.domain.ChecklistItemRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SeedDataConfig {
    @Bean
    CommandLineRunner seedData(CareerPathRepository careerPaths, AssessmentQuestionRepository questions,
            RoadmapStepRepository roadmapSteps, ChecklistItemRepository checklistItems) {
        return arguments -> {
            addCareerPath(careerPaths, "Frontend Developer", "Build accessible, responsive web experiences that people enjoy using.");
            addCareerPath(careerPaths, "Backend Developer", "Design APIs, databases, and the reliable systems behind products.");
            addCareerPath(careerPaths, "Mobile Developer", "Create useful Android and iOS experiences for people on the move.");
            addCareerPath(careerPaths, "Data Analyst", "Use data to find patterns and help teams make better decisions.");

            addQuestion(questions, "build", "Which kind of work sounds most exciting to you?");
            addQuestion(questions, "project", "Which first portfolio project would you be proud to share?");
            addQuestion(questions, "strength", "Which strength feels most like you right now?");

            seedCareerPlan(roadmapSteps, checklistItems, "Frontend Developer", new String[][]{
                {"HTML and semantic structure", "Build accessible pages with meaningful HTML."}, {"CSS and responsive layouts", "Create layouts that work across screen sizes."},
                {"JavaScript fundamentals", "Use functions, arrays, objects, events, and the DOM."}, {"TypeScript", "Add safe types and model application data."},
                {"Angular foundations", "Build components, routes, forms, and HTTP services."}, {"Testing and accessibility", "Test important behavior and support keyboard and screen reader users."},
                {"Portfolio project", "Ship a polished web application and explain your decisions."}});
            seedCareerPlan(roadmapSteps, checklistItems, "Backend Developer", new String[][]{
                {"Java foundations", "Master classes, collections, exceptions, and clean code."}, {"SQL and data modeling", "Design relational schemas and write reliable queries."},
                {"Spring Boot APIs", "Build validated REST endpoints with clear DTOs."}, {"Authentication and security", "Protect data with JWT, authorization, and safe passwords."},
                {"Testing", "Cover services and APIs with repeatable tests."}, {"Deployment", "Package and deploy an API with PostgreSQL."},
                {"Portfolio project", "Ship a documented backend serving a real use case."}});
            seedCareerPlan(roadmapSteps, checklistItems, "Mobile Developer", new String[][]{
                {"Programming foundations", "Build confidence with variables, functions, and state."}, {"Mobile UI", "Create responsive screens and navigation."},
                {"Local data", "Store settings and offline application data."}, {"APIs and networking", "Connect the app to authenticated web services."},
                {"Device capabilities", "Use notifications, media, and permissions responsibly."}, {"Testing and release", "Test on devices and prepare a production build."},
                {"Portfolio app", "Publish a useful app with a clear case study."}});
            seedCareerPlan(roadmapSteps, checklistItems, "Data Analyst", new String[][]{
                {"Spreadsheet foundations", "Clean, calculate, and summarize structured data."}, {"SQL", "Query, join, group, and validate relational data."},
                {"Statistics", "Use descriptive statistics and avoid common interpretation errors."}, {"Python for analysis", "Explore data with pandas and reproducible notebooks."},
                {"Data visualization", "Choose charts that make the conclusion easy to understand."}, {"Business communication", "Turn analysis into a decision-focused story."},
                {"Portfolio analysis", "Publish a documented analysis using a real dataset."}});
        };
    }

    private void seedCareerPlan(RoadmapStepRepository steps, ChecklistItemRepository items, String career, String[][] plan) {
        for (int index = 0; index < plan.length; index++) {
            int order = index + 1;
            if (!steps.existsByCareerNameAndOrderIndex(career, order)) steps.save(new RoadmapStep(career, plan[index][0], plan[index][1], order));
        }
        String[][] checklist = {{"Complete your profile", "Add your education, links, and target career."}, {"Create a focused GitHub profile", "Pin your strongest repositories and add useful README files."},
            {"Publish two relevant projects", "Show complete work related to your target role."}, {"Prepare a one-page CV", "Describe outcomes, skills, and evidence clearly."},
            {"Improve your LinkedIn profile", "Use a clear headline, about section, and project links."}, {"Practice an interview story", "Explain one project, a challenge, and what you learned."},
            {"Apply with a tracking routine", "Track tailored applications and follow-ups every week."}};
        for(int index=0;index<checklist.length;index++){int order=index+1;if(!items.existsByCareerNameAndOrderIndex(career,order))items.save(new ChecklistItem(career,checklist[index][0],checklist[index][1],order));}
    }

    private void addCareerPath(CareerPathRepository repository, String name, String description) {
        if (repository.findAll().stream().noneMatch(path -> path.getName().equals(name))) {
            repository.save(new CareerPath(name, description));
        }
    }

    private void addQuestion(AssessmentQuestionRepository repository, String key, String prompt) {
        if (!repository.existsByQuestionKey(key)) {
            repository.save(new AssessmentQuestion(key, prompt));
        }
    }
}
