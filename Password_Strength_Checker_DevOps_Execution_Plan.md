# Password Strength Checker / Analyzer — DevOps Assignment Execution Plan

## 1. Project Overview

**Project title:** Password Strength Checker and Improvement Suggestion API  
**Technology:** Java 17, Spring Boot, Maven  
**DevOps tools:** Git, GitHub, Jenkins, Ansible, Docker, Kubernetes (Minikube or Kind)  
**Architecture:** Backend-only REST API with auto-generated Swagger UI for browser-based API testing; no custom frontend and no database  
**Assignment:** Component 1 (10 marks) + Component 2 (10 marks)

### Goal
Build a small Java REST API that analyzes a password and returns a strength rating plus practical improvement suggestions. Then demonstrate the complete DevOps pipeline: source control → automatic build and test → server configuration → containerization → Kubernetes deployment and scaling.

**Scope note:** The assignment grades the DevOps pipeline, not application complexity. Keep the application simple and reliable.

## 2. Application Requirements

### Endpoints (2–3 only)

| Method | Endpoint | Purpose |
|---|---|---|
| GET | `/api/health` | Confirm the API is running; used for Kubernetes health checks |
| POST | `/api/password/analyze` | Analyze a submitted password and return rating, feedback and suggestions |
| GET | `/api/password/rules` | Display the password-strength rules used by the analyzer |

### Example request

`POST /api/password/analyze`

```json
{
  "password": "hello123"
}
```

### Example response (illustrative)

```json
{
  "score": 2,
  "strength": "Weak",
  "suggestions": [
    "Use a longer password or passphrase.",
    "Avoid common words and predictable sequences.",
    "Consider adding more variety if appropriate."
  ]
}
```

### Suggested analysis logic

1. Check whether the password is blank or missing; return a clear validation error.
2. Check length: encourage at least 12 characters and preferably longer passphrases.
3. Check for easy-to-guess passwords from a **small, locally stored** demonstration list.
4. Check for obvious sequences/repetitions such as `123456`, `abcdef`, or repeated characters.
5. Check character variety (lowercase, uppercase, digits, symbols) as a supporting signal, **not** as a mandatory rule.
6. Produce a simple score from 0 to 4 and a label such as Very Weak / Weak / Moderate / Strong / Very Strong.
7. Generate suggestions based on the actual problems found, not generic advice only.

**Security boundaries:** This is a simple educational heuristic, not a reliable measure of password cracking resistance. Do not save passwords, include them in logs, expose them in error messages, or send them to external services. Disable request-body logging. Use only dummy passwords in screenshots and tests. For real-world deployment, use HTTPS and a well-tested estimator such as zxcvbn.

## 3. Tools and Setup

Install/check:

- JDK 17 (`java -version`)
- Maven (`mvn -version`), or use the Maven Wrapper included with Spring Initializr
- Git (`git --version`)
- IntelliJ IDEA Community or VS Code
- Swagger UI (springdoc-openapi) for browser-based API testing; `curl` is an optional command-line alternative
- GitHub account
- Jenkins (local installation or Docker)
- Docker Desktop
- WSL2 Ubuntu (for Ansible on Windows), or a Linux VM
- Minikube or Kind plus `kubectl`

**Laptop-friendly approach:** Run everything locally. Docker Desktop, Jenkins and Kubernetes can use significant RAM; run the tools in stages if the laptop struggles. WSL2 Ubuntu can act as the Ansible control machine. Use a separate local Linux VM or another reachable Linux host as the Ansible target, rather than assuming Windows supports Linux service-management tasks.

## 4. Browser-Based API Testing (No Postman Required)

Use **Swagger UI** as the primary way to demonstrate the backend. It is a documentation and testing page generated automatically by Spring Boot with the springdoc dependency; it is not a separate frontend application.

