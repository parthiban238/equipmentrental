# PROBLEM STATEMENT

## 1. Title

**Agricultural Equipment Rental Management System**

---

## 2. Domain

**Agriculture / E-Commerce / Equipment Rental Management**

---

## 3. Who is the User?

The system consists of three major types of users:

### 3.1 Farmer

Farmers are the primary users of the system. They can search for agricultural equipment, check its availability, view equipment details, and submit rental requests based on their farming requirements.

**Responsibilities:**

* Register and log in to the system.
* Search for available agricultural equipment.
* View equipment details and rental prices.
* Check equipment availability.
* Book equipment for a required period.
* View booking status.
* View previous rental history.
* Provide ratings and reviews after using the equipment.

### 3.2 Equipment Owner

Equipment owners can list their agricultural machinery on the platform and make it available for farmers to rent.

**Responsibilities:**

* Register and log in to the system.
* Add agricultural equipment.
* Update equipment information.
* Set rental prices.
* Manage equipment availability.
* View incoming rental requests.
* Approve or reject rental requests.
* Track rental and booking history.

### 3.3 Administrator

The administrator manages and monitors the overall system.

**Responsibilities:**

* Manage farmer and equipment-owner accounts.
* Manage agricultural equipment.
* Manage equipment categories.
* Monitor rental bookings.
* Manage users and system data.
* Monitor overall system activities.

---

## 4. What Problem Are We Solving?

Agricultural machinery such as tractors, harvesters, cultivators, seeders, and other farming equipment is expensive for individual farmers to purchase and maintain. Small and medium-scale farmers may require these machines only during specific agricultural activities or seasons, making direct ownership economically difficult.

At the same time, many equipment owners have agricultural machinery that remains unused for long periods. Existing rental processes are often managed through phone calls, personal contacts, or manual records. This makes it difficult for farmers to find suitable equipment, check availability, compare rental options, and track their bookings.

Equipment owners also face difficulties in managing equipment availability, rental requests, customer details, and rental records manually.

Therefore, there is a need for a centralized digital platform that connects farmers and agricultural equipment owners and simplifies the complete equipment rental process.

The proposed system will provide a convenient, transparent, and organized platform where farmers can find and rent agricultural equipment while equipment owners can list and manage their machinery efficiently.

---

## 5. Proposed Solution

The **Agricultural Equipment Rental Management System** is a web-based application designed to provide an online platform for renting agricultural equipment.

The system will connect farmers who need agricultural machinery with equipment owners who want to rent out their unused equipment.

### The application will provide the following features:

### Farmer Module

* Farmer registration and login.
* Secure user authentication.
* View available agricultural equipment.
* Search equipment based on requirements.
* View equipment details.
* Check equipment availability.
* View rental price.
* Select rental start and end dates.
* Submit equipment rental requests.
* View booking status.
* View rental history.
* Cancel eligible rental requests.
* Provide ratings and reviews.

### Equipment Owner Module

* Owner registration and login.
* Add new agricultural equipment.
* Add equipment name, category, description, price, and availability.
* Update equipment information.
* Delete equipment listings when required.
* Manage equipment availability.
* View incoming rental requests.
* Approve or reject rental requests.
* View current and previous rentals.
* Monitor equipment usage and rental records.

### Administrator Module

* Admin login.
* Manage registered users.
* Manage equipment owners.
* Manage agricultural equipment.
* Manage equipment categories.
* View and monitor rental bookings.
* Monitor system activities.
* Manage inappropriate or invalid equipment listings.
* Maintain overall system control.

### Rental Management

The system will maintain rental records and track the complete booking lifecycle.

A rental request can have different statuses such as:

**Pending → Approved → Completed / Cancelled**

The system can also calculate the total rental amount based on the selected rental period and equipment rental rate.

---

## 6. Core Entities / Database Tables

The system will contain multiple related database tables to support the rental business logic.

### 6.1 Users

Stores information about farmers, equipment owners, and administrators.

**Main attributes:**

* User ID
* Name
* Email
* Password
* Phone Number
* Role

### 6.2 Equipment

Stores information about agricultural equipment listed by equipment owners.

**Main attributes:**

* Equipment ID
* Equipment Name
* Description
* Category
* Rental Price
* Availability Status
* Owner ID

### 6.3 Rentals / Bookings

Stores all equipment rental requests made by farmers.

**Main attributes:**

* Rental ID
* User ID
* Equipment ID
* Start Date
* End Date
* Total Amount
* Booking Status

### 6.4 Equipment Categories

Stores different categories of agricultural equipment.

**Examples:**

* Tractor
* Harvester
* Cultivator
* Seeder
* Plough
* Sprayer

### 6.5 Payments

Stores payment-related information for equipment rentals.

**Main attributes:**

* Payment ID
* Rental ID
* Amount
* Payment Date
* Payment Status
* Payment Method

