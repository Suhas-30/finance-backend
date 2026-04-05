# 💰 Finance Dashboard Backend

A scalable Spring Boot backend for managing personal finances, including income, expenses, user roles, and analytics.

---

## 🚀 Features

- 🔐 **Authentication** — Register, Login, JWT-based security
- 👤 **User Management** — Role & Status control
- 💸 **Record Management** — Income & Expense tracking
- 📊 **Dashboard Analytics**
    - Summary (income, expense, balance)
    - Category breakdown
    - Trends
    - Recent transactions
- 🛡️ **Role-Based Access Control** — ADMIN, ANALYST, VIEWER
- 🚦 **Rate Limiting** — IP-based & login-specific protection
- ✅ **Unit Tested** — 54/54 tests passing
- 📄 **API Documentation** — Swagger / OpenAPI

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.3.2 |
| Security | Spring Security + JWT |
| Database | PostgreSQL |
| Containerization | Docker |
| API Docs | Swagger (OpenAPI) |
| Testing | JUnit + Spring Boot Test |

---

## ⚙️ Environment Variables

Create a `.env` file in the project root with the following variables:

```env
# PostgreSQL Container Config
POSTGRES_DB=finance_db
POSTGRES_USER=postgres
POSTGRES_PASSWORD=<YOUR DB PASSWORD>

# Application DB Connection
DB_URL=jdbc:postgresql://postgres:5432/finance_db
DB_USERNAME=postgres
DB_PASSWORD=Suhas@11base

# JWT
JWT_SECRET=financeappsecurityjwtsecretkeyforbackendassignment
```

> ⚠️ **Note:** Never commit your `.env` file to version control. Add it to `.gitignore`.

---

## 🚦 Rate Limiting

This application includes basic rate limiting to prevent abuse and brute-force attacks.

### 🔹 Strategies Implemented

- **Login Rate Limiting**
    - Endpoint: `/auth/login`
    - Restriction: Limited number of login attempts per IP
    - Purpose: Prevent brute-force attacks

- **IP-Based Rate Limiting**
    - Applied to all other APIs
    - Restriction: Limits number of requests per IP

### ⚙️ Implementation Details

- Strategy Pattern used for flexibility
- Resolver dynamically selects strategy based on request path
- Implemented using Spring Boot Filter (`OncePerRequestFilter`)
- Returns HTTP `429 Too Many Requests` when limit exceeded

---

## 📌 API Documentation

**Swagger UI:**
```
https://finance-backend-j2mr.onrender.com/swagger-ui/index.html  (live)
http://localhost:8080/swagger-ui/index.html (local)
```

**OpenAPI JSON:**
```
http://localhost:8080/v3/api-docs
```

### 🔑 Authentication

Use JWT token in Swagger:
```
Bearer <your_token>
```

---

## ⚙️ Setup Instructions

### 1. Clone Repository

```bash
git clone https://github.com/Suhas-30/finance-backend.git
cd finance-dashboard
```

### 2. Configure Environment Variables

Create a `.env` file in the project root (see [Environment Variables](#️-environment-variables) section above).

### 3. Run Locally

```bash
./mvnw spring-boot:run
```

### 4. Run with Docker

```bash
docker-compose up --build
```

---

## 📊 API Overview

### 🔐 Auth APIs

| Method | Endpoint | Description |
|---|---|---|
| POST | `/auth/register` | Register a new user |
| POST | `/auth/login` | Login and receive JWT |
| PUT | `/auth/change-password` | Change password |

### 👤 User APIs *(ADMIN only)*

| Method | Endpoint | Description |
|---|---|---|
| GET | `/users` | Get all users |
| POST | `/users` | Create a new user |
| PUT | `/users/{id}/role` | Update user role |
| PUT | `/users/{id}/status` | Update user status |

### 💸 Record APIs

| Method | Endpoint | Access | Description |
|---|---|---|---|
| POST | `/records` | ADMIN | Create a record |
| GET | `/records` | ADMIN, ANALYST | Get all records |
| GET | `/records/{id}` | All | Get by ID |
| PUT | `/records/{id}` | ADMIN | Update a record |
| DELETE | `/records/{id}` | ADMIN | Delete a record |
| GET | `/records/filter` | All | Filter records |

### 📈 Dashboard APIs

| Method | Endpoint | Description |
|---|---|---|
| GET | `/dashboard/summary` | Income, expense & balance summary |
| GET | `/dashboard/recent` | Recent transactions |
| GET | `/dashboard/category-breakdown` | Breakdown by category |
| GET | `/dashboard/trends` | Trend data over time |

---

## ✅ Unit Testing

All unit tests pass successfully.

| Metric | Result |
|---|---|
| Total Tests | 54 |
| Passed | ✅ 54 |
| Failed | ❌ 0 |
| Duration | 3 sec 341 ms |

### Run Tests

```bash
./mvnw test
```

> Tested with **Spring Boot 3.3.2** on **Java 17 (Temurin)**

---

## 📦 Project Structure

```
com.financeapp
 ├── auth          # Authentication & JWT
 ├── user          # User management
 ├── record        # Income & expense records
 ├── dashboard     # Analytics & summaries
 ├── common        # Shared utilities, rate limiting filters
 └── config        # Security, Swagger, app config
```

---

## 🧪 Manual Testing

Use **Swagger UI** or **Postman** to test all APIs.

1. Register a user via `/auth/register`
2. Login via `/auth/login` to receive a JWT token
3. Click **Authorize** in Swagger UI and enter `Bearer <your_token>`
4. Test any protected endpoint

---

## 🚀 Deployment

> Live URL: https://finance-backend-j2mr.onrender.com/swagger-ui/index.html
> (Note: May take 30+ seconds on first request due to cold start on free hosting)

---


## 👨‍💻 Author

**Suhas**
