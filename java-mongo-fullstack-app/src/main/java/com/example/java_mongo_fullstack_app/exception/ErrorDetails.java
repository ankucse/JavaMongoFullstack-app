package com.example.java_mongo_fullstack_app.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorDetails {
    private Date timestamp;
    private String errorCode;
    private String message;
    private String path;
}
