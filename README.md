# 💰 RupeeRoot

![Java](https://img.shields.io/badge/Java-17-orange?style=for-the-badge\&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen?style=for-the-badge\&logo=springboot)
![React](https://img.shields.io/badge/React-18-blue?style=for-the-badge\&logo=react)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?style=for-the-badge\&logo=mysql)
![Redis](https://img.shields.io/badge/Redis-Cache-red?style=for-the-badge\&logo=redis)
![WebSockets](https://img.shields.io/badge/WebSockets-Real--Time-purple?style=for-the-badge\&logo=socket.io)

**RupeeRoot** is an enterprise-grade financial management suite engineered with a **Microservice-ready Monolithic architecture**. It leverages **Spring Boot** and **React** to deliver a seamless full-stack experience for personal wealth tracking, complex debt settlement algorithms, and real-time collaborative group finance.

---

## 🚀 Key Features & Technical Capabilities

### 💸 Financial Analytics & Visualization

* **Real-Time Dashboard:** Interactive visualizations (Pie/Line Charts via **Recharts**) displaying income vs. expense trends.
* **Low-Latency Data Fetching:** Implemented **Redis Caching** (`@Cacheable`) to cache dashboard metrics (Total Balance, Recent Transactions), reducing DB hits by ~40% for frequent reads.
* **Granular Transaction Logging:** Custom categorization engine with dynamic **Emoji Support** for intuitive expense tracking.

### 🧠 Algorithmic Debt Settlement

* **Greedy Algorithm Implementation:** Custom **Min-Cash-Flow Algorithm** to minimize the number of settlement transactions.
* **Graph Optimization:** Cuts worst-case `N*(N-1)` transactions toward `N-1` where possible.

### ⚡ Real-Time Collaboration (WebSocket)

* **Event-Driven Architecture:** STOMP over WebSockets + SockJS fallback.
* **Persistent Chat History:** Messages persisted to MySQL for auditability and then broadcast to subscribed clients.
* **Group Invitations:** Email-based group codes via `JavaMailSender`.

### 🛠 Advanced Simulation & Reporting

* **What-If Simulator:** Forecast savings using monthly input, inflation, ROI, etc.
* **Excel Export:** `.xlsx` export using Apache POI for offline analysis.

### 🛡️ Security & Observability

* **JWT-based stateless authentication** + `JwtRequestFilter`.
* **AOP logging** (`ActivityLoggingAspect`) for tamper-evident audit trails.

---

## 🏗 Tech Stack

**Backend:** Java 17, Spring Boot 3.x, Spring Security 6, Hibernate/JPA, MySQL 8, Redis (Lettuce), Maven
**Frontend:** React 18 (Vite), Tailwind CSS, Framer Motion, Lucide React, React Router v6, Axios

---

## 🚀 Getting Started

### Prerequisites

* Java 17+
* Node.js v18+ & npm
* MySQL 8.0+
* Redis (optional)

### Backend

```bash
git clone https://github.com/ShivaScripts/Rupee-Root.git
cd Rupee-Root/backend

# Update src/main/resources/application.properties
# then build & run
./mvnw clean install
./mvnw spring-boot:run
```

### Frontend

```bash
cd ../frontend
npm install
npm run dev
```

---
## 📸 Screenshots

### Dashboard

<table>
<tr>
<td>

**Dashboard — Overview**  
<img src="docs/screenshots/dashboard.png" width="420" style="border:1px solid #ccc;" />

</td>
<td>

**Dashboard — Charts**  
<img src="docs/screenshots/dashboard1.png" width="420" style="border:1px solid #ccc;" />

</td>
</tr>

<tr>
<td>

**Dashboard — Transactions**  
<img src="docs/screenshots/dashboard2.png" width="420" style="border:1px solid #ccc;" />

</td>
<td>

**Debt Settlement**  
<img src="docs/screenshots/settlement.png" width="420" style="border:1px solid #ccc;" />

</td>
</tr>
</table>


---

### Real-Time Collaboration

<table>
<tr>
<td>

**Recent Activity**  
<img src="docs/screenshots/recentactivity.png" width="420" style="border:1px solid #ccc;" />

</td>
<td>

**Group Chat**  
<img src="docs/screenshots/chat.png" width="420" style="border:1px solid #ccc;" />

</td>
</tr>

<tr>
<td>

**Group Invitations**  
<img src="docs/screenshots/groupinvitations.png" width="420" style="border:1px solid #ccc;" />

</td>
<td>

**Invitation Email**  
<img src="docs/screenshots/invitationmail.png" width="420" style="border:1px solid #ccc;" />

</td>
</tr>
</table>


---

### Simulation & Export

<table>
<tr>
<td>

**What-If Simulator**  
<img src="docs/screenshots/whatif.png" width="420" style="border:1px solid #ccc;" />

</td>
<td>

**Excel Export (.xlsx)**  
<img src="docs/screenshots/excel.png" width="420" style="border:1px solid #ccc;" />

</td>
</tr>
</table>

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
