# Password Strength Checker & Improvement Suggestion API

[![Java 17](https://img.shields.io/badge/Java-17-orange.svg)](https://adoptium.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Docker](https://img.shields.io/badge/Docker-Ready-blue.svg)](https://www.docker.com/)
[![Kubernetes](https://img.shields.io/badge/Kubernetes-3%20Replicas-326CE5.svg)](https://kubernetes.io/)

A lightweight Spring Boot REST API for password strength evaluation and actionable security recommendations, accompanied by a complete end-to-end DevOps automation pipeline.

---



## 1. Project Overview

- **Project Title:** Password Strength of the  Checker and Improvement Suggestion API
- **Technology Stack:** Java 17, Spring Boot 3.2.5, Maven, JUnit 5, SpringDoc OpenAPI (Swagger UI)
- **DevOps Tools:** Git, GitHub, Jenkins, Ansible, Docker, Kubernetes (Minikube / Kind)
- **Architecture:** Backend-only REST API; no database required.
- **Assignment Scope:**
  - **Component 1 (10 Marks):** Git branching & PR merge, Maven build, Jenkins automated CI pipeline with JUnit test stage and artifact archiving.
  - **Component 2 (10 Marks):** Ansible server configuration (role, variable, handler, systemd service), Docker containerization, Kubernetes cluster deployment (3 replicas, ClusterIP service), Liveness & Readiness probes, and horizontal scaling.

---

## 2. API Endpoints Specification

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/` | **Default Controller:** Automatically navigates / redirects browser to `/swagger-ui/index.html` |
| `GET` | `/swagger` / `/swagger-ui` / `/docs` | Convenience redirect shortcuts to Swagger UI |
| `GET` | `/swagger-ui/index.html` | Interactive Swagger UI for in-browser API testing and exploration |
| `GET` | `/api/health` | Operational health check (returns `{"status":"UP"}`); utilized by Kubernetes liveness/readiness probes |
| `POST` | `/api/password/analyze` | Evaluates submitted password, returns score (0–4), rating label, and specific suggestions |
| `GET` | `/api/password/rules` | Retrieves password-strength heuristics and security guidelines |

### Sample Request: `POST /api/password/analyze`

```bash
curl -X POST http://localhost:8080/api/password/analyze \
  -H "Content-Type: application/json" \
  -d "{\"password\": \"hello123\"}"
```

### Sample Response:

```json
{
  "score": 2,
  "strength": "Weak",
  "suggestions": [
    "Use a longer password or passphrase (at least 12 characters is recommended).",
    "Avoid common words and predictable sequences.",
    "Consider adding more variety if appropriate."
  ]
}
```

### Global Error Response Format

When validation fails (e.g. blank input or payload exceeding 128 chars), malformed JSON is received, or an unsupported HTTP method/media type is requested, the API returns a standardized `ErrorResponse`:

```json
{
  "timestamp": "2026-11-16T12:00:00Z",
  "status": 400,
  "error": "Validation Error",
  "message": "password: Password must not be blank",
  "path": "/api/password/analyze",
  "details": [
    "password: Password must not be blank"
  ]
}
```

---

## 3. Directory Layout

```text
password-checker/
├── src/
│   ├── main/
│   │   ├── java/com/example/passwordchecker/
│   │   │   ├── PasswordCheckerApplication.java
│   │   │   ├── controller/
│   │   │   │   ├── DefaultController.java       # Root redirect to Swagger UI
│   │   │   │   └── PasswordController.java      # API operations
│   │   │   ├── service/
│   │   │   │   └── PasswordAnalysisService.java # Business heuristics
│   │   │   ├── dto/
│   │   │   │   ├── ErrorResponse.java           # Centralized API error model
│   │   │   │   ├── PasswordRequest.java
│   │   │   │   ├── PasswordResponse.java
│   │   │   │   ├── RulesResponse.java
│   │   │   │   └── HealthResponse.java
│   │   │   └── exception/
│   │   │       ├── GlobalExceptionHandler.java  # @RestControllerAdvice
│   │   │       └── InvalidPasswordException.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/java/com/example/passwordchecker/
│       ├── DefaultControllerTest.java           # Redirection verification tests
│       ├── GlobalExceptionHandlerTest.java      # Validation and error mapping tests
│       └── PasswordAnalysisServiceTest.java     # Logic unit tests
├── ansible/
│   ├── inventory.ini
│   ├── playbook.yml
│   └── roles/password_checker/
│       ├── tasks/main.yml
│       ├── handlers/main.yml
│       └── templates/password-checker.service.j2
├── k8s/
│   ├── deployment.yaml
│   └── service.yaml
├── Dockerfile
├── .dockerignore
├── Jenkinsfile
├── pom.xml
├── mvnw
├── mvnw.cmd
├── .gitignore
├── repo_link.txt
└── README.md
```

---

## 4. Component 1 Execution Guide (Git, Maven & Jenkins)

### Phase 1: Local Development & Running
```bash
# Using Maven Wrapper (Windows PowerShell / CMD)
.\mvnw.cmd spring-boot:run

# Or using Maven directly
mvn spring-boot:run
```
Open your browser to:
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- Health Check: `http://localhost:8080/api/health`
- Rules: `http://localhost:8080/api/password/rules`

### Phase 2: Git Repository & Branching History
As required by the assignment guidelines:
1. Initialize repository and commit baseline to `main`:
   ```bash
   git init
   git add .
   git commit -m "feat: initial commit with Spring Boot password-checker API"
   ```
2. Create and switch to feature branch:
   ```bash
   git checkout -b feature/password-suggestions
   # (Code modifications committed on feature branch)
   git commit -am "feat: enhance scoring heuristic and dynamic suggestions"
   ```
3. Merge feature branch into `main` (preserving merge commit):
   ```bash
   git checkout main
   git merge --no-ff feature/password-suggestions -m "Merge branch 'feature/password-suggestions' into main"
   ```
4. Push to your GitHub repository and link in `repo_link.txt`.

### Phase 3: Maven Build & Test
```bash
# Run automated tests
mvn test

# Package standalone executable JAR
mvn clean package -DskipTests
```
The runnable JAR will be located at: `target/password-checker-0.0.1-SNAPSHOT.jar`.

### Phase 4: Jenkins CI Pipeline
1. In Jenkins, create a new **Pipeline** job pointing to your GitHub repository.
2. In the job configuration, enable **Poll SCM** (e.g. `H/5 * * * *`) or GitHub webhook.
3. The `Jenkinsfile` automatically executes:
   - **Checkout**: Clones code from the GitHub repository.
   - **Test Stage (Chosen Enhancement)**: Runs `mvn test` (JUnit 5 unit tests) and records Surefire test reports.
   - **Build Stage**: Runs `mvn -DskipTests clean package` to produce the production JAR.
   - **Archive Artifact Stage**: Archives `target/*.jar` for deployment.

---

## 5. Component 2 Execution Guide (Ansible, Docker & Kubernetes)

### Phase 5: Server Configuration with Ansible
The Ansible configuration provisions OpenJDK 17, sets up the dedicated service user, copies the built JAR, installs the systemd unit file, and starts the service.

```bash
# Run from project root
ansible-playbook -i ansible/inventory.ini ansible/playbook.yml
```
- **Role:** `roles/password_checker`
- **Variable used:** `app_port: 8080`, `app_user: appuser`, `app_install_dir: /opt/password-checker`
- **Handler used:** Automatically restarts the systemd service upon JAR or template change.

### Phase 6: Docker Containerization
```bash
# Build Docker image
docker build -t password-checker:1.0 .

# Run container locally
docker run --rm -d -p 8080:8080 --name password-checker-app password-checker:1.0

# Verify container status and API
curl http://localhost:8080/api/health
```

### Phase 7: Kubernetes Deployment & Probes
1. Ensure your local Kubernetes cluster (Minikube / Kind / Docker Desktop K8s) is running.
2. Load image into cluster:
   ```bash
   # If using Minikube:
   minikube image load password-checker:1.0

   # If using Kind:
   kind load docker-image password-checker:1.0
   ```
3. Deploy manifests:
   ```bash
   kubectl apply -f k8s/
   ```
4. Verify 3 replicas and probes:
   ```bash
   kubectl get pods -l app=password-checker
   kubectl get svc password-checker
   kubectl describe pod -l app=password-checker
   ```
5. Test locally via port-forward:
   ```bash
   kubectl port-forward svc/password-checker 8080:8080
   ```
   Visit `http://localhost:8080/api/health` or `http://localhost:8080/swagger-ui/index.html`.

### Phase 8: Scaling Demonstration
```bash
# Scale up to 5 replicas
kubectl scale deployment password-checker --replicas=5
kubectl rollout status deployment/password-checker
kubectl get pods

# Scale back down to 3 replicas
kubectl scale deployment password-checker --replicas=3
kubectl rollout status deployment/password-checker
kubectl get pods
```

---

## 6. Enhancements Summary

1. **Component 1 CI Enhancement: Automated JUnit 5 Unit Tests**
   - Implemented in `PasswordAnalysisServiceTest.java`.
   - Tests short/predictable passwords, high-entropy passphrases, dynamic suggestions changing based on specific weakness, and blank/null inputs.
   - Dedicated Test stage runs before packaging in Jenkins CI.

2. **Component 2 Deployment Enhancement: Kubernetes Liveness & Readiness Probes**
   - Configured in `k8s/deployment.yaml`.
   - Readiness probe validates that Spring Boot is ready to serve traffic before Kubernetes routes requests.
   - Liveness probe monitors runtime health and restarts deadlocked or unresponsive containers automatically.

