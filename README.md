# 🍽️ Restaurant Management System

A production-grade REST API backend for managing restaurant operations — built with **Java 17** and **Spring Boot 3.2**.

---

## ✅ Features

| Module | Description |
|--------|-------------|
| **Auth** | JWT-based register/login with BCrypt password hashing |
| **Menu** | Full CRUD with category filtering, search, and caching |
| **Tables** | Real-time status management (AVAILABLE / OCCUPIED / RESERVED / MAINTENANCE) |
| **Orders** | Place orders, auto 10% tax calculation, status state machine, revenue reports |
| **Reservations** | Table booking with party-size validation and conflict detection |
| **Inventory** | Stock tracking with low-stock alerts, restock and deduct operations |

---

## 🛠️ Tech Stack

| Technology | Version | Purpose |
|------------|---------|---------|
| Java | 17 LTS | Core language |
| Spring Boot | 3.2.0 | Application framework |
| Spring Security | 6.2.0 | JWT auth, BCrypt, role-based access |
| Spring Data JPA + Hibernate | 3.2 / 6.3 | ORM, repository abstraction |
| **H2 Database** | 2.x | **Embedded in-memory DB — zero setup required** |
| Spring Cache | 3.2.0 | @Cacheable on menu endpoints |
| SpringDoc OpenAPI | 2.2.0 | Swagger UI at /swagger-ui.html |
| JUnit 5 + Mockito | 5.x | Unit testing |
| Lombok | 1.18.x | Boilerplate reduction |
| Maven | 3.9+ | Build tool |

> **Why H2?**
> H2 is an embedded in-memory database that runs inside the same JVM as the application.
> No installation, no server, no configuration — it just works.
> Because all database interactions go through the JPA/Hibernate abstraction layer,
> switching to MySQL or PostgreSQL for production requires only **3 lines** of config change.

---

## 🚀 How to Run

### Prerequisites
- Java 17+
- Maven 3.9+
- *(No database installation needed — H2 is embedded)*

### Step 1 — Clone the repository
```bash
git clone https://github.com/AndyS12/BackEnd-RestaurantManagementSystem.git
cd BackEnd-RestaurantManagementSystem
```

### Step 2 — Build
```bash
mvn clean package -DskipTests
```

### Step 3 — Run
```bash
mvn spring-boot:run
```

Or run the JAR directly:
```bash
java -jar target/restaurant-management-system-1.0.0.jar
```

The server starts at: **http://localhost:8080**

### Step 4 — Access Swagger UI
```
http://localhost:8080/swagger-ui.html
```

Click **Authorize** → paste `Bearer <token>` after logging in.

### Step 5 — Access H2 Console (optional — inspect DB)
```
http://localhost:8080/h2-console
```

| Field | Value |
|-------|-------|
| JDBC URL | `jdbc:h2:mem:restaurant_db` |
| Username | `sa` |
| Password | *(leave blank)* |

---

## 👤 Default Seeded Users

On first startup the database is **auto-seeded** with test data — no manual SQL needed.

| Username | Password | Role |
|----------|----------|------|
| admin | admin123 | ADMIN |
| manager | manager123 | MANAGER |
| waiter1 | waiter123 | WAITER |
| customer1 | cust123 | CUSTOMER |

Also seeded: **10 tables**, **9 menu items**, **5 inventory items**.

> ⚠️ H2 is in-memory — data resets on every restart. The seeder repopulates it automatically on each startup so test data is always ready.

---

## 🔐 Authentication Flow

**1. Login to get your JWT token:**
```bash
POST /api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}
```

