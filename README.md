# Teacher Management — Backend Service

A multi-tenant REST API backend for managing teachers, departments, roles, and access control in educational institutions. Built with Spring Boot and designed with a microservice-ready architecture.

---

## Introduction

This service provides the core business logic and API layer for the Teacher Management system. It supports:

- **Multi-tenant architecture** — multiple institutions (tenants) share a single deployment with isolated data.
- **Role-Based Access Control (RBAC)** — fine-grained permissions through roles assigned per-tenant context.
- **Authentication & Authorization** — secured endpoints via Spring Security.
- **User & Career Management** — tracks teacher profiles, career ranks, and department assignments.
- **Microservice-ready** — registered with Netflix Eureka for service discovery.

### Package Structure

```
src/main/java/com/teachermanagement/teacher_management/
├── TeacherManagementApplication.java   # Application entry point
├── auth/                               # Authentication & authorization (controller, dto, entity, mapper, repository, service)
├── common/                             # Shared utilities (constants, exceptions, response wrappers, utils)
├── config/                             # Spring configuration classes
├── security/                           # Security filters and configurations
└── user/                               # User management (controller, dto, entity, mapper, repository, service)
```

---

## Project Information

| Property         | Value                   |
| ---------------- | ----------------------- |
| **Artifact**     | `teacher-management`    |
| **Group**        | `com.teachermanagement` |
| **Version**      | `0.0.1-SNAPSHOT`        |
| **Java**         | 17                      |
| **Spring Boot**  | 4.0.6                   |
| **Spring Cloud** | 2025.1.1                |
| **Build Tool**   | Maven 3.8+              |
| **Default Port** | `8080`                  |

### Key Dependencies

- `spring-boot-starter-webmvc` — REST API layer
- `spring-boot-starter-data-jpa` — ORM / database access
- `spring-boot-starter-security` — authentication & authorization
- `spring-boot-starter-validation` — request validation
- `spring-boot-starter-actuator` — health & metrics endpoints
- `spring-cloud-starter-netflix-eureka-client` — service discovery
- `postgresql` — production database driver
- `lombok` — boilerplate reduction

---

## How to Set Up

### Prerequisites

| Tool                            | Version                      |
| ------------------------------- | ---------------------------- |
| Java (JDK)                      | 17 or later                  |
| Maven                           | 3.8 or later                 |
| Git                             | any recent version           |
| PostgreSQL                      | 14+ (production only)        |
| Docker _(optional)_             | for containerized PostgreSQL |
| Postman / Insomnia _(optional)_ | API testing                  |

### 1. Clone the repository

```bash
git clone <repository-url>
cd Teacher_Management/Sources/BE/teacher-management
```
