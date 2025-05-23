# SecureCollab

SecureCollab is a secure workspace collaboration platform designed with clean architecture and modern Spring Boot practices.  
It provides real-time communication, file sharing, access control, and auditing features — all built with security in mind.

## 🔐 Features

- JWT Authentication (Access + Refresh)
- OAuth2 Login (Google/GitHub)
- Role-based access (ADMIN, EDITOR, VIEWER)
- Workspace and membership management
- File upload with type & size validation (local/S3)
- WebSocket chat with history persistence
- Action auditing with Spring AOP
- Redis-based rate limiting (Bucket4j)
- Swagger API documentation
- Testcontainers-based integration tests

## 🧱 Tech Stack

- Java 17
- Spring Boot 3.x
- PostgreSQL + Testcontainers
- Redis + Bucket4j
- WebSocket (STOMP)
- Docker & Docker Compose
- Swagger (springdoc-openapi)
- JPA / Hibernate

## 🚀 Getting Started

```bash
docker-compose up --build
```

Then visit:  
📫 `http://localhost:8080/swagger-ui.html`

## ✅ Test

```bash
./mvnw test
```

## 🧪 Coverage

- aim ≥ 80% unit & integration test coverage for critical components
- Structured by `controller / service / repository / dto / mapper / model`

## 📁 Project Structure

```
com.securecollab
├── auth
├── chat
├── file
├── security
├── user
├── workspace
└── audit
```

## 🛠 Dev Hints

- Use `application-dev.yml` for local overrides
- Devtools enabled for hot reload
- Redis is required for rate limiting
- PostgreSQL & Redis run via Docker Compose

## 📜 License

MIT 
