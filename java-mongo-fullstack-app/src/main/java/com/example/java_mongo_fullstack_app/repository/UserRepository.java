package com.example.java_mongo_fullstack_app.repository;

import com.example.java_mongo_fullstack_app.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);
}
