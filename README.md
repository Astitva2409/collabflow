# CollabFlow

CollabFlow is a full-stack project collaboration and Kanban management platform built with **Spring Boot** and **React**.

The project is designed as a resume-level full-stack application demonstrating authentication, authorization, workspace-based collaboration, project management, Kanban board structure, clean REST API design, database migrations, Docker-based local development, and scalable backend architecture.

---

## Table of Contents

- [Tech Stack](#tech-stack)
- [Current Backend Features](#current-backend-features)
- [Project Structure](#project-structure)
- [Backend API Modules](#backend-api-modules)
- [Local Development Setup](#local-development-setup)
- [Database Migrations](#database-migrations)
- [Entity Relationship Overview](#entity-relationship-overview)
- [Planned Features](#planned-features)
- [Deployment Plan](#deployment-plan)
- [Learning Goals Demonstrated](#learning-goals-demonstrated)
- [Author](#author)

---

## Tech Stack

### Backend

- Java 21
- Spring Boot 3
- Spring Security
- JWT Authentication
- Spring Data JPA
- Hibernate
- PostgreSQL
- Flyway Database Migrations
- Swagger / OpenAPI
- Docker

### Frontend

- React
- TypeScript
- Vite
- Tailwind CSS
- React Router
- Axios
- TanStack Query
- Zustand

### Tools & DevOps

- Git
- GitHub
- Docker Compose
- DBeaver
- Postman
- Render / Vercel / Neon planned for deployment

---

## Current Backend Features

### Authentication & Security

- User registration
- User login
- BCrypt password hashing
- JWT access token generation
- JWT validation using custom authentication filter
- Protected routes using Spring Security
- Current logged-in user API
- Custom `UserDetails`
- Custom `UserDetailsService`
- Stateless authentication using JWT
- Clean JSON responses for:
    - `401 Unauthorized`
    - `403 Forbidden`
- Role-based application security foundation

### Common Backend Foundation

- Standard API response wrapper
- Global exception handling
- Custom exceptions
- Invalid UUID handling
- Request validation error handling
- Swagger/OpenAPI documentation
- Dockerized PostgreSQL setup
- Flyway-based database schema versioning

### Workspace Module

- Create workspace
- Get logged-in user's workspaces
- Get workspace by ID
- Workspace membership model
- Workspace roles:
    - `OWNER`
    - `ADMIN`
    - `MEMBER`
    - `VIEWER`
- Add workspace member
- List workspace members
- Update member role
- Remove workspace member
- Centralized workspace permission checks using `WorkspaceAccessService`

### Project Module

- Create project inside workspace
- List active projects inside workspace
- Get project by ID
- Update project
- Archive project using soft delete
- Duplicate project name validation
- Workspace-based project authorization
- `VIEWER` role restricted from project management actions

### Board Module

- Auto-create default board when a project is created
- Auto-create default Kanban columns:
    - `TODO`
    - `IN_PROGRESS`
    - `REVIEW`
    - `DONE`
- Get board with ordered columns
- One project currently has one default board

---

## Project Structure

```text
collabflow/
│
├── collabflow-backend/
│   ├── src/main/java/com/astitva/collabflow/
│   │   ├── auth/
│   │   ├── user/
│   │   ├── workspace/
│   │   ├── project/
│   │   ├── board/
│   │   └── common/
│   │
│   ├── src/main/resources/
│   │   ├── application.yml
│   │   └── db/migration/
│   │
│   ├── pom.xml
│   ├── mvnw
│   └── mvnw.cmd
│
├── collabflow-frontend/
│   ├── src/
│   ├── package.json
│   └── vite.config.ts
│
├── docker-compose.yml
├── README.md
├── .gitignore
└── docs/