1. In `pom.xml`, add the Maven dependency `org.springdoc:springdoc-openapi-starter-webmvc-ui` with a version compatible with your chosen Spring Boot release. Do not copy an arbitrary version without checking compatibility.
2. Start the app with `mvnw.cmd spring-boot:run` on Windows or `./mvnw spring-boot:run` on Linux/macOS.
3. Visit `http://localhost:8080/swagger-ui/index.html`.
4. Expand `POST /api/password/analyze` → **Try it out** → enter `{"password":"hello123"}` → **Execute**.
5. Inspect the JSON response and the HTTP status. Repeat with a stronger **dummy** password.
6. Test `GET /api/health` and `GET /api/password/rules` from the same page.
7. For Docker and Kubernetes, open the same Swagger UI address after exposing/forwarding port 8080.

Optional terminal-only fallback (no extra application needed):

```bash
curl http://localhost:8080/api/health
curl -X POST http://localhost:8080/api/password/analyze -H "Content-Type: application/json" -d '{"password":"hello123"}
```

On Windows PowerShell, `curl` may be an alias; use `curl.exe` or PowerShell `Invoke-RestMethod` instead.

**Security:** Swagger UI is for local/demo testing. Use only fake passwords. Never put passwords in GET query strings, screenshots of real credentials, logs, or Git commits. If the service is ever deployed publicly, restrict or disable Swagger UI and protect traffic with HTTPS.

## 5. Proposed Folder Structure

```text
password-checker/
├── src/
│   ├── main/
│   │   ├── java/com/example/passwordchecker/
│   │   │   ├── PasswordCheckerApplication.java
│   │   │   ├── controller/PasswordController.java
│   │   │   ├── service/PasswordAnalysisService.java
│   │   │   └── dto/
│   │   │       ├── PasswordRequest.java
│   │   │       └── PasswordResponse.java
│   │   └── resources/application.properties
│   └── test/java/com/example/passwordchecker/
│       └── PasswordAnalysisServiceTest.java
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
├── Jenkinsfile
├── pom.xml
├── .gitignore
├── README.md
└── repo_link.txt
```

## 6. Component 1 — Git, Maven and Jenkins (10 marks)

### Phase 1: Build the Java application

1. Create a Spring Boot Maven project with Java 17 and Spring Web, Validation and Spring Boot Test dependencies.
2. Add `springdoc-openapi-starter-webmvc-ui` to `pom.xml` using a version compatible with the selected Spring Boot major version (Spring Boot 3.x with a compatible springdoc 2.x release). This generates Swagger UI and OpenAPI documentation without building a frontend.
3. Implement the three endpoints.
4. Put the scoring and suggestions in `PasswordAnalysisService` so the controller stays simple.
5. Handle empty, missing and oversized input safely.
6. Start locally using `./mvnw spring-boot:run` (on Windows: `mvnw.cmd spring-boot:run`).
7. Open `http://localhost:8080/swagger-ui/index.html` in a browser. Use **Try it out** to send sample JSON requests to the POST endpoint and inspect the response. Also test the GET endpoints.
8. Optionally verify the OpenAPI JSON at `http://localhost:8080/v3/api-docs`.

**Done when:** Swagger UI loads, all three endpoints are documented and callable in the browser, and the analyzer gives different, sensible suggestions for weak and stronger dummy passwords.

### Phase 2: Git and GitHub

1. Initialize a Git repository and commit the working starter application to `main`.
2. Create a branch named `feature/password-suggestions`.
3. Add or improve suggestion logic in that branch and commit it.
4. Merge the feature branch into `main` using a pull request (preferred) or Git merge.
5. Push the repository to GitHub.
6. Capture branch/merge history as evidence.

**Done when:** GitHub shows **at least two branches and a merge**, as required by the assignment.

### Phase 3: Maven build

1. Configure `pom.xml` with Spring Boot dependencies, tests and the Maven build plugin.
2. Run `./mvnw clean package` (Windows: `mvnw.cmd clean package`) or `mvn clean package` if Maven is installed.
3. Confirm the executable JAR appears under `target/`.
4. Run `java -jar target/<generated-jar-name>.jar` and test the API again.

**Done when:** Maven builds a runnable JAR without errors.

### Phase 4: Jenkins CI pipeline

1. Install/start Jenkins and create a Pipeline job.
2. Link the job to the GitHub repository.
3. Add a `Jenkinsfile` with stages: Checkout → Test → Build → Archive Artifact.
4. Configure a GitHub webhook or SCM polling so commits automatically trigger builds.
5. Run Maven tests and package the application.
6. Archive the JAR using Jenkins' artifact-archiving step.
7. Push a small code change and verify Jenkins starts a new build **without manual intervention**.

