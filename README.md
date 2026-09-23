# CS Career Platform

The CS Career Platform helps technology students choose a direction, follow a practical roadmap, build projects, and prepare to enter the job market.

## Project structure

- `frontend/` — Angular web application
- `backend/` — Spring Boot REST API
- `docs/` — product and technical notes

## Current beta

The MVP now includes JWT authentication, student profiles, career assessment, career-specific roadmaps with progress, project management, public portfolios, a career checklist, and a freelancing guide. Student data is persisted through the Spring Boot API.

## Run locally

### Frontend

```powershell
cd frontend
npm install
npm start
```

Open `http://localhost:4200`.

### Backend

```powershell
cd backend
mvn spring-boot:run
```

The API health check is available at `http://localhost:8080/api/health`.

Keep the backend terminal open while using the frontend. Only run one backend instance because the local H2 database accepts one process.

## Verification

```powershell
cd backend
mvn test

cd ..\frontend
npm run build
```

The backend test uses an isolated in-memory database and covers registration, profile setup, roadmap progress, project creation, portfolio publishing, and public portfolio access.
