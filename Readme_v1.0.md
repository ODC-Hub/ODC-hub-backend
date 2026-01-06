# ODC Hub Backend – Complete Developer Guide

This document is the **official backend reference** for the ODC Hub project.
It explains **every folder, package, and main file**, how the backend works, how to run it locally, and how each developer must configure their own environment securely.

This README is written so that **any new team member can understand the backend without asking questions**.

---

## 1. Project Purpose

ODC Hub Backend is a **Spring Boot REST API** that provides:

* Secure authentication (JWT Access + Refresh tokens)
* User & admin management
* Audit logging of sensitive actions
* Profile & avatar management
* Email-based workflows (password reset, notifications)

The backend is **stateless**, **JWT-secured**, and uses **MongoDB** as the database.

---

## 2. Tech Stack

* Java 17
* Spring Boot
* Spring Security (JWT)
* MongoDB (Spring Data MongoDB)
* Maven
* SMTP (Gmail)

---

## 3. Global Project Structure

```text
odc-hub-backend/
├── src/main/java/com/odc/hub/
│   ├── admin/
│   ├── audit/
│   ├── auth/
│   ├── common.service/
│   ├── config/
│   ├── user/
│   └── OdcHubBackendApplication.java
│
├── src/main/resources/
│   ├── application.yml
│   ├── application.properties
│   ├── templates/
│   ├── static/
│   └── graphql/
│
├── .env.example
├── pom.xml
└── README.md
```

---

## 4. Application Entry Point

### `OdcHubBackendApplication.java`

* Main Spring Boot launcher
* Initializes the Spring context
* Loads all configurations, security filters, controllers, and services

---

## 5. Configuration & Environment

### `application.yml`

Contains **configuration only**, never secrets.

```yaml
spring:
  data:
    mongodb:
      uri: ${MONGO_URI}

  mail:
    host: smtp.gmail.com
    port: 587
    username: ${MAIL_USERNAME}
    password: ${MAIL_PASSWORD}

  jwt:
    secret: ${JWT_SECRET}
    access-token-expiration: 1800000
    refresh-token-expiration: 86400000
```

---

## 6. Environment Variables (.env)

### `.env.example` (committed)

```env
MONGO_URI=
MAIL_USERNAME=
MAIL_PASSWORD=
JWT_SECRET=
```

### `.env` (local only – NEVER committed)

Each developer must create their own `.env` file:

```env
MONGO_URI=mongodb+srv://<user>:<password>@cluster.mongodb.net/odc_hub
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-gmail-app-password
JWT_SECRET=your-strong-secret-key
```

📌 Rules:

* `.env` is ignored by Git
* Never share secrets
* Restart IDE after changing env variables

---

## 7. Admin Module (`admin/`)

### Purpose

Contains **ADMIN-only endpoints**.

### `AdminUserController`

* Search users
* Filter by role/status
* Approve/reject users

### `AdminAuditController`

* Exposes audit logs
* Used to track sensitive users actions

---

## 8. Audit Module (`audit/`)

### Purpose

Tracks **who did what and when** (security & traceability).

#### `model/`

* `AuditLog` → MongoDB document storing actions
* `AuditAction` → Enum describing action type

#### `dto/`

* `AuditDTO` → Safe API response representation

#### `mapper/`

* `AuditMapper` → Converts entity → DTO

#### `repository/`

* `AuditLogRepository` → MongoDB access layer

#### `service/`

* `AuditService` → Creates audit entries when admin actions occur

---

## 9. Authentication Module (`auth/`)

### `controller/AuthController`

Handles:

* Login
* Logout
* Refresh token
* Forgot password
* Reset password
* Change password

### `dto/`

* `LoginRequest`
* `RegisterRequest`
* `ForgotPasswordRequest`
* `ResetPasswordRequest`
* `ChangePasswordRequest`

### `service/AuthService`

Contains authentication business logic:

* Credential validation
* Account status checks
* Token generation
* Cookie handling

### `service/JwtService`

Responsible for:

* Generating access tokens
* Generating refresh tokens
* Validating JWTs
* Extracting claims

### `security/JwtAuthenticationFilter`

* Runs on each request
* Extracts JWT from cookies
* Authenticates user into Spring context

---

## 10. Common Services (`common.service/`)

### `EmailService`

Used to send:

* Password reset emails
* System notifications

Configured via SMTP (Gmail).

---

## 11. Configuration Layer (`config/`)

### `SecurityConfig`

* Defines secured routes
* Role-based access control
* Stateless JWT security

### `CorsConfig`

* Allows frontend access
* Configures allowed origins, headers, methods

---

## 12. User Module (`user/`)

### `controller/`

* `UserController` → profile & user info endpoints
* `AvatarController` → avatar upload & retrieval

### `dto/`

* `UserResponseDto`
* `ProfileResponseDto`
* `UpdateProfileRequest`

### `mapper/UserMapper`

* Converts User entity ↔ DTO

### `model/`

* `User` → MongoDB document
* `Role` → ADMIN / FORMATEUR / BOOTCAMPER
* `AccountStatus` → PENDING / ACTIVE / DISABLED
* `Promotion`

### `repository/UserRepository`

* MongoDB access layer

### `service/`

* `UserService` → user business logic
* `AvatarService` → avatar storage & retrieval

---

## 13. Resources Folder

### `templates/`

* Email templates (password reset, notifications)

### `static/`

* Static resources (if needed)

### `graphql/`

* Reserved for future GraphQL support

---

## 14. Running the Backend

### Prerequisites

* Java 17+
* Maven
* MongoDB Atlas

### Run

```bash
mvn clean install
mvn spring-boot:run
```

Backend URL:

```
http://localhost:8080
```

---

## 15. Common Errors

### JWT String argument cannot be null

* JWT_SECRET not loaded
* Restart IDE

### 403 Forbidden

* User not ACTIVE
* Role mismatch

### Mail not sent

* Wrong Gmail App Password

---

## 16. Git Rules

✔ `.env` → ignored
✔ `.env.example` → committed
✔ Secrets → NEVER pushed

---

## 17. Final Notes

This backend is:

* Secure
* Modular
* Production-ready

If you add new modules:

* Follow existing structure
* Update this README

🚀 Happy coding
