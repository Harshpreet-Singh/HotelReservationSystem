<p align="center">

<img src="https://img.shields.io/badge/Java-OOP-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java" />
<img src="https://img.shields.io/badge/Swing-GUI-2563EB?style=for-the-badge" alt="Swing" />
<img src="https://img.shields.io/badge/JDBC-Database-16A34A?style=for-the-badge&logo=mysql&logoColor=white" alt="JDBC" />
<img src="https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white" alt="MySQL" />
<img src="https://img.shields.io/badge/Multithreading-Auto%20Release-7C3AED?style=for-the-badge" alt="Threads" />
<img src="https://img.shields.io/badge/Exception%20Handling-Custom-DC2626?style=for-the-badge" alt="Exceptions" />

</p>

<p align="center">

<img src="https://img.shields.io/badge/Course-CSE2003%20OOP%20using%20JAVA-0F766E?style=for-the-badge" alt="Course" />
<img src="https://img.shields.io/badge/Semester-3%20%7C%20B.Tech%20CSE-orange?style=for-the-badge" alt="Semester" />
<img src="https://img.shields.io/badge/Status-In%20Development-orange?style=for-the-badge" alt="Status" />
<img src="https://img.shields.io/badge/Type-Mini%20Project-7C3AED?style=for-the-badge" alt="Mini Project" />

</p>

<h1 align="center"> Hotel Room Reservation System</h1>

<p align="center">
A complete Java + MySQL + Swing application to manage hotel room availability, customer bookings, check-in/check-out, bill generation with tax, and booking history — built to demonstrate all 5 units of the CSE2003 OOP syllabus.
</p>

---

## Table of Contents

