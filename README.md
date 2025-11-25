# 💰 RupeeRoot

![Java](https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen?style=for-the-badge&logo=springboot)
![React](https://img.shields.io/badge/React-18-blue?style=for-the-badge&logo=react)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?style=for-the-badge&logo=mysql)
![Redis](https://img.shields.io/badge/Redis-Cache-red?style=for-the-badge&logo=redis)
![WebSockets](https://img.shields.io/badge/WebSockets-Real--Time-purple?style=for-the-badge&logo=socket.io)

**RupeeRoot** is a comprehensive full-stack financial management suite designed to simplify personal wealth tracking, debt settlement, and financial planning. It combines enterprise-grade security with real-time collaboration tools, making it easier for individuals and groups to manage money together.

## 🌟 Key Features

### 💸 Financial Management

* **Dashboard Analytics:** Get a real-time overview of your Total Balance, Income, and Expenses with interactive charts (Recharts) powered by low-latency caching.
* **Transaction Tracking:** Log incomes and expenses with custom categories and emoji support.
* **Smart Filtering:** Filter transaction history by date, category, or type for detailed analysis.
* **Excel Reports:** Export your financial data instantly to Excel for offline analysis.

### 🤝 Collaboration & Social

* **Real-Time Group Chat:** A fully integrated chat system powered by **WebSockets (STOMP)** allows group members to discuss expenses instantly. Messages are persisted in the database, ensuring history is never lost.
* **Debt Settlement:** Simplify group debts using a **Greedy Algorithm** that minimizes the number of transactions required to settle dues among multiple people.
* **Group Management:** Create and manage expense groups to track shared spending easily.

### 🛠 Advanced Tools

* **"What-If" Calculator:** Simulate future savings based on monthly investments, inflation rates, and expected returns.
* **Activity Auditing:** A complete audit trail of user actions (Logins, Updates, Deletions) for enhanced security.

## 🏗 Technical Architecture

RupeeRoot is built using a **Microservice-ready Monolithic architecture**, ensuring robust performance and scalability.

### ⚡ Performance Optimization (Caching)

To ensure low latency on the most frequently accessed page (The Dashboard), the application utilizes **Spring Cache with Redis**.

* **Cache Strategy:** Dashboard metrics (Total Balance, Recent Transactions) are cached using `@Cacheable(value = "dashboard", key = "#userId")`.
* **Cache Consistency:** The application employs an event-driven eviction policy (`@CacheEvict`). Operations that modify data (e.g., `addExpense`, `deleteIncome`) automatically invalidate the specific user's cache to ensure data consistency without over-fetching from the database.

### 💬 Real-Time Communication

* **WebSockets:** Implemented using the **STOMP** protocol over WebSockets.
* **Flow:** The `WebSocketConfig` sets up a message broker that routes messages to `/topic` destinations.
* **Persistence:** Unlike ephemeral chat systems, the `ChatController` intercepts messages to save them to the MySQL database *before* broadcasting, ensuring a persistent audit trail of group conversations.

### 🔒 Security

* **Stateless Authentication:** Secured using **JWT (JSON Web Tokens)**. The `SecurityConfig` creates a stateless session policy, ensuring scalability.
* **Aspect-Oriented Programming (AOP):** Logging logic is decoupled from business logic using Spring AOP. The `ActivityLoggingAspect` intercepts service execution to create secure audit logs without cluttering the codebase.

### 🧠 Algorithmic Efficiency

* **Debt Simplification:** The application uses a custom implementation of the **min-cash-flow algorithm**. It calculates the net balance of every user and iteratively settles the maximum debtor with the maximum creditor, significantly reducing the complexity of group settlements.

## 💻 Tech Stack

| Area | Technologies | 
| ----- | ----- | 
| **Backend** | Java 17, Spring Boot, Hibernate/JPA, Spring Security, Spring AOP | 
| **Frontend** | React.js, Vite, Tailwind CSS, Framer Motion, Recharts | 
| **Database** | MySQL (Primary), Redis (Caching) | 
| **Real-time** | Spring WebSocket (STOMP), SockJS | 
| **Tools** | Apache POI (Excel), JavaMailSender (Email), Docker | 

## 🚀 Getting Started

### Prerequisites

* Java 17+
* Node.js & npm
* MySQL Server
* Redis Server (for caching)

### Backend Setup

1. Clone the repository.
2. Navigate to the `backend` directory.
3. Update `src/main/resources/application.properties` with your MySQL and Redis credentials.
4. Run the application:

   ```bash
   ./mvnw spring-boot:run
