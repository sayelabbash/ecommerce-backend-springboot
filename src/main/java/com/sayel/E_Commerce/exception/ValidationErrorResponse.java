package com.sayel.E_Commerce.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class ValidationErrorResponse {
    private String message;
    private Map<String,String> errors;
}
