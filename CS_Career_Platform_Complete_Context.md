# CS Career Platform — Complete Product and Development Context

Version: 2.1  
Last updated: September 23, 2026  
Current status: Functional local MVP, ready for structured full testing  
Repository: https://github.com/hackerman0y/cs-career-platform

## 1. Product vision

CareerPath helps computer science and technology students move from uncertainty to execution:

> I know which career path to explore, what to learn, what to build, how to prove my skills, and what to do next to enter the market.

The product is a career execution platform. It connects assessment, learning, projects, public proof, job readiness, and freelancing into one measurable journey.

## 2. Target users

- Computer science and technology students.
- Students who are unsure which specialization fits them.
- Students who study but do not know what to build next.
- Students with weak GitHub, LinkedIn, CV, or portfolio presentation.
- Beginners who want to reach internships, junior roles, or a first freelance client.

## 3. Core journey

Account → Profile → Assessment → Career direction → Roadmap → Learning resources → Practice → Projects → Portfolio → Career checklist → Jobs or first freelance client.

## 4. Product principles

- Give the student a concrete next action.
- Recommendations guide the student without taking control away.
- Track measurable progress and the evidence behind it.
- Prefer real deliverables over content consumption.
- Never claim that a task was verified without evidence.
- Keep the product useful without depending on advanced AI.
- Build and validate the smallest useful version before expanding.

## 5. Current implemented MVP

### Authentication

- Student registration.
- Student login.
- Client and server logout flow.
- JWT authentication.
- BCrypt password hashing.
- Protected student endpoints.
- Public portfolio endpoint.

### Student profile

- Full name.
- University.
- Academic year.
- GitHub URL.
- LinkedIn URL.
- Target career.
- Profile data updates the dashboard and determines roadmap/checklist content.

### Career assessment

- Three practical questions.
- Four supported directions:
  - Frontend Developer
  - Backend Developer
  - Mobile Developer
  - Data Analyst
- Saved answers.
- Ranked recommendations.
- Saved latest result.
- Assessment can be retaken.
- A recommendation can update the student's target career.

### Career roadmaps

- A seeded seven-step roadmap for every supported career.
- Ordered practical steps and descriptions.
- Mark steps complete or incomplete.
- Persistent student progress.
- Progress percentage and next action on the dashboard.
- Eight supported paths: Frontend, Backend, Mobile, Data Analysis, AI / Machine Learning, Cybersecurity, DevOps / Cloud, and Full Stack Development.
- A free learning resource is seeded for each roadmap step.

### Job readiness and execution

- A readiness score is calculated from verified profile, assessment, GitHub, LinkedIn, projects, published portfolio, roadmap, and checklist evidence.
- Students can see the missing actions that improve their score and the automatic evidence already recognized.
- Students can save Job and Internship opportunities, compare requirements with their current proof, and track applications.
- Application states: saved, preparing, applied, interview, offer, rejected, and withdrawn.

### Projects

- Create a project.
- View the student's projects.
- Edit a project.
- Delete a project.
- Store name, description, technologies, GitHub URL, and live demo URL.
- Projects appear automatically in the student's portfolio.

### Public portfolio

- Unique public username.
- Custom headline.
- Custom bio.
- Publish/private control.
- Public student name and target career.
- GitHub and LinkedIn links.
- Project cards with source and demo links.
- Public URL format: `/p/{username}`.

### Career checklist

- Seven career-readiness items for every supported career.
- Complete or reopen each item.
- Persistent progress.
- Dashboard completion percentage.

### Freelancing guide — current foundation

- Freelancing foundations.
- Evaluating freelance jobs.
- Writing useful proposals.
- Client communication.
- Individual guide details and practical actions.

This guide is the initial content foundation. It will be replaced by the execution-focused first-client system defined later in this document.

### Dashboard

- Welcome using real profile data.
- Target career.
- Roadmap progress.
- Current next step.
- Project count.
- Career-checklist progress.
- Roadmap preview.
- Direct navigation to all student features.

### Responsive experience

- Desktop layout with persistent navigation.
- Mobile layout with compact navigation and single-column content.
- Responsive public portfolio.

## 6. Current technology stack

### Frontend

- Angular 20.
- TypeScript.
- Standalone components.
- Angular Router.
- Reactive Forms and template forms.
- HttpClient with JWT interceptor.
- Responsive CSS.

### Backend

