package com.careerpath.common.config;

import com.careerpath.assessment.domain.AssessmentQuestion;
import com.careerpath.assessment.domain.AssessmentQuestionRepository;
import com.careerpath.career.domain.CareerPath;
import com.careerpath.career.domain.CareerPathRepository;
import com.careerpath.roadmap.domain.RoadmapStep;
import com.careerpath.roadmap.domain.RoadmapStepRepository;
import com.careerpath.checklist.domain.ChecklistItem;
import com.careerpath.checklist.domain.ChecklistItemRepository;
import com.careerpath.learning.domain.LearningResource;
import com.careerpath.learning.domain.LearningResourceRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SeedDataConfig {
    @Bean
    CommandLineRunner seedData(CareerPathRepository careerPaths, AssessmentQuestionRepository questions,
            RoadmapStepRepository roadmapSteps, ChecklistItemRepository checklistItems, LearningResourceRepository learningResources) {
        return arguments -> {
            addCareerPath(careerPaths, "Frontend Developer", "Build accessible, responsive web experiences that people enjoy using.");
            addCareerPath(careerPaths, "Backend Developer", "Design APIs, databases, and the reliable systems behind products.");
            addCareerPath(careerPaths, "Mobile Developer", "Create useful Android and iOS experiences for people on the move.");
            addCareerPath(careerPaths, "Data Analyst", "Use data to find patterns and help teams make better decisions.");
            addCareerPath(careerPaths, "AI / Machine Learning", "Build intelligent products with data, models, and responsible evaluation.");
            addCareerPath(careerPaths, "Cybersecurity", "Protect systems, networks, and users through practical security work.");
            addCareerPath(careerPaths, "DevOps / Cloud", "Build reliable delivery pipelines and cloud infrastructure for modern software.");
            addCareerPath(careerPaths, "Full Stack Developer", "Build complete products from user interface to database and deployment.");

            addQuestion(questions, "build", "Which kind of work sounds most exciting to you?");
            addQuestion(questions, "project", "Which first portfolio project would you be proud to share?");
            addQuestion(questions, "strength", "Which strength feels most like you right now?");

            seedCareerPlan(roadmapSteps, checklistItems, learningResources, "Frontend Developer", new String[][]{
                {"HTML and semantic structure", "Build accessible pages with meaningful HTML."}, {"CSS and responsive layouts", "Create layouts that work across screen sizes."},
                {"JavaScript fundamentals", "Use functions, arrays, objects, events, and the DOM."}, {"TypeScript", "Add safe types and model application data."},
                {"Angular foundations", "Build components, routes, forms, and HTTP services."}, {"Testing and accessibility", "Test important behavior and support keyboard and screen reader users."},
                {"Portfolio project", "Ship a polished web application and explain your decisions."}});
            seedCareerPlan(roadmapSteps, checklistItems, learningResources, "Backend Developer", new String[][]{
                {"Java foundations", "Master classes, collections, exceptions, and clean code."}, {"SQL and data modeling", "Design relational schemas and write reliable queries."},
                {"Spring Boot APIs", "Build validated REST endpoints with clear DTOs."}, {"Authentication and security", "Protect data with JWT, authorization, and safe passwords."},
                {"Testing", "Cover services and APIs with repeatable tests."}, {"Deployment", "Package and deploy an API with PostgreSQL."},
                {"Portfolio project", "Ship a documented backend serving a real use case."}});
            seedCareerPlan(roadmapSteps, checklistItems, learningResources, "Mobile Developer", new String[][]{
                {"Programming foundations", "Build confidence with variables, functions, and state."}, {"Mobile UI", "Create responsive screens and navigation."},
                {"Local data", "Store settings and offline application data."}, {"APIs and networking", "Connect the app to authenticated web services."},
                {"Device capabilities", "Use notifications, media, and permissions responsibly."}, {"Testing and release", "Test on devices and prepare a production build."},
                {"Portfolio app", "Publish a useful app with a clear case study."}});
            seedCareerPlan(roadmapSteps, checklistItems, learningResources, "Data Analyst", new String[][]{
                {"Spreadsheet foundations", "Clean, calculate, and summarize structured data."}, {"SQL", "Query, join, group, and validate relational data."},
                {"Statistics", "Use descriptive statistics and avoid common interpretation errors."}, {"Python for analysis", "Explore data with pandas and reproducible notebooks."},
                {"Data visualization", "Choose charts that make the conclusion easy to understand."}, {"Business communication", "Turn analysis into a decision-focused story."},
                {"Portfolio analysis", "Publish a documented analysis using a real dataset."}});
            seedCareerPlan(roadmapSteps, checklistItems, learningResources, "AI / Machine Learning", new String[][]{
                {"Python and data foundations", "Work confidently with Python, data structures, and notebooks."}, {"Math for machine learning", "Understand the practical intuition behind probability, statistics, and linear algebra."},
                {"Data preparation", "Clean, inspect, and split datasets without leaking information."}, {"Machine learning models", "Train and compare baseline models for a clear problem."},
                {"Evaluation and ethics", "Measure performance, errors, bias, and safe use cases."}, {"Model deployment", "Expose a useful model through an API or simple interface."},
                {"Portfolio ML project", "Publish an end-to-end case study with a dataset and evaluation."}});
            seedCareerPlan(roadmapSteps, checklistItems, learningResources, "Cybersecurity", new String[][]{
                {"Networking foundations", "Understand TCP/IP, DNS, HTTP, ports, and network traffic."}, {"Linux and command line", "Navigate systems, permissions, processes, and logs."},
                {"Security fundamentals", "Learn threats, vulnerabilities, authentication, and risk."}, {"Web security", "Practice common web vulnerabilities in legal training labs."},
                {"Security tools", "Use scanning, analysis, and monitoring tools responsibly."}, {"Incident and defense basics", "Read logs and document a practical defensive response."},
                {"Security portfolio project", "Publish a legal lab write-up or defensive security case study."}});
            seedCareerPlan(roadmapSteps, checklistItems, learningResources, "DevOps / Cloud", new String[][]{
                {"Linux and networking", "Use Linux, processes, permissions, HTTP, and DNS confidently."}, {"Git and collaboration", "Use branches, pull requests, and clean release history."},
                {"Containers", "Package applications with Docker and compose local services."}, {"CI/CD", "Automate tests and deployment checks in a pipeline."},
                {"Cloud foundations", "Understand compute, storage, networking, IAM, and cost control."}, {"Infrastructure as code", "Describe repeatable infrastructure safely."},
                {"Cloud deployment project", "Deploy and document a monitored application with a pipeline."}});
            seedCareerPlan(roadmapSteps, checklistItems, learningResources, "Full Stack Developer", new String[][]{
                {"Web foundations", "Build semantic, responsive pages with HTML, CSS, and JavaScript."}, {"Frontend application", "Build components, forms, state, and accessible interactions."},
                {"Backend API", "Design validated endpoints and business logic."}, {"Data and authentication", "Model data, use SQL, and protect user accounts."}, {"Testing", "Test core behavior from API to interface."}, {"Deployment", "Deploy the frontend, backend, and database."},
                {"Full stack portfolio project", "Ship a product with users, data, and a public case study."}});
        };
    }

    private void seedCareerPlan(RoadmapStepRepository steps, ChecklistItemRepository items, LearningResourceRepository resources, String career, String[][] plan) {
        for (int index = 0; index < plan.length; index++) {
            int order = index + 1;
            if (!steps.existsByCareerNameAndOrderIndex(career, order)) steps.save(new RoadmapStep(career, plan[index][0], plan[index][1], order));
        }
        for (RoadmapStep step : steps.findByCareerNameOrderByOrderIndex(career)) {
            if (!resources.existsByRoadmapStepIdAndDisplayOrder(step.getId(), 1)) {
                String query = (career + " " + step.getTitle() + " tutorial").replace(" ", "+");
                resources.save(new LearningResource(step, step.getTitle() + " learning playlist", "YouTube", "playlist", "https://www.youtube.com/results?search_query=" + query, "English", "free", "beginner", "1-3 hours", "Start with a practical video lesson, then complete the roadmap deliverable.", 1));
            }
            if (!resources.existsByRoadmapStepIdAndDisplayOrder(step.getId(), 2)) {
                resources.save(new LearningResource(step, step.getTitle() + " official documentation", documentationProvider(career), "documentation", documentationUrl(career), "English", "free", "beginner", "30-90 minutes", "Use the official reference while building the step deliverable. Search within it for the concepts in this roadmap step.", 2));
            }
            if (!resources.existsByRoadmapStepIdAndDisplayOrder(step.getId(), 3)) {
                resources.save(new LearningResource(step, "Project inspiration for " + step.getTitle(), "GitHub", "project", githubTopic(career), "English", "free", "beginner", "2-6 hours", "Browse real projects for inspiration. Build your own version and explain your decisions instead of copying a repository.", 3));
            } else {
                resources.findByRoadmapStepIdAndDisplayOrder(step.getId(), 3).ifPresent(resource -> {
                    resource.refresh("Project inspiration for " + step.getTitle(), "GitHub", "project", githubTopic(career), "English", "free", "beginner", "2-6 hours", "Browse real projects for inspiration. Build your own version and explain your decisions instead of copying a repository.");
                    resources.save(resource);
                });
            }
        }
        String[][] checklist = {{"Complete your profile", "Add your education, links, and target career."}, {"Create a focused GitHub profile", "Pin your strongest repositories and add useful README files."},
            {"Publish two relevant projects", "Show complete work related to your target role."}, {"Prepare a one-page CV", "Describe outcomes, skills, and evidence clearly."},
            {"Improve your LinkedIn profile", "Use a clear headline, about section, and project links."}, {"Practice an interview story", "Explain one project, a challenge, and what you learned."},
            {"Apply with a tracking routine", "Track tailored applications and follow-ups every week."}};
        for(int index=0;index<checklist.length;index++){int order=index+1;if(!items.existsByCareerNameAndOrderIndex(career,order))items.save(new ChecklistItem(career,checklist[index][0],checklist[index][1],order));}
    }

    private String documentationProvider(String career) { return switch (career) { case "Frontend Developer", "Full Stack Developer" -> "MDN Web Docs"; case "Backend Developer" -> "Spring"; case "Mobile Developer" -> "Android Developers"; case "Data Analyst" -> "Python Docs"; case "AI / Machine Learning" -> "scikit-learn"; case "Cybersecurity" -> "OWASP"; case "DevOps / Cloud" -> "Docker Docs"; default -> "Official docs"; }; }
    private String documentationUrl(String career) { return switch (career) { case "Frontend Developer", "Full Stack Developer" -> "https://developer.mozilla.org/en-US/docs/Learn"; case "Backend Developer" -> "https://docs.spring.io/spring-boot/index.html"; case "Mobile Developer" -> "https://developer.android.com/docs"; case "Data Analyst" -> "https://docs.python.org/3/"; case "AI / Machine Learning" -> "https://scikit-learn.org/stable/"; case "Cybersecurity" -> "https://owasp.org/www-project-web-security-testing-guide/"; case "DevOps / Cloud" -> "https://docs.docker.com/"; default -> "https://developer.mozilla.org/"; }; }
    private String githubTopic(String career) { return switch (career) { case "Frontend Developer" -> "https://github.com/topics/frontend"; case "Backend Developer" -> "https://github.com/topics/spring-boot"; case "Mobile Developer" -> "https://github.com/topics/android"; case "Data Analyst" -> "https://github.com/topics/data-analysis"; case "AI / Machine Learning" -> "https://github.com/topics/machine-learning"; case "Cybersecurity" -> "https://github.com/topics/cybersecurity"; case "DevOps / Cloud" -> "https://github.com/topics/devops"; case "Full Stack Developer" -> "https://github.com/topics/full-stack"; default -> "https://github.com/topics"; }; }

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
