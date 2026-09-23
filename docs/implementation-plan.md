# Beta implementation plan

Status: Job-readiness expansion implemented and ready for full manual testing.

## Foundation

- [x] Angular application shell and responsive design system
- [x] Spring Boot API project and health endpoint
- [x] Local development documentation

## First vertical slice

1. [x] Student registration, login, and logout
2. [x] Student profile with target career selection
3. [x] Dashboard backed by profile, roadmap, project, and checklist data

## MVP features

1. [x] Career assessment, saved answers, and recommendations
2. [x] Career roadmaps with progress tracking
3. [x] Project CRUD and publishable public portfolio
4. [x] Career preparation checklist with progress
5. [x] Freelancing guide and individual articles

## Verification gates

- [x] Backend integration journey runs against an isolated H2 database
- [x] Angular production build completes
- [x] Browser journey verified for login, profile, roadmap, projects, public portfolio, checklist, and guide
- [x] Responsive layout for desktop and mobile breakpoints

## Out of scope for beta

- Marketplace and job board
- Community and mentorship platform
- Advanced AI career analysis
- Native mobile application

## Next product phase: guided execution

### Implemented job-readiness foundation

- [x] Expanded career catalog: AI / Machine Learning, Cybersecurity, DevOps / Cloud, and Full Stack Developer.
- [x] Expanded assessment choices and recommendations for all supported paths.
- [x] Seeded a seven-step roadmap and practical learning resource for every supported path.
- [x] Added a readiness score based on automatically verified profile, assessment, GitHub, LinkedIn, projects, portfolio, roadmap, and checklist evidence.
- [x] Added a student-owned opportunity workspace with a transparent requirements-fit evaluation.
- [x] Added an application tracker with saved, preparing, applied, interview, offer, rejected, and withdrawn states.

### Smart progress tracking

- [x] Add automatic evidence records and completion source for verified job-readiness milestones.
- Add completion states: not started, in progress, automatic, manual, and needs review.
- Extend the rule engine with explicit roadmap/checklist completion states and manual review.
- Extend automatic verification to project checkpoints and external integrations.

### Roadmap learning resources

- [x] Add an initial free learning resource to every roadmap step.
- Store language, cost, difficulty, duration, provider, verification state, and last link check.
- Add curated video, documentation, exercise, and mini-project resources for every step.
- Create admin management and student broken-link reporting.

### Job execution

- [x] Save student-owned Job and Internship opportunities with listing links and requirements.
- [x] Generate live LinkedIn search links for internships and entry-level roles based on the student's target career in Egypt.
- [x] Compare the student’s projects and target career to listed requirements.
- [x] Track application status, application date, follow-up date, notes, and outcome.
- [x] Record every application status change as an application event.

### First-client freelancing system

- Freelance readiness assessment and personalized gap plan.
- Focused service and target-customer builder.
- Portfolio proof checks for the selected service.
- Offer builder covering scope, deliverables, timeline, revisions, price, and boundaries.
- Daily opportunity discovery routine and search terms.
- Opportunity evaluator with fit, clarity, budget, risk, and credibility scoring.
- Proposal workspace based on the real opportunity and the student's real evidence.
- Communication simulations for discovery, negotiation, revisions, delays, and difficult clients.
- Client acquisition pipeline from opportunity found through won or lost.
- Follow-up reminders, outcome review, and response-rate metrics.
- First-project delivery checklists, milestones, updates, handoff, payment safety, and review request.

Detailed product behavior, data requirements, success metrics, and priority order are defined in `CS_Career_Platform_Master_Context.md` sections 21–24.