- [About the Project](#-about-the-project)
- [Features](#-features)
- [Syllabus Concepts Applied](#-syllabus-concepts-applied)
- [Tech Stack](#-tech-stack)
- [Project Structure](#-project-structure)
- [Database Schema](#-database-schema)
- [Setup Instructions](#-setup-instructions)
- [How to Run](#-how-to-run)
- [Sample Workflow](#-sample-workflow)
- [Team Members](#-team-members)
- [Troubleshooting](#-troubleshooting)

---

## About the Project

The **Hotel Room Reservation System** is a desktop application developed as part of the **CSE2003 – Object Oriented Programming using Java** mini-project for **B.Tech CSE, Semester 3**.

It simulates a real-world hotel front-desk workflow: checking room availability, registering customers, creating bookings, auto-releasing expired reservations in the background, checking guests out, and generating a tax-inclusive bill using a MySQL stored procedure.

The project is intentionally designed to **touch every unit of the OOP syllabus** — from basic classes and `Math`/`StringBuffer` (Unit 1) → inheritance & interfaces (Unit 2) → custom exceptions & threads (Unit 3) → JDBC with `PreparedStatement`/`CallableStatement` (Unit 4) → Swing GUI with event listeners (Unit 5).

---

## Features

- **Check room availability** — dynamically filters only rooms marked free
- **Customer registration** — saves name, phone, email
- **Book a room** — with check-in / check-out dates
- **Occupied room protection** — throws custom `RoomOccupiedException`
- **Auto-release thread** — frees rooms whose checkout date has passed (background daemon)
- **Bill generation with 18% GST** — computed via a MySQL stored procedure
- **Booking history** — full list of all past and active bookings
- **Swing GUI** — clean `FlowLayout` + `BorderLayout` interface with event listeners
- **Polymorphic tariffs** — Deluxe/Suite rooms override `calculateTariff()` for dynamic pricing

---

## Syllabus Concepts Applied

| Unit | Concept | Where It's Used |
| :---: | :--- | :--- |
| **1** | Classes, Objects, `Math`, `StringBuffer` | `Room`, `Customer`, `Booking` classes; `Math.round()` in bill; `StringBuffer` for bill text |
| **2** | Inheritance, Interface, Polymorphism | `DeluxeRoom` & `SuiteRoom extends Room`; `Billable` interface; overridden `calculateTariff()` |
| **3** | Custom Exception, Multithreading | `RoomOccupiedException`; `AutoReleaseThread` for expired bookings |
| **4** | JDBC — `PreparedStatement`, `CallableStatement` | `BookingService` — all DB operations; `generate_bill` stored procedure |
| **5** | Swing, Layouts, Event Listeners | `HotelGUI` — `JLabel`, `JTextField`, `JButton`, `FlowLayout`, `BorderLayout`, `ActionListener` |

---

## Tech Stack

| Layer | Technology |
| :--- | :--- |
| **Language** | Java 17+ |
| **GUI** | Java Swing (`javax.swing.*`, `java.awt.*`) |
| **Database** | MySQL 8.0 |
| **DB Driver** | MySQL Connector/J 8.x |
| **Concurrency** | `java.lang.Thread` |
| **Build Tool** | Any (Eclipse / IntelliJ / VS Code / `javac`) |

---

## Project Structure

```
HotelReservationSystem/
│
├── README.md
│
├── .gitignore
│
├── src/
│   ├── db/
│   │   └── DBConnection.java
│   ├── model/
│   │   ├── Room.java
│   │   ├── DeluxeRoom.java
│   │   ├── SuiteRoom.java
│   │   ├── Customer.java
│   │   └── Booking.java
│   ├── interfaces/
│   │   └── Billable.java
│   ├── exceptions/
│   │   └── RoomOccupiedException.java
│   ├── service/
│   │   ├── BookingService.java
│   │   └── AutoReleaseThread.java
│   ├── ui/
│   │   └── HotelGUI.java
│   └── Main.java
└── sql/
    └── hotel_db.sql
```

---

## Database Schema

**Database name:** `hotel_db`

### Tables

| Table | Columns |
| :--- | :--- |
| **`rooms`** | `room_id` (PK), `room_number`, `room_type`, `base_tariff`, `is_available` |
| **`customers`** | `customer_id` (PK), `name`, `phone` (unique), `email` |
| **`bookings`** | `booking_id` (PK), `customer_id` (FK), `room_id` (FK), `check_in`, `check_out`, `total_bill`, `status` |

### Stored Procedure

```sql
generate_bill(IN b_id INT, OUT final_bill DOUBLE)
```

`Calculates tariff × days + 18% GST, updates bookings.total_bill, and returns the final amount — invoked via CallableStatement.`

Sample Seed Data
5 rooms are auto-inserted:

- 101, 102 → Standard @ ₹1500/day

- 201, 202 → Deluxe @ ₹2500/day

- 301 → Suite @ ₹5000/day

---

# Setup Instructions

## Prerequisites

Install the following tools before starting:

| Tool | Version | Download |
| :--- | :--- | :--- |
| **JDK** | 17 or higher | [adoptium.net](https://adoptium.net) |
| **MySQL Server** | 8.0+ | [dev.mysql.com](https://dev.mysql.com) |
| **IDE** | Any | IntelliJ IDEA / Eclipse / VS Code |

---

## Clone / Download the Project

Clone the repository using Git:

```bash
git clone https://Harshpreet-Singh/HotelReservationSystem
cd HotelReservationSystem
```

> *Note: If you don't have Git installed, simply download the project ZIP archive and extract it.*

---

## Set Up the Database

### Option A — MySQL Workbench / phpMyAdmin
1. Open `sql/hotel_db.sql`.
2. Run the entire script. It will automatically:
   * Create the `hotel_db` database
   * Create all 3 required tables
   * Create the `generate_bill` stored procedure
   * Insert 5 sample rooms

### Option B — Command Line
Run the SQL script directly from your terminal:

```bash
mysql -u root -p < sql/hotel_db.sql
```

### Verify Setup
Execute the following query in your MySQL client to confirm the sample data was loaded:

```sql
USE hotel_db;
SELECT * FROM rooms;
```

---

## Add MySQL Connector/J to the Project

Download the JDBC driver JAR (`mysql-connector-j-8.x.x.jar`) from [Maven Central](https://central.sonatype.com/), then add it to your project based on your environment:

| IDE / Method | How to Add |
| :--- | :--- |
| **IntelliJ IDEA** | `File` → `Project Structure` → `Libraries` → `+` → `Java` → select the JAR |
| **Eclipse** | Right-click project → `Build Path` → `Configure Build Path` → `Libraries` → `Add External JARs` |
| **VS Code** | Place the JAR in a `lib/` folder → update `.classpath` or `settings.json` |
| **Manual (CLI)** | Place the JAR in a `lib/` folder → compile with `javac -cp "lib/*" ...` |

---

## Configure Database Credentials

Open `src/db/DBConnection.java` and update the connection constants with your local MySQL credentials:

```java
private static final String URL  = "jdbc:mysql://localhost:3306/hotel_db";
private static final String USER = "root";              // your MySQL user
private static final String PASS = "your_password";     // your MySQL password
```

> **Security Warning:** Do not commit your real password to GitHub. Before submitting or pushing your code, replace it with `"root"` or use environment variables.

---

## How to Run

### From an IDE (Easiest)
1. Set `Main.java` as the entry point in your run configuration.
2. Click **Run**.

### From the Command Line

```bash
cd HotelReservationSystem

# Compile
javac -cp "lib/mysql-connector-j-8.3.0.jar;lib/jcalendar-1.4.jar" -d out src/db/*.java src/model/*.java src/interfaces/*.java src/exceptions/*.java src/service/*.java src/ui/*.java src/Main.java

# Run
java -cp "out;lib/mysql-connector-j-8.3.0.jar;lib/jcalendar-1.4.jar" Main 
```

> *Note: On Windows, use a semicolon (`;`) instead of a colon (`:`) in the classpath (e.g., `"out;lib/mysql-connector-j-8.3.0.jar"`).*

### What You'll See
A Java Swing window titled **" Hotel Room Reservation System"** containing:
* **Input fields:** Name, Phone, Email, Room ID, Check-in, Check-out
* **Buttons:** Show Available, Book Room, History, Check Out

---

## Sample Workflow

Follow this end-to-end flow to test and demonstrate the application:

| Step | Action | Expected Result |
| :---: | :--- | :--- |
| **1** | Click **Show Available** | Lists rooms `101`, `102`, `201`, `202`, `301` |
| **2** | Fill in: `Name=Rahul`, `Phone=9999999999`, `Room ID=201`, `In=2025-01-10`, `Out=2025-01-12` → click **Book Room** | `Booking ID = 1` |
| **3** | Click **Show Available** again | Room `201` is no longer listed |
| **4** | Try booking Room `201` again | `RoomOccupiedException` alert |
| **5** | Click **History** | Shows Booking `#1` with status `BOOKED` |
| **6** | Enter `Booking ID=1` → click **Check Out** | Bill popup: `₹7000` (`2500 × 2 days + 18% GST`) |
| **7** | Click **Show Available** again | Room `201` is back |

