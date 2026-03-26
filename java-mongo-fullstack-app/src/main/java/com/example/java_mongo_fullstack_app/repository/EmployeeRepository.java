package com.example.java_mongo_fullstack_app.repository;

import com.example.java_mongo_fullstack_app.model.Employee;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends MongoRepository<Employee, String> {
}
