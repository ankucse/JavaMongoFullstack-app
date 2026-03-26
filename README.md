# Enterprise Employee Management System

This is a production-ready, full-stack CRUD application for managing employee data, built with a modern Java and Vaadin tech stack. It has been enhanced with enterprise-level features to serve as a realistic Human Resources management tool.

---

## 🧩 Tech Stack

### **Backend**
*   **Language:** Java 21
*   **Framework:** Spring Boot 3.2.0
*   **Database:** MongoDB
*   **Data Access:** Spring Data MongoDB
*   **Build Tool:** Gradle (Groovy DSL)
*   **Logging:** SLF4J + Logback
*   **Boilerplate Reduction:** Lombok

### **Frontend**
*   **Framework:** Vaadin 24.3.0
*   **UI Integration:** Vaadin UI directly consumes the backend REST APIs.

---

## 🚀 Features

### **Core CRUD Operations**
*   **Create, Read, Update, and Delete (CRUD)** functionality for employee records.

### **Enhanced Employee Model**
The `Employee` entity includes comprehensive, enterprise-level fields:
*   **Basic Information:** First Name, Last Name, Email (unique), Phone Number.
*   **Professional Details:** Department, Designation, Role, Employment Type (Enum), Date of Joining, Experience.
*   **Compensation:** Salary, Bonus, and Currency.
*   **Status & Metadata:** Employee Status (Enum), Manager Name, Work Location, and automatically audited `createdAt` and `updatedAt` timestamps.

### **Advanced API Functionality**
*   **Search:** Find employees by their first or last name.
*   **Filtering:** Filter the employee list by `department`, `status`, or `employmentType`.

### **Vaadin UI Enhancements**
*   **Categorized Forms:** Employee data is organized into "Personal," "Job," and "Salary" sections for a cleaner user experience.
*   **Rich UI Components:** Utilizes `ComboBox` for enums (like Status and Employment Type) and a `DatePicker` for the joining date.
*   **Visual Status Badges:** The main grid displays employee status with colored badges for better readability:
    *   <span style="color:green;">**ACTIVE**</span>
    *   <span style="color:red;">**INACTIVE**</span>
    *   <span style="color:orange;">**ON_LEAVE**</span>

### **System Quality**
*   **Global Exception Handling:** A centralized `@ControllerAdvice` handles all exceptions, including custom ones like `ResourceNotFoundException` and `ValidationException`, returning structured error responses.
*   **Robust Logging:** Comprehensive SLF4J logging is implemented across all service and controller methods for better traceability.
*   **Validation:** Backend validation is enforced using `@Valid` annotations, with frontend feedback provided through notifications.

---

## 📦 Getting Started

### **Prerequisites**
*   **Java 21** (or a compatible JDK)
*   **MongoDB Compass** (or any MongoDB instance) running on `localhost:27017`.
*   **Gradle**

### **Running the Application**
1.  **Clone the repository.**
2.  **Start MongoDB:** Ensure your MongoDB instance is running.
3.  **Run the Spring Boot application:**
    ```bash
    ./gradlew bootRun
    ```
4.  **Access the UI:** Open your web browser and navigate to `http://localhost:8080`.

---

## 🧪 API Testing with Postman / SoapUI

You can test the REST APIs using any API client.

*   **Base URL:** `http://localhost:8080/employees`

### **API Endpoints**

| Method | Endpoint | Description |
| --- | --- | --- |
| `POST` | `/` | Create a new employee. |
| `GET` | `/` | Retrieve all employees. |
| `GET` | `/{id}` | Get a specific employee by their ID. |
| `PUT` | `/{id}` | Update an existing employee. |
| `DELETE` | `/{id}` | Delete an employee. |
| `GET` | `/search?name={name}` | Search for employees by first or last name. |
| `GET` | `/filter/department?department={dept}` | Filter employees by department. |
| `GET` | `/filter/status?status={status}` | Filter employees by status (e.g., `ACTIVE`). |
| `GET` | `/filter/type?type={type}` | Filter employees by employment type (e.g., `FULL_TIME`). |

### **Sample `POST` Payload**
```json
{
    "firstName": "Ankit",
    "lastName": "Kumar",
    "email": "ankit.k@example.com",
    "phoneNumber": "9876543210",
    "department": "Technology",
    "designation": "Lead Engineer",
    "role": "Backend Developer",
    "employmentType": "FULL_TIME",
    "dateOfJoining": "2022-08-15",
    "experienceYears": 5,
    "salary": 120000.0,
    "bonus": 15000.0,
    "currency": "INR",
    "status": "ACTIVE",
    "managerName": "S. Gupta",
    "workLocation": "Remote"
}
```
