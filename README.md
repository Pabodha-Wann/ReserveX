# 📚 ReserveX - Book Fair Stall Booking System

**ReserveX** is a modern, full-stack web application designed to manage stall reservations for the Colombo International Book Fair. It provides a seamless, interactive experience for book vendors to select, reserve, and manage their stalls, while offering administrators a powerful dashboard to oversee the entire event.

> **Academic Context:** This project was developed as the group assignment for the **SENG 22212 – Software Architecture and Design** module at the University of Kelaniya.

## ✨ Key Features

* **Interactive Floor Plan:** A dynamic, grid-based live map allowing vendors to visually select available stalls.
* **Dual-Portal Architecture:** * **Online Portal:** For vendors to browse the map, book up to 3 stalls, and assign specific literary genres to their spaces.
    * **Admin Portal:** For event organizers to manage stall availability, view booking statistics via a live dashboard, and oversee vendor accounts.
* **Secure Authentication:** Role-based access control (Vendor vs. Admin/Employee) secured by JSON Web Tokens (JWT).
* **Automated Email Notifications:** Vendors receive instant confirmation emails with a QR code receipt upon successful reservation.
* **Real-time Analytics:** Visual dashboards utilizing pie charts and dynamic stats calculations.
## Project Modules

- `backend`: Spring Boot REST API with JWT authentication, role-based access, MySQL persistence, and email support.
- `online-portal`: Vendor-facing React app for authentication, stall browsing, reservations, and profile actions.
- `admin-portal`: Admin/employee React app for monitoring reservations and managing stalls.
- `database`: Data model reference (`db.sql`).

## Tech Stack

- **Backend**: Java 17, Spring Boot, Spring Security, Spring Data JPA, MySQL, Maven
- **Frontend**: React, Vite, Axios, React Router
- **Other**: JWT, SMTP email integration


## Getting Started

### Prerequisites

- Java 17+
- Node.js 18+ and npm
- MySQL

### 1) Database Setup
1. Create a MySQL database named `reservex`.
2. The initial database schema is located in `backend/src/main/resources/schema.sql`. 
3. Because `spring.jpa.hibernate.ddl-auto=update` is configured, Spring Boot will automatically manage the tables, but you can also run the SQL script manually.

### 2) Backend Setup

The backend requires a `.env` file for secure credentials (database, email, etc.) and Auth0 for authentication.

1. Navigate to the backend directory:
   ```bash
   cd backend
   ```
2. Create a `.env` file in the `backend/` directory with the following variables:
   ```env
   DB_USERNAME=root
   DB_PASSWORD=your_mysql_password
   JWT_SECRET=your_jwt_secret_key
   MAIL_USERNAME=your_email@gmail.com
   MAIL_PASSWORD=your_app_password
   SSL_KEYSTORE_PASSWORD=password
   ```
3. Run the Spring Boot application:
   ```bash
   mvnw.cmd spring-boot:run
   ```
   > **Note:** The backend is configured to run securely via HTTPS on port `8443` (i.e., `https://localhost:8443`). 

### 3) Online Portal (Vendor App)

1. Open a new terminal and navigate to the online portal:
   ```bash
   cd online-portal
   npm install
   ```
2. Configure Auth0 by creating an `.env.local` file (or updating `vite.config.js` / Auth0Provider settings) with your Auth0 Domain and Client ID.
3. Start the development server:
   ```bash
   npm run dev
   ```
   The Vite server will typically start securely on `https://localhost:5173`.

### 4) Admin Portal (Organizer App)

1. Open a third terminal and navigate to the admin portal:
   ```bash
   cd admin-portal
   npm install
   npm run dev
   ```
   If the online portal is already running on port 5173, Vite will automatically assign the Admin portal to `https://localhost:5174`.

---

## 🐳 Running with Docker (Production Ready)

If you have Docker installed, you can skip the manual setup entirely and start the full application stack (MySQL Database, Spring Boot Backend, Online Portal, and Admin Portal) with a single command!

1. Ensure your `backend/.env` file is created with all the required secrets (see Backend Setup).
2. From the root directory of the project, run:
   ```bash
   docker-compose up --build -d
   ```
   
**What this does:**
- `reservex-mysql`: Starts a MySQL 8 container and automatically builds your tables using `schema.sql`.
- `reservex-backend`: Compiles the Spring Boot `.jar` via Maven, links it to the database container, and serves it on `https://localhost:8443`.
- `reservex-online-portal`: Compiles the React app into optimized static files and serves them via Nginx on `http://localhost:8081`.
- `reservex-admin-portal`: Compiles the Admin React app and serves it via Nginx on `http://localhost:8082`.

To stop the containers, run: `docker-compose down`

## Environment & Security Notes

- **Secrets Management:** Sensitive keys and database passwords have been removed from source code and are injected dynamically via the `.env` file.
- **HTTPS/SSL:** End-to-End encryption is enforced locally. The backend utilizes a self-signed PKCS12 keystore, and the Vite frontends use the `@vitejs/plugin-basic-ssl` plugin.
- **OWASP Top 10 Patches:** This repository has been audited and patched for major security vulnerabilities, including IDOR, Injection, Cryptographic Failures, and TOCTOU Race Conditions.

## API Overview (High-Level)

Base URL: `https://localhost:8443`

- Auth: handled via Auth0
- Users: `/api/users/*`
- Reservations: `/api/reservations/*`
- Stalls: `/api/stalls/*`
- Admin: `/api/admin/*`
- Genres: `/api/genres/*`
- Contact: `/api/contact`

## Academic Context

This codebase was developed collaboratively as a university coursework deliverable for:

**SENG 22212 - Software Architecture and Design**  
Group Assignment Project

