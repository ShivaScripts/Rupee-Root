# 💰 RupeeRoot

![Java](https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen?style=for-the-badge&logo=springboot)
![React](https://img.shields.io/badge/React-18-blue?style=for-the-badge&logo=react)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?style=for-the-badge&logo=mysql)
![Redis](https://img.shields.io/badge/Redis-Cache-red?style=for-the-badge&logo=redis)
![WebSockets](https://img.shields.io/badge/WebSockets-Real--Time-purple?style=for-the-badge&logo=socket.io)

**RupeeRoot** is an enterprise-grade financial management suite engineered with a **Microservice-ready Monolithic architecture**. It leverages **Spring Boot** and **React** to deliver a seamless full-stack experience for personal wealth tracking, complex debt settlement algorithms, and real-time collaborative group finance.

---

## 🚀 Key Features & Technical Capabilities

### 💸 Financial Analytics & Visualization
* **Real-Time Dashboard:** Interactive visualizations (Pie/Line Charts via **Recharts**) displaying income vs. expense trends.
* **Low-Latency Data Fetching:** Implemented **Redis Caching** (`@Cacheable`) to cache dashboard metrics (Total Balance, Recent Transactions), reducing DB hits by ~40% for frequent reads.
* **Granular Transaction Logging:** Custom categorization engine with dynamic **Emoji Support** for intuitive expense tracking.

![Dashboard Screenshot](docs/screenshots/dashboard.png)


![Dashboard Screenshot](docs/screenshots/dashboard1.png)


![Dashboard Screenshot](docs/screenshots/dashboard2.png)

### 🧠 Algorithmic Debt Settlement
Solves the "Who owes whom" problem efficiently.
* **Greedy Algorithm Implementation:** Utilizes a custom **Min-Cash-Flow Algorithm** to simplify a complex graph of debts into the minimum number of transactions required.
* **Graph Optimization:** Reduces `N*(N-1)` potential transactions to `N-1` in the best-case scenario, significantly optimizing settlement cycles for large groups.

![Debt Settlement Screenshot](docs/screenshots/settlement.png)

### ⚡ Real-Time Collaboration (WebSocket)
* **Event-Driven Architecture:** Implemented **STOMP over WebSockets** to facilitate instant group communication.
* **Persistent Chat History:** Unlike ephemeral socket connections, the **ChatController** intercepts messages, persists them to MySQL for audit trails, and then broadcasts to subscribed clients via `/topic` destinations.
* **Group Invitations:** Integrated **JavaMailSender** for asynchronous email dispatch of unique group join codes.

![Group Chat Screenshot](docs/screenshots/recentactivity.png)

![Group Chat Screenshot](docs/screenshots/chat.png)

![Group Chat Screenshot](docs/screenshots/groupinvitations.png)

![Group Chat Screenshot](docs/screenshots/invitationmail.png)


### 🛠 Advanced Simulation & Reporting
* **"What-If" Financial Simulator:** A forecasting tool that projects future savings based on user-defined variables (Monthly Investment, Inflation Rate, ROI) using compound interest logic.
* **Automated Reporting:** Generates and exports financial statements in `.xlsx` format using **Apache POI**, enabling offline accounting and data portability.
* 
![Calculator Screenshot](docs/screenshots/whatif.png)

![Calculator Screenshot](docs/screenshots/excel.png)

### 🛡️ Security & Observability
* **Stateless Authentication:** Secured via **JWT (JSON Web Tokens)** with a custom `JwtRequestFilter` intercepting every request for validity.
* **Aspect-Oriented Programming (AOP):** Decoupled cross-cutting concerns (Logging) from business logic using **Spring AOP**. The `ActivityLoggingAspect` automatically intercepts service layer execution to create tamper-evident audit logs.

---

## 🏗 Technical Architecture

### Backend (Spring Boot 3.x)
* **Core:** Java 17, Spring Web MVC
* **Data Layer:** Hibernate/JPA with MySQL 8.0
* **Caching:** Redis (Lettuce Client) with `@CacheEvict` policies for data consistency.
* **Security:** Spring Security 6 (Stateless Session Management), BCrypt Password Encoding.
* **Real-Time:** Spring WebSocket (STOMP), SockJS Fallback.
* **Aspects:** Spring AOP for centralized logging and auditing.
* **Build Tool:** Maven

### Frontend (React 18)
* **Build System:** Vite (for fast HMR and optimized builds).
* **State Management:** React Context API & Hooks (`useReducer`, `useContext`).
* **Routing:** React Router DOM v6.
* **UI/UX:** Tailwind CSS (Utility-first), Framer Motion (Animations), Lucide React (Icons).
* **HTTP Client:** Axios with Interceptors for JWT injection and global error handling.

---

## 🚀 Getting Started

### Prerequisites

* Java 17+
* Node.js v18+ & npm
* MySQL Server 8.0+
* Redis Server (optional, for caching)

### Backend Setup

1.  **Clone the repository:**
    ```bash
    git clone [https://github.com/ShivaScripts/Rupee-Root.git](https://github.com/ShivaScripts/Rupee-Root.git)
    cd Rupee-Root/backend
    ```

2.  **Configure Environment:**
    Update `src/main/resources/application.properties`:
    ```properties
    spring.datasource.url=jdbc:mysql://localhost:3306/moneymanager
    spring.datasource.username=root
    spring.datasource.password=YOUR_PASSWORD
    jwt.secret=YOUR_SECURE_KEY
    ```

3.  **Build & Run:**
    ```bash
    ./mvnw clean install
    ./mvnw spring-boot:run
    ```

### Frontend Setup

1.  **Navigate to frontend:**
    ```bash
    cd ../frontend
    ```

2.  **Install Dependencies:**
    ```bash
    npm install
    ```

3.  **Start Development Server:**
    ```bash
    npm run dev
    ```

---

## 📸 Screenshots

<!-- START: Styled screenshot gallery --> <div style="display:flex; gap:18px; flex-wrap:wrap; justify-content:center; align-items:flex-start; margin: 12px 0;"> <!-- Dashboard group --> <figure style="margin:0; text-align:center; width:320px;"> <a href="docs/screenshots/dashboard.png" target="_blank" rel="noopener noreferrer"> <img src="docs/screenshots/dashboard.png" alt="Dashboard" width="320" style="border:1px solid #e6eef6; border-radius:10px; padding:6px; box-shadow:0 8px 20px rgba(18,38,63,0.06); display:block;" /> </a> <figcaption style="font-size:13px; color:#333; margin-top:8px;">Dashboard — Overview</figcaption> </figure> <figure style="margin:0; text-align:center; width:320px;"> <a href="docs/screenshots/dashboard1.png" target="_blank" rel="noopener noreferrer"> <img src="docs/screenshots/dashboard1.png" alt="Dashboard 1" width="320" style="border:1px solid #e6eef6; border-radius:10px; padding:6px; box-shadow:0 8px 20px rgba(18,38,63,0.06); display:block;" /> </a> <figcaption style="font-size:13px; color:#333; margin-top:8px;">Dashboard — Charts</figcaption> </figure> <figure style="margin:0; text-align:center; width:320px;"> <a href="docs/screenshots/dashboard2.png" target="_blank" rel="noopener noreferrer"> <img src="docs/screenshots/dashboard2.png" alt="Dashboard 2" width="320" style="border:1px solid #e6eef6; border-radius:10px; padding:6px; box-shadow:0 8px 20px rgba(18,38,63,0.06); display:block;" /> </a> <figcaption style="font-size:13px; color:#333; margin-top:8px;">Dashboard — Transactions</figcaption> </figure> <!-- Settlement --> <figure style="margin:0; text-align:center; width:320px;"> <a href="docs/screenshots/settlement.png" target="_blank" rel="noopener noreferrer"> <img src="docs/screenshots/settlement.png" alt="Debt Settlement" width="320" style="border:1px solid #f0efe9; border-radius:10px; padding:6px; box-shadow:0 8px 20px rgba(18,38,63,0.04); display:block;" /> </a> <figcaption style="font-size:13px; color:#333; margin-top:8px;">Debt Settlement Algorithm</figcaption> </figure> <!-- Real-time / Chat --> <figure style="margin:0; text-align:center; width:320px;"> <a href="docs/screenshots/recentactivity.png" target="_blank" rel="noopener noreferrer"> <img src="docs/screenshots/recentactivity.png" alt="Recent Activity" width="320" style="border:1px solid #eef6f1; border-radius:10px; padding:6px; box-shadow:0 8px 20px rgba(18,38,63,0.04); display:block;" /> </a> <figcaption style="font-size:13px; color:#333; margin-top:8px;">Recent Activity</figcaption> </figure> <figure style="margin:0; text-align:center; width:320px;"> <a href="docs/screenshots/chat.png" target="_blank" rel="noopener noreferrer"> <img src="docs/screenshots/chat.png" alt="Group Chat" width="320" style="border:1px solid #eef3ff; border-radius:10px; padding:6px; box-shadow:0 8px 20px rgba(18,38,63,0.04); display:block;" /> </a> <figcaption style="font-size:13px; color:#333; margin-top:8px;">Group Chat</figcaption> </figure> <figure style="margin:0; text-align:center; width:320px;"> <a href="docs/screenshots/groupinvitations.png" target="_blank" rel="noopener noreferrer"> <img src="docs/screenshots/groupinvitations.png" alt="Group Invitations" width="320" style="border:1px solid #fff4e6; border-radius:10px; padding:6px; box-shadow:0 8px 20px rgba(18,38,63,0.04); display:block;" /> </a> <figcaption style="font-size:13px; color:#333; margin-top:8px;">Group Invitations</figcaption> </figure> <figure style="margin:0; text-align:center; width:320px;"> <a href="docs/screenshots/invitationmail.png" target="_blank" rel="noopener noreferrer"> <img src="docs/screenshots/invitationmail.png" alt="Invitation Mail" width="320" style="border:1px solid #f6f0ff; border-radius:10px; padding:6px; box-shadow:0 8px 20px rgba(18,38,63,0.04); display:block;" /> </a> <figcaption style="font-size:13px; color:#333; margin-top:8px;">Invitation e-mail</figcaption> </figure> <!-- What-if / Excel --> <figure style="margin:0; text-align:center; width:320px;"> <a href="docs/screenshots/whatif.png" target="_blank" rel="noopener noreferrer"> <img src="docs/screenshots/whatif.png" alt="What If Calculator" width="320" style="border:1px solid #eef9f6; border-radius:10px; padding:6px; box-shadow:0 8px 20px rgba(18,38,63,0.04); display:block;" /> </a> <figcaption style="font-size:13px; color:#333; margin-top:8px;">What-If Simulator</figcaption> </figure> <figure style="margin:0; text-align:center; width:320px;"> <a href="docs/screenshots/excel.png" target="_blank" rel="noopener noreferrer"> <img src="docs/screenshots/excel.png" alt="Excel Export" width="320" style="border:1px solid #f3f7ff; border-radius:10px; padding:6px; box-shadow:0 8px 20px rgba(18,38,63,0.04); display:block;" /> </a> <figcaption style="font-size:13px; color:#333; margin-top:8px;">Excel Export (.xlsx)</figcaption> </figure> </div> <!-- END: Styled screenshot gallery -->
---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
