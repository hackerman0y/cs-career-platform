# Beta implementation plan

Status: MVP implemented and ready for full manual testing.

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

### Smart progress tracking

- Add evidence and completion-source fields to roadmap and checklist progress.
- Add completion states: not started, in progress, automatic, manual, and needs review.
- Build a rule engine triggered by profile, assessment, project, portfolio, learning, and integration events.
- Automatically verify the first rules: complete profile, assessment result, GitHub link, qualifying project, published portfolio, and two qualifying projects.
- Show the student why an item is complete and what evidence is still missing.

### Roadmap learning resources

- Add learning resources to every roadmap step: videos, playlists, documentation, articles, courses, exercises, and projects.
- Store language, cost, difficulty, duration, provider, verification state, and last link check.
- Add learning outcomes, estimated time, practice, and an evidence-producing checkpoint to every step.
- Create admin management and student broken-link reporting.

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
