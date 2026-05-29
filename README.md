# Connextion Helpdesk - Backend 🚀

This is the backend implementation for **Sprint 1** of the **Proyecto Final IF0006 I 2026** course. It provides the core API endpoints and data models for clients, support users, and supervisors, adhering to the project's strict rubrics.

## 🛠️ Architecture & Technologies

* **Core Framework:** Jakarta EE 8 (using **JAX-RS / Jakarta RESTful Web Services**).
* **Package Naming:** Standardized under `com.connextion.helpdesk.*`.
* **Coding Standards:** Strict English, CamelCase naming convention, nouns for classes, verbs for methods, and full encapsulation.
* **Database Target:** SQL Server (SQL scripts included below).
* **Security & CORS:** Configured with a custom `ContainerResponseFilter` (CORS Filter) allowing all origins (`*`) to ensure smooth communication with the frontend.

---

## 📋 Features Implemented (Sprint 1)

* **CU1: Client User Registration:** Create Client users and assign their corresponding subscribed services.
* **CU2: Client Login:** Secure authentication logic for Client users.
* **CU7: Support User Registration:** Create technical support staff and supervisors, assigning their target support services.
* **CU8: Support/Supervisor Login:** Secure authentication logic differentiating roles (Supporter vs. Supervisor).

---

## 📡 API Endpoints Reference

All endpoints consume and produce purely **JSON** (`application/json`). In Jakarta EE, they are hosted under the `/resources` root path.

### Client Endpoints
| HTTP Method | Endpoint URL | Description | Input JSON Body |
| :--- | :--- | :--- | :--- |
| **POST** | `/resources/api/v1/clients/register` | Registers a client user. | `{ "name": "...", "firstSurname": "...", "secondSurname": "...", "email": "...", "password": "...", "address": "...", "phone": "...", "secondContact": "...", "services": [1, 2] }` |
| **POST** | `/resources/api/v1/clients/login` | Authenticates a client user. | `{ "email": "...", "password": "..." }` |

### Support Endpoints
| HTTP Method | Endpoint URL | Description | Input JSON Body |
| :--- | :--- | :--- | :--- |
| **POST** | `/resources/api/v1/support/register` | Registers a support user or supervisor. | `{ "name": "...", "firstSurname": "...", "secondSurname": "...", "email": "...", "password": "...", "isSupervisor": false, "services": [3, 4] }` |
| **POST** | `/resources/api/v1/support/login` | Authenticates support/supervisor users. | `{ "email": "...", "password": "..." }` |

---

## 🗄️ Database Schema (MySQL / MariaDB for XAMPP)

Run the following SQL script in phpMyAdmin or your MySQL client in XAMPP to initialize the relational database tables:

```sql
-- Create Database
CREATE DATABASE IF NOT EXISTS ConnextionDB;
USE ConnextionDB;

-- 1. Services Table
CREATE TABLE Services (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

-- Populate required services
INSERT INTO Services (name) VALUES 
('Telefonía móvil'), 
('Cable'), 
('Internet'), 
('Telefonía fija');

-- 2. Clients Table
CREATE TABLE Clients (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    first_surname VARCHAR(50) NOT NULL,
    second_surname VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    address VARCHAR(255) NULL,
    phone VARCHAR(20) NULL,
    second_contact VARCHAR(20) NULL
);

-- 3. Client_Services Table (Many-to-Many Relation)
CREATE TABLE Client_Services (
    client_id INT NOT NULL,
    service_id INT NOT NULL,
    PRIMARY KEY (client_id, service_id),
    FOREIGN KEY (client_id) REFERENCES Clients(id) ON DELETE CASCADE,
    FOREIGN KEY (service_id) REFERENCES Services(id) ON DELETE CASCADE
);

-- 4. Support_Users Table (Supporters and Supervisors)
CREATE TABLE Support_Users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    first_surname VARCHAR(50) NOT NULL,
    second_surname VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    is_supervisor BOOLEAN NOT NULL DEFAULT FALSE
);

-- 5. Support_User_Services Table (Many-to-Many Relation)
CREATE TABLE Support_User_Services (
    support_user_id INT NOT NULL,
    service_id INT NOT NULL,
    PRIMARY KEY (support_user_id, service_id),
    FOREIGN KEY (support_user_id) REFERENCES Support_Users(id) ON DELETE CASCADE,
    FOREIGN KEY (service_id) REFERENCES Services(id) ON DELETE CASCADE
);
```

---

## ⚙️ How to Build & Run locally

### Prerequisites
* JDK 8 or higher
* Apache Maven
* A Jakarta EE 8 compatible Application Server (e.g. Glassfish 5+, Payara Server 5+)

### Compilation
Build the WAR package using Maven:
```bash
mvn clean package
```

Deploy the generated `.war` file located in the `target/` directory to your Application Server console or autodeploy folder.
