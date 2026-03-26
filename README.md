# 🌟 Employee Management System (Full Stack CRUD)

> 🚀 A production-ready **Full Stack CRUD Application** built using **Java 26 + Spring Boot + MongoDB + Vaadin**

---

## 📌 **Table of Contents**

- ✨ Overview  
- 🧰 Tech Stack  
- 📂 Project Structure  
- ⚙️ Setup & Installation  
- 🚀 Running the Application  
- 📡 API Endpoints  
- 🎨 UI Features (Vaadin)  
- 📊 Logging Strategy  
- ❗ Exception Handling  
- 🔍 Validation  
- 🧪 Testing  
- 📮 Postman Collection  
- 🤝 Contributing  
- 📜 License  

---

## ✨ **Overview**

This project is a **Full Stack Employee Management System** that allows users to:

✅ Create Employees  
✅ Read Employee Data  
✅ Update Employee Details  
✅ Delete Employees  

It follows **clean architecture principles**, includes **robust logging**, and ensures **proper error handling**.

---

## 🧰 **Tech Stack**

### 🔙 Backend
- ☕ Java 26  
- 🌱 Spring Boot  
- 🍃 Spring Data MongoDB  
- 📦 Gradle (Groovy DSL)  
- 🧾 Lombok  
- 📜 SLF4J + Logback  

### 🎨 Frontend
- ⚡ Vaadin (Modern UI Framework)

### 🗄️ Database
- 🍀 MongoDB (via MongoDB Compass)

---


---

## ⚙️ **Setup & Installation**

### 🔧 Prerequisites

Make sure you have installed:

- ✅ Java 26  
- ✅ Gradle  
- ✅ MongoDB (running locally)  
- ✅ MongoDB Compass  

---

### 📥 Clone the Repository

```bash
git clone https://github.com/your-username/employee-management.git
cd employee-management

spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/employee_db

./gradlew bootRun

| Method | Endpoint        | Description        |
| ------ | --------------- | ------------------ |
| POST   | /employees      | Create Employee    |
| GET    | /employees      | Get All Employees  |
| GET    | /employees/{id} | Get Employee by ID |
| PUT    | /employees/{id} | Update Employee    |
| DELETE | /employees/{id} | Delete Employee    |