- Java 25.
- Spring Boot 3.5.6.
- Spring Web.
- Spring Validation.
- Spring Security.
- JWT with JJWT.
- Spring Data JPA.
- Maven.

### Data

- H2 file database for local development.
- PostgreSQL driver and production profile.
- Hibernate schema updates for the current beta.
- Isolated in-memory H2 database for integration tests.

## 7. Current project structure

```text
cs Assistant/
├── backend/
│   ├── pom.xml
│   └── src/
│       ├── main/java/com/careerpath/
│       │   ├── assessment/
│       │   ├── auth/
│       │   ├── career/
│       │   ├── checklist/
│       │   ├── common/
│       │   ├── guide/
│       │   ├── portfolio/
│       │   ├── profile/
│       │   ├── project/
│       │   ├── roadmap/
│       │   └── security/
│       └── test/
├── frontend/
│   └── src/app/
│       ├── core/
│       └── features/
│           ├── assessment/
│           ├── auth/
│           ├── portfolio/
│           ├── profile/
│           └── workspace/
├── docs/
│   └── implementation-plan.md
└── README.md
```

## 8. Implemented REST API

Base URL: `http://localhost:8080/api`

### Health

- `GET /api/health`

### Authentication

- `POST /api/auth/register`
- `POST /api/auth/login`
- `POST /api/auth/logout`

### Profile

- `GET /api/profile`
- `PUT /api/profile`

### Career paths

- `GET /api/career-paths`
- `GET /api/career-paths/{id}`

### Assessment

- `GET /api/assessment/questions`
- `POST /api/assessment/submit`
- `GET /api/assessment/result`

### Roadmap

- `GET /api/roadmap`
- `PUT /api/roadmap/{stepId}`

### Projects

- `GET /api/projects`
- `POST /api/projects`
- `GET /api/projects/{projectId}`
- `PUT /api/projects/{projectId}`
- `DELETE /api/projects/{projectId}`

### Portfolio

- `GET /api/portfolio`
- `PUT /api/portfolio`
- `GET /api/portfolio/public/{username}`

### Checklist

- `GET /api/checklist`
- `PUT /api/checklist/{itemId}`

### Freelancing guide

- `GET /api/guides/freelancing`
- `GET /api/guides/freelancing/{slug}`

## 9. Current database entities

- `AppUser`
- `StudentProfile`
- `CareerPath`
- `AssessmentQuestion`
- `AssessmentAnswer`
- `RoadmapStep`
- `RoadmapProgress`
- `StudentProject`
- `PortfolioSettings`
- `ChecklistItem`
- `ChecklistProgress`

## 10. Security rules

- Passwords are stored only as BCrypt hashes.
- JWT protects student data.
- Project queries are scoped to the authenticated owner.
- Profile, progress, private portfolio settings, and checklist data require authentication.
- Published portfolios are public.
- Career paths and health checks are public.
- Development JWT defaults must be replaced by a strong environment secret in production.
- Production CORS must allow only the deployed frontend origin.

## 11. Verification completed

- Angular production build completed successfully.
- Spring Boot compilation completed successfully.
- Backend integration test passes.
- API journey verified for:
  - register and login
  - profile update
  - assessment submission
  - roadmap loading and completion
  - checklist loading and completion
  - project creation, update, list, and deletion
  - portfolio creation and publishing
  - public portfolio access
  - freelancing guide list and article
- Browser journey verified for:
  - login
  - profile setup
  - dashboard
  - roadmap progress
  - project creation
  - portfolio publishing
  - public portfolio rendering
  - career checklist
  - freelancing article interaction

## 12. Local setup

### Backend

```powershell
cd backend
mvn spring-boot:run
```

Backend URL: `http://localhost:8080`  
Health check: `http://localhost:8080/api/health`

Run only one backend instance because the local H2 file database is locked by the running process.

### Frontend

```powershell
cd frontend
npm install
npm start
```

Frontend URL: `http://localhost:4200`

### Verification commands

```powershell
cd backend
mvn test

cd ..\frontend
npm run build
```

## 13. Known work before production launch

- Add a public landing page.
- Add a public career-path discovery page.
- Add a settings page.
- Add the student's major to the profile.
- Replace target-career text with a database relationship.
- Introduce explicit `Roadmap` and `Checklist` container entities.
- Add Flyway or Liquibase migrations.
- Test with a real PostgreSQL instance.
- Move all production secrets to environment variables or a secret manager.
- Configure production CORS, HTTPS, logging, monitoring, backups, and rate limits.
- Add refresh-token or secure session-revocation strategy.
- Add broader validation, authorization, accessibility, mobile, and performance tests.
- Add deployment pipelines for frontend, backend, and PostgreSQL.
- Add a custom domain after launch validation.

