# 💰 RupeeRoot

![Java](https://img.shields.io/badge/Java-17-orange?style=for-the-badge\&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen?style=for-the-badge\&logo=springboot)
![React](https://img.shields.io/badge/React-18-blue?style=for-the-badge\&logo=react)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?style=for-the-badge\&logo=mysql)
![Redis](https://img.shields.io/badge/Redis-Cache-red?style=for-the-badge\&logo=redis)
![WebSockets](https://img.shields.io/badge/WebSockets-Real--Time-purple?style=for-the-badge\&logo=socket.io)

**RupeeRoot** is an enterprise-grade financial management suite engineered with a **Microservice-ready Monolithic architecture**. It leverages **Spring Boot** and **React** to deliver a polished full-stack experience for personal wealth tracking, optimized group debt settlement, and real-time collaborative finance.

---

## 🚀 Executive Summary (Recruiter-focused)

RupeeRoot is a production-oriented personal & group finance platform built with scalability and observability in mind. It demonstrates practical application of modern backend and frontend patterns: RESTful APIs, JWT-based stateless security, Redis caching for read-heavy endpoints, event-driven WebSocket flows for real-time collaboration, and exportable reporting for auditability. The project showcases system design decisions relevant to data engineering and backend-heavy roles.

---

## 🔧 Technical Highlights (What to call out in interviews)

* **Scalability:** Read-heavy metrics are cached in Redis (Lettuce) with TTL and `@CacheEvict` eviction after writes to maintain eventual consistency. The monolith is designed to be split into microservices (auth, transactions, chat) if needed.
* **Resilience & Observability:** Centralized AOP-based logging (`ActivityLoggingAspect`) + structured logs (JSON) for ingestion into log aggregators. Health endpoints and metrics are exposed for Prometheus scraping.
* **Security:** Stateless JWT authentication, BCrypt password hashing, role-based access control at method level (Spring Security annotations), and request-level validation.
* **Real-Time Data:** STOMP over WebSockets with server-side persistence of chat and activity events to ensure clients can recover state after reconnects.
* **Efficient Settlements:** Custom Min-Cash-Flow algorithm reduces settlement transactions from O(N^2) to O(N) in many cases — suitable for large groups.
* **Data Portability:** XLSX exports via Apache POI for compliance-ready reporting and offline audits.

---

## 🚀 Key Features & Technical Capabilities

### 💸 Financial Analytics & Visualization

* **Real-Time Dashboard:** Interactive charts (Recharts) for trends, with backend endpoints optimized for paginated and aggregated queries.
* **Low-Latency Data Fetching:** Redis caching (`@Cacheable`) applied to dashboard aggregates and leaderboard endpoints. Cache invalidation is handled using `@CacheEvict` on transactional writes.
* **Transaction Metadata:** Flexible tagging and emoji-based categorization for ML-ready features (can be extended for classification/expense prediction).

### 🧠 Algorithmic Debt Settlement

* **Min-Cash-Flow Algorithm:** Greedy reduction of group debts with complexity and correctness notes included in `docs/algorithms/`.
* **Settlement Transactions:** Generates minimal settlement instructions with idempotent APIs and audit trails.

### ⚡ Real-Time Collaboration (WebSocket)

* **STOMP + SockJS:** Fallback support and topic-based broadcasting for group channels.
* **Persistent Events:** Chat and group activity events are persisted to MySQL and re-broadcasted on reconnection.
* **Invitation Flow:** Secure group codes are generated and emailed using `JavaMailSender` with templates.

### 🛠 Advanced Simulation & Reporting

* **What-If Simulator:** Compound-interest forecasting with variable inflation/ROI inputs; outputs downloadable scenarios.
* **Automated Reporting:** Scheduled export jobs (configurable cron) produce XLSX reports and store them for user download.

### 🛡️ Security & Observability

* **JWT + Spring Security 6:** Token refresh workflows, revocation via token blacklist in Redis (optional).
* **AOP Auditing:** Method-level activity logging for security-sensitive operations, with tamper-evident timestamps.

---

## 🏗 Architecture & Design

**High-level architecture**: Monolith with clear bounded-context modules (Auth, Transactions, Chat, Reporting). Services communicate internally via method calls; the system is designed to be extracted into microservices when required.

**Data stores**:

* MySQL (primary transactional store) — normalized schema for transactions, groups, users.
* Redis (in-memory cache & ephemeral token store) — caching and short-lived state.

**Asynchronous & background processing**:

* Email dispatch and scheduled exports run on an async thread pool (Spring `@Async`) or via a lightweight job scheduler for production.

**Deployment footprint**: Designed for containerization (Docker + docker-compose) and cloud deployment (ECS/EKS or simple ECS Fargate). CI/CD pipelines can build, test and push artifacts to Docker Hub / ECR and deploy via GitHub Actions.

---

## 📸 Screenshots 

> Click any thumbnail to open full-size image.

### Local preview images (for canvas only)

![Local Screenshot 1](/mnt/data/Screenshot 2025-11-25 at 10.14.32 PM.png)

![Local Screenshot 2](/mnt/data/Screenshot 2025-11-25 at 10.14.52 PM.png)

### Project screenshots

<table>
<tr>
<td>
Dashboard — Overview  
<img src="docs/screenshots/dashboard.png" width="420" />
</td>
<td>
Dashboard — Charts 
<img src="docs/screenshots/dashboard1.png" width="420" />
</td>
</tr>
<tr>
<td>
Dashboard — Transactions 
<img src="docs/screenshots/dashboard2.png" width="420" />
</td>
<td>
Debt Settlement 
<img src="docs/screenshots/settlement.png" width="420" />
</td>
</tr>
</table>

---

### Real-Time Collaboration

<table>
<tr>
<td>
Recent Activity  
<img src="docs/screenshots/recentactivity.png" width="420" />
</td>
<td>
Group Chat  
<img src="docs/screenshots/chat.png" width="420" />
</td>
</tr>
<tr>
<td>
Group Invitations
<img src="docs/screenshots/groupinvitations.png" width="420" />
</td>
<td>
Invitation Email
<img src="docs/screenshots/invitationmail.png" width="420" />
</td>
</tr>
</table>

---

### Simulation & Export

<table>
<tr>
<td>
What-If Simulator
<img src="docs/screenshots/whatif.png" width="420" />
</td>
<td>
Excel Export (.xlsx)
<img src="docs/screenshots/excel.png" width="420" />
</td>
</tr>
</table>

---

## ✅ Running Locally (developer steps)

### Prerequisites

* Java 17+, Maven, Node 18+, MySQL, Redis (optional)

### Setup

```bash
# clone
git clone https://github.com/ShivaScripts/Rupee-Root.git
cd Rupee-Root

# backend
cd backend
cp src/main/resources/application.properties.example src/main/resources/application.properties
# edit DB creds
./mvnw clean install
./mvnw spring-boot:run &

# frontend
cd ../frontend
npm install
npm run dev
```

**Notes:** Use `docker-compose up` (if available) to start a local MySQL + Redis for reproducible setup.

---

## 🤝 Contributing

Contributions are welcome. Please open issues for bugs/feature requests and submit PRs against `develop`.

**Suggested PR checklist:**

* Feature branch named `feature/<short-desc>`
* Unit tests added
* Documentation updated (`docs/`)

---

## 📄 License

This project is licensed under the MIT License — see [LICENSE](LICENSE).

---

## 📬 Contact

Shivam Deore — [shivamdeore160@gmail.com](mailto:shivamdeore160@gmail.com)
