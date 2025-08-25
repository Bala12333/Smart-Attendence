## Smart Attendance System — Project Overview

### Structure
- `backend/` — Spring Boot REST API with JWT auth and H2 database
- `frontend/` — Installable PWA (HTML + Tailwind + Service Worker)
- `PROJECT_OVERVIEW.md` — This document
- `STYLE_GUIDE.md` — UI palette, typography, component guidance
- `README.md` — Setup and run instructions

### Roles (Agents)
- Planning Agent: Defines structure, scope, and delivery milestones.
- UI/UX Design Agent: Delivers high-fidelity lecturer dashboard mockup and style guide.
- Backend Developer Agent: Implements Spring Boot API, models, repositories, services, security, and seed data.
- Frontend Developer Agent: Builds PWA shell, role-based views, API integration, offline caching.
- Documentation Agent: Produces README with clear setup and usage.

### High-Level Architecture
- Client (PWA) communicates with the API via JWT-secured endpoints under `/api/*`.
- H2 in-memory database persists demo data; replaceable with a production RDBMS.
- Service Worker caches app shell and last-viewed data for offline access.
- Optional integrations (e.g., facial recognition) are stubbed for later provider wiring.

### API Domains
- Auth: register/login → JWT
- Courses: CRUD and listing
- Attendance: QR/scan/face/manual
- Reports: student and course views

### Non-Functional
- PWA Lighthouse target 90+.
- HTTPS recommended in production; localhost for development.