**Done when:** Jenkins automatically checks out, tests and builds the latest code and archives the JAR.

### Chosen CI enhancement: Automated JUnit tests

Add at least **three unit tests** and run them in a dedicated Jenkins Test stage before Build:

- A short, predictable password is rated weak.
- A longer, less predictable sample receives a stronger rating.
- The suggestion list changes based on the weakness detected.

Also test blank input separately if time permits. Ensure the Build stage does not redundantly run the tests if the pipeline already ran them, for example by using `mvn -DskipTests package` **only after** a successful dedicated Test stage.

**Evidence to save:** Git history/merge, Jenkins SCM and trigger configuration, successful console output, archived JAR and passing test stage.

## 7. Component 2 — Ansible, Docker and Kubernetes (10 marks)

### Phase 5: Configure a server using Ansible

1. Prepare a reachable Linux target host (local Ubuntu VM is fine) and configure SSH access from the Ansible control machine.
2. Create `ansible/inventory.ini` with the target host.
3. Initialize a role using `ansible-galaxy init password_checker` inside `ansible/roles/`.
4. Write the playbook/role to install Java 17, copy the built JAR and create/start a systemd service.
5. Use at least one variable, such as `app_port: 8080`.
6. Use at least one handler to restart the service only when its JAR or service definition changes.
7. Run the playbook and verify the application responds on the target host.

Example command from the project root:

```bash
ansible-playbook -i ansible/inventory.ini ansible/playbook.yml
```

**Done when:** Ansible configures the target without manual installation steps and the API runs as a service. The role, variable and handler are present and demonstrably used.

### Phase 6: Docker containerization

1. Create a `Dockerfile` using a Java 17 runtime image.
2. Copy the Maven-generated JAR into the image.
3. Expose port 8080 and start the JAR.
4. Build and run the image locally.
5. Open `http://localhost:8080/swagger-ui/index.html` after starting the container; test the health and analysis endpoints through Swagger UI.

Example commands:

```bash
docker build -t password-checker:1.0 .
docker run --rm -p 8080:8080 password-checker:1.0
```

**Done when:** The container runs the same application successfully. Stop any local Java process using port 8080 before starting the container.

### Phase 7: Kubernetes deployment

1. Start a local Minikube or Kind cluster.
2. Make the Docker image available to the cluster (for Minikube, `minikube image load password-checker:1.0`; for Kind, `kind load docker-image password-checker:1.0`).
3. Create `k8s/deployment.yaml` with **3 replicas**, container port 8080 and a matching label.
4. Create `k8s/service.yaml` as a ClusterIP Service with a selector matching the Deployment labels.
5. Apply the YAML manifests and wait for pods to become ready.
6. Use `kubectl port-forward` to reach the API locally, then open Swagger UI in a browser and test the endpoints.

Example commands:

```bash
kubectl apply -f k8s/
kubectl get deployments
kubectl get pods
kubectl get svc
kubectl port-forward svc/password-checker 8080:8080
```

**Done when:** All three pods are ready and the API responds through the Kubernetes Service. For a local-only image, set a suitable `imagePullPolicy` such as `IfNotPresent` and ensure the image was loaded into the cluster.

### Phase 8: Demonstrate scaling

Run:

```bash
kubectl scale deployment password-checker --replicas=5
kubectl rollout status deployment/password-checker
kubectl get pods
kubectl scale deployment password-checker --replicas=3
kubectl rollout status deployment/password-checker
kubectl get pods
```

**Done when:** Screenshots show the deployment running at 3 replicas, then 5, then back to 3.

### Chosen deployment enhancement: Liveness and readiness probes

Use the existing `GET /api/health` endpoint for both Kubernetes probes in `deployment.yaml`:

- **Readiness probe:** Determines when the API can receive requests.
- **Liveness probe:** Helps Kubernetes restart an unresponsive application.

Set an appropriate startup delay or startup probe to allow Spring Boot to initialize. Capture the relevant YAML and `kubectl describe pod` output as evidence.

