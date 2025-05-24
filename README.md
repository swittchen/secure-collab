# SecureCollab

SecureCollab is a production-grade collaboration platform backend built with **Spring Boot 3**, focusing on **security**, **modularity**, and **clean architecture**.

## Features

### ⚡ Authentication & Authorization

* JWT-based authentication (access + refresh tokens)
* OAuth2 login via **Google** (via Spring Security)
* Role-based access control (ADMIN, EDITOR, VIEWER)
* Method-level security via `@PreAuthorize`

### 🔒 Security Highlights

* Stateless sessions with JWT
* Rate limiting (Bucket4j + Redis)
* Secure logout flow
* CSRF protection disabled for stateless API

### 📹 API

* REST API (documented with **Swagger/OpenAPI**)
* Custom error handling and status responses
* Public and protected endpoints

### 🚀 Modular Design

* Clean/hexagonal architecture
* Separation of concerns: controller, service, repository, DTO, model
* Organized by domain: `auth`, `chat`, `user`, `workspace`, `file`, `security`, etc.

### ⚡ Real-time Chat

* WebSocket + STOMP messaging inside workspace
* Chat messages stored in PostgreSQL
* Message history available via REST

### 📂 File Handling

* File uploads stored to `uploads/` (S3-compatible in future)
* File size/type restriction for DLP compliance
* Workspace-level access control to files

### 📊 Audit & Logging

* Spring AOP-based user action logging
* Optional forwarding to ELK stack

### ✅ Tests

* JUnit5 + Testcontainers (PostgreSQL, Redis)
* > 80% coverage goal on core modules

---

## Getting Started

### ⚙ Requirements

* Java 17+
* Docker (for PostgreSQL + Redis)
* Google Cloud project with OAuth2 credentials

### ⛓ Run with Docker Compose

```bash
docker-compose up -d
```

### 📁 Setup Environment Variables

Create a `.env` file:

```env
GOOGLE_CLIENT_ID=your-client-id
GOOGLE_CLIENT_SECRET=your-client-secret
JWT_SECRET=your-jwt-secret
```

Or set them in your system environment.

### 🚀 Start the App

```bash
./mvnw spring-boot:run
```

App runs on `http://localhost:8080`

Swagger UI: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

---

## Project Structure

```
src/main/java/com/securecollab
├── auth              # Login, register, token refresh
├── chat              # WebSocket-based chat per workspace
├── config            # Spring and Swagger configuration
├── file              # File upload and download
├── security          # JWT, OAuth2, RateLimiting config
├── user              # User management and OAuth2 mapping
├── workspace         # Workspace and membership logic
```

---

## Roadmap

* [x] OAuth2 Google login
* [x] JWT token with refresh
* [x] Role-based access control
* [x] WebSocket chat per workspace
* [x] File uploads and access control
* [ ] Email invitations to workspace
* [ ] S3 support for file storage
* [ ] Admin dashboard UI

---

## License

MIT License

---

*This project is built by developers, for developers. Contributions welcome!*
