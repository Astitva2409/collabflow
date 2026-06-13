# CollabFlow

CollabFlow is a full-stack project collaboration and Kanban management platform built with **Spring Boot** and **React**.

The project is designed as a resume-level full-stack application demonstrating authentication, authorization, workspace-based collaboration, member management, project management, Kanban board workflows, task management, clean REST API design, database migrations, Docker-based local development, and scalable backend architecture.

---

## Table of Contents

- [Tech Stack](#tech-stack)
- [Current Backend Features](#current-backend-features)
- [Project Structure](#project-structure)
- [Backend API Modules](#backend-api-modules)
- [Local Development Setup](#local-development-setup)
- [Run PostgreSQL with Docker](#run-postgresql-with-docker)
- [Run Backend](#run-backend)
- [Run Frontend](#run-frontend)
- [Database Migrations](#database-migrations)
- [Entity Relationship Overview](#entity-relationship-overview)
- [Authentication Flow](#authentication-flow)
- [Authorization Model](#authorization-model)
- [Task Workflow Design](#task-workflow-design)
- [Local Database Access Using DBeaver](#local-database-access-using-dbeaver)
- [Planned Features](#planned-features)
- [Deployment Plan](#deployment-plan)
- [Learning Goals Demonstrated](#learning-goals-demonstrated)
- [Current Status](#current-status)
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

### Task Module

- Create task inside project
- Create task in default `TODO` column if column is not provided
- Create task in a specific board column if `boardColumnId` is provided
- Fetch all active tasks for a project
- Fetch task by ID
- Update task details:
    - title
    - description
    - priority
    - due date
- Move task between Kanban columns
- Reorder task within the same column
- Assign task to a workspace member
- Unassign task
- Archive task using soft delete
- Normalize task positions after move/archive operations
- Task status is derived from `board_column_id`
- One assignee per task currently supported
- `VIEWER` role restricted from task management actions

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
│   │   ├── task/
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
```

---

## Backend API Modules

### Auth APIs

```http
POST /api/v1/auth/register
POST /api/v1/auth/login
GET  /api/v1/users/me
```

### Workspace APIs

```http
POST /api/v1/workspaces
GET  /api/v1/workspaces
GET  /api/v1/workspaces/{workspaceId}
```

### Workspace Member APIs

```http
POST   /api/v1/workspaces/{workspaceId}/members
GET    /api/v1/workspaces/{workspaceId}/members
PATCH  /api/v1/workspaces/{workspaceId}/members/{userId}/role
DELETE /api/v1/workspaces/{workspaceId}/members/{userId}
```

### Project APIs

```http
POST   /api/v1/workspaces/{workspaceId}/projects
GET    /api/v1/workspaces/{workspaceId}/projects
GET    /api/v1/workspaces/{workspaceId}/projects/{projectId}
PATCH  /api/v1/workspaces/{workspaceId}/projects/{projectId}
DELETE /api/v1/workspaces/{workspaceId}/projects/{projectId}
```

### Board APIs

```http
GET /api/v1/workspaces/{workspaceId}/projects/{projectId}/board
```

### Task APIs

```http
POST   /api/v1/workspaces/{workspaceId}/projects/{projectId}/tasks
GET    /api/v1/workspaces/{workspaceId}/projects/{projectId}/tasks
GET    /api/v1/workspaces/{workspaceId}/projects/{projectId}/tasks/{taskId}
PATCH  /api/v1/workspaces/{workspaceId}/projects/{projectId}/tasks/{taskId}
PATCH  /api/v1/workspaces/{workspaceId}/projects/{projectId}/tasks/{taskId}/move
PATCH  /api/v1/workspaces/{workspaceId}/projects/{projectId}/tasks/{taskId}/assign
DELETE /api/v1/workspaces/{workspaceId}/projects/{projectId}/tasks/{taskId}
```

---

## Local Development Setup

### Prerequisites

Make sure the following are installed:

```text
Java 21+
Maven
Node.js 20+
Docker Desktop
Git
Postman / Bruno / Insomnia
DBeaver
```

---

## Run PostgreSQL with Docker

From the root folder:

```bash
docker compose up -d
```

PostgreSQL will run on:

```text
Host: localhost
Port: 5433
Database: collabflow
Username: postgres
Password: postgres
```

To verify the container is running:

```bash
docker ps
```

Expected container:

```text
collabflow-postgres
```

---

## Run Backend

From the root folder:

```bash
cd collabflow-backend
./mvnw spring-boot:run
```

Backend runs on:

```text
http://localhost:8080
```

Swagger UI:

```text
http://localhost:8080/swagger-ui/index.html
```

---

## Run Frontend

From the root folder:

```bash
cd collabflow-frontend
npm install
npm run dev
```

Frontend runs on:

```text
http://localhost:5173
```

---

## Database Migrations

Database schema is managed using Flyway.

Current migrations include:

```text
V1__init.sql
V2__add_user_profile_fields.sql
V3__create_workspace_tables.sql
V4__create_projects_table.sql
V5__create_board_tables.sql
V6__create_tasks_table.sql
```

Flyway migration history can be checked in the database table:

```text
flyway_schema_history
```

---

## Entity Relationship Overview

Current relationship structure:

```text
User
  └── WorkspaceMember
        └── Workspace
              └── Project
                    └── Board
                          └── BoardColumn
                                └── Task
```

Upcoming relationship structure:

```text
Task
  └── Comment

Task
  └── ActivityLog

Task
  └── Attachment
```

---

## Authentication Flow

```text
User registers
      ↓
Password is hashed using BCrypt
      ↓
User logs in
      ↓
JWT access token is generated
      ↓
Client sends token in Authorization header
      ↓
JwtAuthenticationFilter validates token
      ↓
SecurityContextHolder stores authenticated user
      ↓
Protected APIs are accessed
```

Example authorization header:

```http
Authorization: Bearer <access-token>
```

---

## Authorization Model

CollabFlow currently supports workspace-level authorization.

Workspace roles:

```text
OWNER
ADMIN
MEMBER
VIEWER
```

### Current Permission Rules

```text
OWNER  -> Full workspace control
ADMIN  -> Can manage workspace members and workspace resources
MEMBER -> Can manage projects and tasks
VIEWER -> Read-only access
```

### Module-Level Access Rules

```text
Workspace viewing:
- Any workspace member can view workspace details.

Workspace member management:
- OWNER and ADMIN can add members.
- OWNER can update roles and remove members.
- OWNER cannot remove self.
- OWNER role cannot be assigned through member APIs.

Project management:
- OWNER, ADMIN, and MEMBER can create/update/archive projects.
- VIEWER can only view projects.

Board access:
- Any workspace member can view board and columns.

Task management:
- OWNER, ADMIN, and MEMBER can create/update/move/assign/archive tasks.
- VIEWER can only view tasks.
```

---

## Task Workflow Design

Task status is not stored as a separate status column.

Instead, task workflow state is derived from the current board column:

```text
Task in TODO column        -> TODO
Task in IN_PROGRESS column -> IN_PROGRESS
Task in REVIEW column      -> REVIEW
Task in DONE column        -> DONE
```

This avoids inconsistent data such as:

```text
task.status = DONE
task.column = TODO
```

Task ordering is handled using the `position` field.

Example:

```text
TODO column:
1. Design Task Module
2. Implement Task APIs
3. Test Task Assignment
```

When a task is moved or archived, positions are normalized so active tasks remain ordered as:

```text
1, 2, 3, ... N
```

---

## Local Database Access Using DBeaver

Create a PostgreSQL connection in DBeaver using:

```text
Host: localhost
Port: 5433
Database: collabflow
Username: postgres
Password: postgres
```

Useful queries:

```sql
SELECT * FROM users;

SELECT * FROM workspaces;

SELECT * FROM workspace_members;

SELECT * FROM projects;

SELECT * FROM boards;

SELECT * FROM board_columns ORDER BY board_id, position;

SELECT * FROM tasks;
```

Task verification query:

```sql
SELECT 
    t.id,
    t.title,
    bc.name AS column_name,
    t.priority,
    t.position,
    creator.email AS created_by,
    assignee.email AS assigned_to,
    t.due_date,
    t.archived
FROM tasks t
JOIN board_columns bc ON t.board_column_id = bc.id
JOIN users creator ON t.created_by = creator.id
LEFT JOIN users assignee ON t.assigned_to = assignee.id
ORDER BY bc.position, t.position;
```

---

## Planned Features

Upcoming backend features:

- Task comments
- Activity logs
- Notifications
- File attachments
- Dashboard analytics
- Real-time updates using WebSockets
- Redis caching
- Unit and integration tests
- Dockerized backend and frontend
- Cloud deployment using Render, Vercel, and Neon PostgreSQL

Upcoming frontend features:

- Login page
- Register page
- Workspace dashboard
- Project dashboard
- Kanban board UI
- Task creation modal
- Task detail drawer
- Member management UI
- Responsive layout
- Protected routes
- Role-based UI actions

---

## Deployment Plan

Planned deployment architecture:

```text
Frontend  -> Vercel
Backend   -> Render
Database  -> Neon PostgreSQL
```

Future deployment improvements:

```text
Dockerfile for backend
Dockerfile for frontend
Environment-based configuration
CI/CD pipeline
Production profile
Cloud-hosted database
```

---

## Learning Goals Demonstrated

This project demonstrates:

- Spring Boot REST API development
- Spring Security with JWT
- Authentication and authorization
- Role-based access control
- Workspace-based permission design
- JPA/Hibernate relationships
- Join entity design
- Flyway database migrations
- Transaction management
- Global exception handling
- Docker-based local development
- Clean layered architecture
- Modular monorepo structure
- Real-world backend design patterns
- Production-style API response design
- Kanban workflow modeling
- Task movement and position normalization
- Soft delete/archive pattern

---

## Current Status

```text
Backend foundation: Completed
Authentication module: Completed
Workspace module: Completed
Workspace member management: Completed
Project module: Completed
Board module: Completed
Task module: Completed
Task comments module: Upcoming
Frontend implementation: Upcoming
Deployment: Planned
```

---

## Author

**Astitva Singh**

- GitHub: https://github.com/Astitva2409
- LinkedIn: https://www.linkedin.com/in/astitva-singh/