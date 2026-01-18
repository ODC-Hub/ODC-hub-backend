# ODC Hub Backend
### Bootcamp Management Platform — Backend Service

Backend service for a centralized bootcamp management platform, designed to structure operations, manage users and roles, and support secure, scalable workflows for training programs.

---

## Overview
ODC Hub centralizes core bootcamp operations by providing secure APIs for user management, authentication, auditing, and administrative workflows, following clean architecture and production-ready backend practices.

---

## Core Features
- User and role management (admin / user scopes)
- Secure authentication and authorization
- Administrative APIs for user supervision
- Audit logging for sensitive operations
- Centralized email service integration

---

## Business Value
- Structures bootcamp operations in a single platform
- Improves organization and administrative control
- Enhances traceability and accountability through auditing
- Designed to scale with multiple programs and users

---

## Technical Highlights
- Modular package organization by domain (auth, user, admin, audit)
- Secure access control with Spring Security
- DTO and mapper-based data handling
- Centralized cross-cutting services (email, security, configuration)
- Environment-based configuration using properties and YAML

---

## Tech Stack
- **Backend**: Java 17, Spring Boot
- **Security**: Spring Security
- **Database**: MongoDB
- **Testing**: JUnit, Testcontainers
- **DevOps**: Docker, GitHub Actions
- **Configuration**: application.properties, application.yml

---

## Project Structure
```text
src/main/java/com/odc/hub
├── admin
│   └── controller
├── audit
│   ├── dto
│   ├── mapper
│   ├── model
│   ├── repository
│   └── service
├── auth
│   ├── controller
│   ├── dto
│   ├── security
│   └── service
├── common
│   └── service
├── config
├── user
│   ├── controller
│   ├── dto
│   ├── mapper
│   ├── model
│   ├── repository
│   └── service
└── OdcHubBackendApplication.java
```

## Documentation
- Detailed backend documentation is available in **DEVELOPER_GUIDE.md**

## Running Locally
```bash
mvn clean install
mvn spring-boot:run
```