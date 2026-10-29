# 🚗 Smart Parking Lot Management System Backend Service

A production-grade, enterprise-ready **RESTful Backend Service** built with **Java 21** and **Spring Boot 3.3.4** adhering strictly to **Clean Architecture**, **Defensive Programming**, and standard **REST API Design Patterns**.

---

## 📋 Table of Contents
1. [Key Features & Capabilities](#key-features--capabilities)
2. [Pricing & Fare Engine](#pricing--fare-engine)
3. [Architecture & Design Patterns](#architecture--design-patterns)
4. [Tech Stack](#tech-stack)
5. [Database Schema & Migration](#database-schema--migration)
6. [REST API Documentation & Endpoints](#rest-api-documentation--endpoints)
7. [Getting Started & Local Setup](#getting-started--local-setup)
8. [Running Tests & Edge Case Verification](#running-tests--edge-case-verification)
9. [Postman Collection & Verification](#postman-collection--verification)
10. [Submission Artifacts Checklist](#submission-artifacts-checklist)

---

## 🛠️ Key Features & Capabilities

- **Real-Time Slot Availability**: Instant query of all unoccupied parking slots with optional filtering by slot type (`STANDARD`, `COMPACT`, `LARGE`, `EV`).
- **Concurrent Slot Booking (Entry)**: Dynamic slot allocation upon vehicle arrival with strict concurrency guarantees (pessimistic row-level lock and optimistic versioning) preventing duplicate vehicle sessions and double-booking race conditions.
- **Active Parking Records**: Comprehensive tracking of all vehicles currently parked on the premises.
- **Checkout & Automated Billing**: Instant checkout releasing the allocated slot back to the available pool and computing exact charges based on the official fee rules.
- **Accrued Fare Calculation (Real-Time)**: Query real-time accrued fare for any active vehicle session at any point in time without checking out.
- **Revenue Analytics & Reporting**: Aggregate metrics calculating total revenue and detailed checkout counts for any specified date.
- **Standardized Error Handling**: Centralized `@RestControllerAdvice` mapping validation and domain errors to structured RFC-compliant JSON payloads (HTTP 400, 404, 409, 500).

---

## 💰 Pricing & Fare Engine

### Official Fee Rules:
- **Rate**: Flat charge of **₹50 per 3 hours (or fraction thereof)**.
- **Slab Formula**:
  $$\text{Duration (min)} = \text{exitTime} - \text{entryTime}$$
  $$\text{Billed Slabs} = \left\lceil \frac{\max(1, \text{Duration (min)})}{180} \right\rceil$$
  $$\text{Total Fee} = \text{Billed Slabs} \times 50.00\text{ INR}$$

### Verified Examples:
| Duration | Minutes | Billed Slabs | Fee (₹) | Explanation |
| :--- | :--- | :--- | :--- | :--- |
| **0 - 59 sec** | 0 min | 1 slab | **₹50** | Immediate checkout (fraction thereof counts as minimum 1 slab) |
| **1 min** | 1 min | 1 slab | **₹50** | First fraction |
| **2 hours 15 mins** | 135 mins | 1 slab | **₹50** | $\lceil 135 / 180 \rceil = 1 \times 50$ |
| **Exactly 3 hours** | 180 mins | 1 slab | **₹50** | Boundary check: $\lceil 180 / 180 \rceil = 1 \times 50$ |
| **3 hours 5 mins** | 185 mins | 2 slabs | **₹100** | $\lceil 185 / 180 \rceil = 2 \times 50$ |
| **Exactly 6 hours** | 360 mins | 2 slabs | **₹100** | $\lceil 360 / 180 \rceil = 2 \times 50$ |
| **6 hours 1 min** | 361 mins | 3 slabs | **₹150** | $\lceil 361 / 180 \rceil = 3 \times 50$ |
| **24 hours** | 1440 mins | 8 slabs | **₹400** | Full day parking |

---

## 🏛️ Architecture & Design Patterns

The service is structured according to **Layered Clean Architecture**:

```
com.parkinglot
├── config/        # OpenAPI / Swagger 3 Configuration
├── controller/    # REST Controllers exposing versioned /api/v1 endpoints
├── dto/           # Request & Response Data Transfer Objects (Validation-annotated)
├── entity/        # JPA Entities (ParkingSlot, VehicleBooking, Enums)
├── exception/     # Domain Exceptions & @RestControllerAdvice Handler
├── repository/    # Spring Data JPA Repositories with custom JPQL & Lock Queries
└── service/       # Business interfaces and transaction-managed implementations
    └── impl/
```

### Defensive Programming Principles Applied:
1. **Concurrency Safety**:
   - `SELECT ... FOR UPDATE` (Pessimistic write locking) on slot allocation.
   - `@Version` optimistic locking on `ParkingSlot` and `VehicleBooking` entities.
   - Database unique constraint on `slot_number` and indexed queries.
2. **Defensive Validation**:
   - Jakarta Bean Validation (`@NotBlank`, `@Pattern`, `@Positive`, `@PastOrPresent`) on all DTOs.
   - Guard against invalid timestamps (e.g. exit time preceding entry time).
   - Conflict detection: prevents vehicle from having multiple simultaneous active parking slots.
3. **Transaction Boundaries**:
   - Read-only transactions (`@Transactional(readOnly = true)`) for queries.
   - Write transactions (`@Transactional`) ensuring atomic slot update and booking creation.

---

## 💻 Tech Stack

- **Language**: Java 21 (LTS)
- **Framework**: Spring Boot 3.3.4
- **ORM / Persistence**: Spring Data JPA / Hibernate 6
- **Database**: 
  - **H2 In-Memory Database** (preconfigured default for zero-setup execution)
  - **MySQL 8.0 / PostgreSQL** (production-ready configuration included)
- **Validation**: Jakarta Bean Validation (`spring-boot-starter-validation`)
- **Documentation**: SpringDoc OpenAPI 3 / Swagger UI (`/swagger-ui.html`)
- **Build Tool**: Apache Maven 3.9+
- **Testing**: JUnit 5, Mockito, Spring Boot MockMvc

---

## 🗄️ Database Schema & Migration

Database scripts are available in multiple formats:
- Standard SQL DDL: `src/main/resources/schema.sql`
- Seed Data: `src/main/resources/data.sql`
- Flyway Migration: `src/main/resources/db/migration/V1__init_parking_lot_schema.sql`

### Entity Relationship Model:
```
+--------------------+              +--------------------+
|   parking_slots    | 1          * |  vehicle_bookings  |
+--------------------+--------------+--------------------+
| id (PK)            |              | id (PK)            |
| slot_number (UQ)   |              | vehicle_number     |
| slot_type          |              | parking_slot_id(FK)|
| status             |              | entry_time         |
| version            |              | exit_time          |
| created_at         |              | duration_minutes   |
| updated_at         |              | fee_amount         |
+--------------------+              | payment_status     |
                                    | booking_status     |
                                    | version            |
                                    | created_at         |
                                    | updated_at         |
                                    +--------------------+
```

---

## 📡 REST API Documentation & Endpoints

Base URL: `http://localhost:8080`
Interactive Swagger UI: `http://localhost:8080/swagger-ui.html`
OpenAPI Specification: `http://localhost:8080/v3/api-docs`

### 1. Parking Slots
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/v1/slots/available` | Get real-time available parking slots (optional `?slotType=EV`) |
| `GET` | `/api/v1/slots` | List all parking slots |
| `GET` | `/api/v1/slots/{id}` | Get parking slot details by ID |
| `POST` | `/api/v1/slots` | Admin: create a new parking slot |

### 2. Vehicle Booking & Entry
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/api/v1/bookings/entry` | Allocate slot upon entry (Auto or specific slot ID) |

**Sample Entry Request:**
```json
{
  "vehicleNumber": "MH12AB1234",
  "slotType": "STANDARD"
}
```

### 3. Active Records
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/v1/bookings/active` | Fetch all currently active vehicle sessions / occupied slots |
| `GET` | `/api/v1/bookings/{id}` | Fetch booking record details by ID |

### 4. Real-time Fare Calculation
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/v1/bookings/{id}/fare` | Accrued fare by booking ID (optional `?asOf=...`) |
| `GET` | `/api/v1/bookings/fare` | Accrued fare by vehicle number (`?vehicleNumber=MH12AB1234`) |

**Sample Fare Response:**
```json
{
  "success": true,
  "message": "Real-time fare calculated successfully",
  "data": {
    "bookingId": 1,
    "vehicleNumber": "MH12AB1234",
    "slotNumber": "A1",
    "entryTime": "2026-10-29T10:00:00",
    "asOfTime": "2026-10-29T13:05:00",
    "durationMinutes": 185,
    "durationFormatted": "3 hours 5 minutes",
    "billedSlabs": 2,
    "slabRate": 50.00,
    "totalFare": 100.00,
    "currency": "INR",
    "feeBreakdown": "Duration: 3 hours 5 minutes (185 min). Billed as 2 three-hour slab(s) @ ₹50.00 per slab = ₹100.00"
  }
}
```

### 5. Checkout & Billing
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/api/v1/bookings/checkout` | Process exit, calculate fee, record payment, free up slot |

**Sample Checkout Request:**
```json
{
  "bookingId": 1,
  "paymentStatus": "PAID"
}
```

### 6. Analytics & Revenue Reporting
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/v1/analytics/revenue` | Aggregate revenue report (`?date=2026-10-29`) |

**Sample Revenue Report Response:**
```json
{
  "success": true,
  "message": "Revenue report generated successfully",
  "data": {
    "reportDate": "2026-10-29",
    "totalCompletedBookings": 12,
    "totalRevenue": 950.00,
    "currency": "INR",
    "bookings": [ ... ]
  }
}
```

---

## 🚀 Getting Started & Local Setup

### Prerequisites
- JDK 21+
- Apache Maven 3.8+ (or use included `mvnw`)

### 1. Run using Default In-Memory H2 Profile (Zero configuration required)
```bash
mvn spring-boot:run
```
- The application will automatically bootstrap and seed sample slots (`A1`-`A5`, `B1`-`B4`, `EV1`-`EV2`).
- Swagger UI will be live at: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- H2 Console available at: [http://localhost:8080/h2-console](http://localhost:8080/h2-console) (JDBC URL: `jdbc:h2:mem:parkinglotdb`, User: `sa`, Password: empty)

### 2. Run with MySQL Profile
1. Ensure MySQL 8.0 is running and create the database if desired:
   ```sql
   CREATE DATABASE parking_lot_db;
   ```
2. Verify credentials in `src/main/resources/application-mysql.properties` (`root`/`root`).
3. Launch with the `mysql` profile:
   ```bash
   mvn spring-boot:run -Dspring-boot.run.profiles=mysql
   ```

---

## 🧪 Running Tests & Edge Case Verification

Run the full automated test suite validating the fee calculation engine edge cases, service layer logic, and REST controllers:

```bash
mvn test
```

### Tested Scenarios:
- ✅ Fraction within 3 hours (2 hours 15 mins -> ₹50)
- ✅ Exact 3 hours boundary (180 mins -> ₹50)
- ✅ Fraction over 3 hours (3 hours 5 mins -> ₹100)
- ✅ Just over 3 hours boundary (181 mins -> ₹100)
- ✅ Exact 6 hours boundary (360 mins -> ₹100)
- ✅ Just over 6 hours (361 mins -> ₹150)
- ✅ Immediate exit / 0 mins / 1 min edge cases (₹50)
- ✅ Defensive validation: exit timestamp preceding entry timestamp (Throws 400 Bad Request)
- ✅ Duplicate active booking conflict prevention (Throws 409 Conflict)
- ✅ Full parking lot capacity exhaustion (Throws 409 Conflict)
- ✅ Jakarta Bean Validation constraints on vehicle numbers and slot IDs (400 Bad Request)

---

## 📮 Postman Collection & Verification

Import the collection file into Postman:
`postman/Parking_Lot_Management_API.postman_collection.json`

The collection is pre-configured with:
- Environment variable `baseUrl` (`http://localhost:8080`)
- Dynamic test scripts saving `activeBookingId` between entry and checkout requests
- Positive and negative verification requests (Validations, 404 Not Found, 409 Conflicts).