**Done when:** Kubernetes reports ready pods with the probes configured and the API responds normally.

## 8. Suggested Execution Schedule

| Session | Work | Output |
|---|---|---|
| 1 | Spring Boot setup, scoring logic and API endpoints | Working backend |
| 2 | JUnit tests, Git branching/merge and Maven build | Tests, Git history, JAR |
| 3 | Jenkins pipeline, auto-trigger and artifact archive | Completed Component 1 |
| 4 | Ansible target setup, role, variable, handler and service | Working configured host |
| 5 | Docker build and local container testing | Working Docker image |
| 6 | Kubernetes deployment, service, probes and scaling | Completed Component 2 |
| 7 | Screenshots, report, cleanup and final verification | Submission-ready project |

These are **work sessions, not guaranteed durations**. Jenkins, Ansible and local Kubernetes setup may take longer depending on laptop resources.

## 9. Screenshots / Evidence Checklist

### Component 1

- [ ] Swagger UI showing the three endpoints and a sample JSON response
- [ ] GitHub repository and at least two branches
- [ ] Git merge/pull request history
- [ ] Successful Maven build and generated JAR
- [ ] Jenkins job SCM configuration
- [ ] Jenkins automatic build trigger configuration
- [ ] Jenkins successful console output
- [ ] Archived JAR artifact in Jenkins
- [ ] Passing JUnit Test stage (chosen enhancement)

### Component 2

- [ ] Ansible inventory, playbook, role, variable and handler
- [ ] Successful `ansible-playbook` run
- [ ] API responding from the configured host
- [ ] Dockerfile and successful Docker image build
- [ ] Containerized API responding through Swagger UI
- [ ] Kubernetes Deployment and Service YAML
- [ ] Three ready Kubernetes pods
- [ ] Five ready pods after scaling up
- [ ] Three ready pods after scaling down
- [ ] `kubectl get svc` output
- [ ] API response via Kubernetes Service, tested through Swagger UI
- [ ] Probe configuration and healthy pod status (chosen enhancement)

## 10. Report and Submission Plan

The provided assignment requires a **separate report section for each component** using the provided template/format:

1. Title page and certificate (as supplied by the course)
2. Abstract
3. Introduction
4. Implementation: Development Environment, Code and Description
5. Chosen enhancement and why it was selected
6. Results with screenshots
7. Conclusion
8. References

Prepare the same structure for **Component 1 and Component 2 separately**. Follow the course-provided report template rather than inventing a new format.

### Final submission items

- [ ] Component 1 report section with all required evidence
- [ ] Component 2 report section with all required evidence
- [ ] Complete source-code folder
- [ ] `pom.xml`, `Jenkinsfile` and test files
- [ ] Ansible inventory, playbook, role and handler
- [ ] `Dockerfile`, Kubernetes Deployment and Service YAML
- [ ] `README.md` with exact run/demo steps
- [ ] GitHub/GitLab repository URL included in the report
- [ ] `repo_link.txt` containing the repository URL
- [ ] Course coordinator added as a repository collaborator
- [ ] All screenshots match the submitted, working code

## 11. Final Live Demo Order

1. Explain the password-analysis API in 30–60 seconds.
2. Open Swagger UI, expand `POST /api/password/analyze`, click **Try it out**, enter a dummy weak password, execute it and show the score and improvement suggestions.
3. Show Git branches and the merged feature branch.
4. Push a small change and show Jenkins automatically testing/building and archiving the JAR.
5. Show the Ansible playbook and successful configuration run.
6. Show the running Docker container.
7. Show Kubernetes with 3 ready pods and a working API response.
8. Scale to 5 pods, then back to 3.
9. Show the configured liveness/readiness probes.

## 12. Definition of Done

The assignment is complete only when the **same password-checker application** has been demonstrated across both components, every required DevOps tool has working evidence, one CI enhancement and one deployment enhancement are implemented, and the source code, reports and repository link are ready to submit.

---

**Source:** Execution requirements adapted from the uploaded 5-page DevOps assignment (`DOC-20260824-WA0017.pdf`). The password-checker API design, chosen enhancements, schedule, folder structure and security recommendations are proposed implementation decisions for this project.