## 14. Smart automatic progress tracking

The platform should determine progress automatically when it has reliable evidence, while preserving manual completion when automatic verification is unavailable.

### Completion states

- `not_started`
- `in_progress`
- `completed_automatically`
- `completed_manually`
- `needs_review`

Every completed item must show:

- Why it was completed.
- Which evidence was used.
- Whether it was automatic or manual.
- What is still missing if verification is incomplete.

### Initial automatic rules

1. Complete profile setup when all required fields are present.
2. Complete career assessment when a saved result exists.
3. Complete initial GitHub setup when a valid GitHub URL is present.
4. Complete first-project task when a project has a meaningful name, description, technologies, and repository or demo link.
5. Complete portfolio task when the public portfolio is published.
6. Complete two-project task when two qualifying projects exist.
7. Move relevant roadmap steps to `in_progress` when the student starts their exercises or deliverables.

### Future evidence sources

- Internal lessons, quizzes, exercises, and project submissions.
- Profile and assessment events.
- Project and portfolio events.
- GitHub integration: repository, commits, README, required files, and deployment.
- CV completion and section validation.
- Practical checkpoint submissions.

### Technical direction

Build a rule-based progress engine that listens to product events and evaluates rules linked to roadmap steps and checklist items. Store evidence records and completion source separately from the progress record.

## 15. Roadmap learning resources

Each roadmap step must become a complete learning action rather than only a topic name.

### LearningResource data model

- id
- roadmap_step_id
- title
- provider
- resource_type: video, playlist, article, documentation, course, exercise, or project
- url
- language: Arabic or English
- cost_type: free or paid
- difficulty
- estimated_duration
- description
- display_order
- is_verified
- last_checked_at

### Every roadmap step should contain

- Why the skill matters.
- Clear learning outcomes.
- Curated YouTube video or playlist.
- Official documentation or reliable article.
- Arabic resources when quality is sufficient.
- At least one practice exercise.
- A mini-project or practical deliverable.
- Estimated study time.
- A completion checkpoint that creates progress evidence.

### Resource quality

- Prioritize useful free resources.
- Clearly label language, price, difficulty, and duration.
- Allow students to report broken or poor resources.
- Give administrators tools to replace and reorder resources.
- Periodically recheck external links.

## 16. Freelancing First-Client System

The freelancing experience must guide the student to real execution and a first client. It should feel like a practical coach working beside the student, not a generic article collection or an AI chat that only gives broad advice.

### Stage 1: Freelance readiness assessment

- Evaluate skills, available time, communication, confidence, portfolio proof, and preferred work.
- Identify readiness gaps.
- Build a personalized preparation plan.
- Block premature applications when essential proof is missing and explain the next action.

### Stage 2: Choose a focused service

- Convert skills into one clear service.
- Choose a specific target customer.
- Define the business outcome.
- Avoid vague offers such as “I can build anything.”
- Example services:
  - Landing-page development.
  - API integration.
  - Business dashboard creation.
  - Data cleaning and reporting.
  - Website bug fixing.
  - Small-business portfolio websites.

### Stage 3: Build minimum proof

- Recommend one or two projects that prove the selected service.
- Require a useful case study.
- Check screenshots, repository, demo, result, technologies, and explanation.
- Connect the proof directly to the target customer's problem.

### Stage 4: Create the offer

- Scope.
- Deliverables.
- Timeline.
- Revision policy.
- Starter price or price range.
- Client requirements.
- Boundaries and exclusions.
- Reusable service brief.

### Stage 5: Find real opportunities

- Freelance platforms.
- Local businesses.
- University and alumni networks.
- Professional communities.
- Referrals.
- Direct outreach.
- Daily opportunity-finding routine.
- Search keywords based on the student's selected service.

### Stage 6: Evaluate every opportunity

Score and explain:

- Requirement clarity.
- Skill fit.
- Portfolio match.
- Budget quality.
- Timeline realism.
- Client credibility.
- Competition.
- Scam risk.
- Expected learning and commercial value.

Return one decision:

- Apply.
- Ask questions.
- Skip.
- Prepare more evidence first.

### Stage 7: Tailored proposal workspace

