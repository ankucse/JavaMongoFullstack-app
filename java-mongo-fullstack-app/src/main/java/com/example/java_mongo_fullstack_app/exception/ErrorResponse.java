package com.example.java_mongo_fullstack_app.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    private LocalDateTime timestamp;
    private int errorCode;
    private String message;
    private String path;
}