**2. Copy the token from the response:**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "role": "ADMIN"
}
```

**3. Add to all subsequent requests:**
```
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
```

---

## 📡 API Endpoints

### Authentication
```
POST /api/auth/register   → Register new user
POST /api/auth/login      → Login, returns JWT token
```

### Menu *(Public GET · ADMIN/MANAGER for write)*
```
GET    /api/menu                         → All menu items (cached)
GET    /api/menu/available               → Available items only
GET    /api/menu/category/{CATEGORY}     → Filter by category
GET    /api/menu/search?keyword=XXX      → Search by name
POST   /api/menu                         → Create item
PUT    /api/menu/{id}                    → Update item
PATCH  /api/menu/{id}/toggle-availability → Toggle availability
DELETE /api/menu/{id}                    → Delete item
```

Valid categories: `APPETIZER` `MAIN_COURSE` `DESSERT` `BEVERAGE` `SOUP` `SALAD` `SPECIAL`

### Tables
```
GET    /api/tables                          → All tables
GET    /api/tables/available                → Available tables only
GET    /api/tables/available/party-size/{n} → Tables that fit n people
POST   /api/tables                          → Create table
PUT    /api/tables/{id}                     → Update table
PATCH  /api/tables/{id}/status              → Update status
DELETE /api/tables/{id}                     → Delete table
```

### Orders
```
POST   /api/orders                       → Place new order
GET    /api/orders                       → All orders
GET    /api/orders/{id}                  → Order by ID
GET    /api/orders/status/{STATUS}       → Filter by status
GET    /api/orders/table/{tableId}       → Orders for a table
PATCH  /api/orders/{id}/status           → Update order status
PATCH  /api/orders/{id}/discount?amount= → Apply discount
GET    /api/orders/revenue?start=&end=   → Revenue for date range
```

Valid order statuses: `PENDING` → `CONFIRMED` → `PREPARING` → `READY` → `SERVED` → `PAID`

### Reservations
```
POST   /api/reservations                  → Create reservation
GET    /api/reservations                  → All reservations
GET    /api/reservations/{id}             → Reservation by ID
GET    /api/reservations/customer/{id}    → By customer
PATCH  /api/reservations/{id}/cancel      → Cancel reservation
PATCH  /api/reservations/{id}/complete    → Mark as completed
```

### Inventory *(ADMIN/MANAGER only)*
```
GET    /api/inventory               → All items
GET    /api/inventory/{id}          → Item by ID
GET    /api/inventory/low-stock     → Items below threshold
POST   /api/inventory               → Add item
PUT    /api/inventory/{id}          → Update item
PATCH  /api/inventory/{id}/restock?quantity=  → Add stock
PATCH  /api/inventory/{id}/deduct?quantity=   → Deduct stock
DELETE /api/inventory/{id}          → Delete item
```

---

## 🔄 Switching to MySQL (Production)

Since all DB access goes through JPA/Hibernate, switching from H2 to MySQL requires **only these changes** in `application.properties`:

```properties
# Replace these 4 lines:
spring.datasource.url=jdbc:mysql://localhost:3306/restaurant_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update

# And in pom.xml — swap H2 for MySQL driver:
# Remove:  <artifactId>h2</artifactId>
# Add:     <artifactId>mysql-connector-j</artifactId>
```

Zero Java code changes required.

---

## ☁️ AWS Deployment

```bash
# 1. Build fat JAR
mvn clean package -DskipTests

# 2. Upload to S3
aws s3 cp target/restaurant-management-system-1.0.0.jar s3://your-bucket/

# 3. Deploy to Elastic Beanstalk
aws elasticbeanstalk create-application-version \
  --application-name restaurant-rms \
  --version-label v1.0 \
  --source-bundle S3Bucket=your-bucket,S3Key=restaurant-management-system-1.0.0.jar

# 4. Set environment variables (never hardcode secrets):
#    SPRING_DATASOURCE_URL     → jdbc:mysql://<rds-endpoint>:3306/restaurant_db
#    SPRING_DATASOURCE_USERNAME → your_rds_user
#    SPRING_DATASOURCE_PASSWORD → your_rds_password
#    APP_JWT_SECRET             → your_256bit_secret_key
```

---

## 📁 Project Structure

```
src/main/java/com/restaurant/
├── RestaurantManagementApplication.java
├── config/
│   ├── DataSeeder.java          ← Auto-seeds DB on startup
│   ├── OpenApiConfig.java       ← Swagger configuration
│   └── SecurityConfig.java      ← JWT filter chain
├── controller/                  ← REST endpoints (6 controllers)
├── service/                     ← Business logic (6 services)
├── repository/                  ← JPA repositories (6 repos)
├── model/                       ← JPA entities (7 entities)
├── dto/                         ← Request/Response DTOs
├── exception/                   ← GlobalExceptionHandler
└── security/                    ← JWT utils + filter
```

---

## 🧪 Running Tests

```bash
mvn test
```