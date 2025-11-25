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

*(Add your screenshots here)*

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