- Extract the client's actual problem.
- Select the student's relevant evidence.
- Explain the proposed approach.
- Set a realistic timeline.
- Include one useful discovery question.
- Detect unsupported claims, copied templates, vague promises, and unnecessary length.
- Keep the student responsible for reviewing and sending the final proposal.

### Stage 8: Client communication practice

Realistic simulations for:

- Discovery questions.
- Unclear requirements.
- Pricing objections.
- Scope negotiation.
- Revision requests.
- Delays.
- Difficult clients.
- Project cancellation.

The system should explain weaknesses in the student's response and show a better response with reasons.

### Stage 9: Client acquisition pipeline

Pipeline stages:

- opportunity_found
- evaluating
- proposal_drafted
- applied
- replied
- meeting
- negotiation
- won
- lost

Track:

- Opportunity source and link.
- Client and project notes.
- Fit score.
- Proposal version.
- Application date.
- Follow-up date.
- Current status.
- Win/loss reason.

### Stage 10: Deliver the first project safely

- Requirements checklist.
- Written scope.
- Milestones.
- Change-request handling.
- Progress-update templates.
- Handoff checklist.
- Payment-safety guidance.
- Client-review request.
- Professional or legal referral when contracts and taxes require jurisdiction-specific advice.

### Stage 11: Learn from every outcome

- Ask what happened after every proposal or interaction.
- Update the student's recommended next action.
- Track response rate and meeting rate.
- Identify repeated rejection reasons.
- Improve portfolio proof, service positioning, or proposal quality based on evidence.

### First-client dashboard questions

The dashboard must always answer:

- What should I do today?
- What is blocking me from applying?
- Which opportunity should I prioritize?
- Is my proposal ready to send?
- When should I follow up?
- What did I learn from the last result?

### Success metrics

- Students who define a focused service.
- Students who publish qualifying proof projects.
- Suitable opportunities evaluated.
- Tailored proposals sent.
- Proposal response rate.
- Meeting rate.
- Time to first client conversation.
- Time to first paid client.

## 17. Updated development priority

### Phase 1 — Production foundation

1. Complete the missing public and settings pages.
2. Align the database model and add migrations.
3. Configure and test PostgreSQL.
4. Expand automated tests and production security.
5. Add deployment and monitoring.

### Phase 2 — Smart progress

1. Add progress states and evidence entities.
2. Add the rule engine.
3. Implement automatic rules for existing features.
4. Display completion reasons and missing evidence.

### Phase 3 — Learning execution

1. Add the learning-resource data model and management API.
2. Curate resources for all current roadmap steps.
3. Add exercises, deliverables, and checkpoints.
4. Add broken-link reporting and admin management.

### Phase 4 — First-client system

1. Freelance readiness assessment.
2. Focused-service builder.
3. Portfolio-proof checks.
4. Opportunity evaluator.
5. Proposal workspace.
6. Communication simulations.
7. Client pipeline and follow-ups.
8. Delivery and outcome-review workflows.

### Phase 5 — Stronger integrations

1. GitHub evidence integration.
2. CV analysis and evidence.
3. Optional LinkedIn-related guidance where platform rules allow it.
4. Admin tools for roadmaps, rules, resources, and freelancing content.

## 18. Explicitly out of scope for the current version

- Freelance marketplace.
- Large social community.
- Company hiring portal.
- Advanced autonomous AI career decisions.
- Native Android or iOS application.
- Replacing GitHub, LinkedIn, Upwork, or learning platforms.

These may be evaluated after the core execution journey is validated with real students.

## 19. Repository and version control

GitHub repository:

https://github.com/hackerman0y/cs-career-platform

Default branch: `main`  
Initial MVP commit: `40bdbbc Build CareerPath MVP`

Ignored from Git:

- Local H2 database.
- Maven build output.
- Angular build output.
- `node_modules`.
- Local trust store.
- IDE and operating-system files.

## 20. Definition of success

The product succeeds when a student can:

1. Choose an informed career direction.
2. See a practical learning plan.
3. Find trustworthy resources without searching randomly.
4. Complete real work and have progress recognized with evidence.
5. Build projects relevant to the target role or service.
6. Present those projects professionally.
7. Become ready to apply for work.
8. Find, evaluate, and pursue suitable freelance opportunities.
9. Hold a real client conversation.
10. Win and deliver a first paid project safely.

The platform should measure each stage and always give the student the clearest next action.