### 6.6 Reviews / Ratings

Stores feedback provided by farmers after completing a rental.

**Main attributes:**

* Review ID
* User ID
* Equipment ID
* Rating
* Review Comment
* Review Date

### Main Relationships

* One **User** can create multiple **Rentals**.
* One **Equipment** can have multiple **Rental** records over time.
* One **Equipment Owner** can list multiple **Equipment** items.
* One **Equipment Category** can contain multiple **Equipment** items.
* One **Rental** can have a corresponding **Payment**.
* One **User** can provide multiple **Reviews**.
* One **Equipment** can receive multiple **Reviews**.

This provides the required relational database structure with more than five connected entities.

---

## 7. User Roles & Permissions

The system will have three major roles.

| Role                | Main Permissions                                                                                                              |
| ------------------- | ----------------------------------------------------------------------------------------------------------------------------- |
| **Farmer**          | Register, Login, Search Equipment, View Equipment, Book Equipment, View Booking Status, View Rental History, Review Equipment |
| **Equipment Owner** | Register, Login, Add Equipment, Update Equipment, Manage Availability, Approve/Reject Rentals, View Rental History            |
| **Admin**           | Manage Users, Manage Equipment, Manage Categories, Monitor Rentals, Manage System Data                                        |

### Farmer Permissions

Farmers can:

* Create an account.
* Login securely.
* Search available equipment.
* View equipment details.
* Book equipment.
* View booking status.
* View rental history.
* Provide ratings and reviews.

### Equipment Owner Permissions

Equipment owners can:

* Create an account.
* Login securely.
* Add equipment.
* Update equipment.
* Manage equipment availability.
* View rental requests.
* Approve or reject requests.
* View rental records.

### Administrator Permissions

Administrators can:

* Manage all users.
* Manage equipment.
* Manage categories.
* Monitor rentals.
* Manage system records.
* Monitor overall application activities.

---

## 8. Success Criteria

The project will be considered successful when the following conditions are achieved:

1. A farmer can successfully register and log in.
2. An equipment owner can successfully register and log in.
3. Farmers can view available agricultural equipment.
4. Farmers can search and select suitable equipment.
5. Farmers can submit rental requests.
6. Equipment owners can view incoming rental requests.
7. Equipment owners can approve or reject rental requests.
8. The system correctly maintains equipment availability.
9. The system stores rental and booking records in the database.
10. The system calculates the total rental amount based on the rental duration.
11. Farmers can view their booking status and rental history.
12. Administrators can manage users, equipment, and bookings.
13. The application provides a simple and user-friendly interface.
14. The backend and database work together correctly through REST APIs.
15. The application maintains proper relationships between the database entities.

### Primary Success Goal

**A farmer should be able to find available agricultural equipment, submit a rental request, and track the booking status through the application without depending on manual rental processes.**

---

## 9. Out of Scope

To keep the project achievable within the capstone timeline, the following features are outside the initial scope:

* Real-time GPS tracking of agricultural equipment.
* Physical transportation and delivery management.
* Insurance claim processing.
* Automated equipment maintenance.
* IoT-based equipment monitoring.
* Real-time machinery sensor integration.
* Government subsidy processing.
* Large-scale financial accounting.
* Real-money payment gateway integration in the initial MVP.
* Automatic legal contract generation.

These features may be considered as future enhancements if required.

---

## 10. Chosen Track

### Technology Track

**Java – Spring Boot**

### Technology Stack

| Layer                   | Technology                  |
| ----------------------- | --------------------------- |
| Frontend                | HTML, CSS, JavaScript       |
| Backend                 | Java Spring Boot            |
| Database                | MySQL                       |
| ORM                     | Spring Data JPA / Hibernate |
| API                     | REST API                    |
| Build Tool              | Maven                       |
| API Testing             | Postman                     |
| Testing                 | JUnit                       |
| Version Control         | Git & GitHub                |
| Development Environment | Visual Studio Code          |
| Database Management     | MySQL Workbench             |

### Backend Architecture

The application will follow a modular architecture consisting of:

**Controller → Service → Repository → Database**

This separates API handling, business logic, data access, and database operations.

---

# Project Summary

The **Agricultural Equipment Rental Management System** is a web-based rental platform that connects farmers with agricultural equipment owners. The system addresses the difficulty of accessing expensive farming machinery by providing an organized platform for equipment discovery, availability checking, rental booking, and rental management.

Farmers can find and request agricultural equipment according to their requirements, while equipment owners can list their machinery and manage rental requests. Administrators can monitor users, equipment, and bookings.

The system will use **Java Spring Boot, MySQL, REST APIs, and a web-based frontend** to provide a scalable and organized solution for agricultural equipment rental management.

The project has clear business logic, multiple user roles, more than five related database entities, and scope for future enhancements such as AI-based equipment recommendations, making it suitable for a full-stack capstone project.
