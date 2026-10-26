package com.wipfli.training.policyadmin.web;

import com.wipfli.training.policyadmin.dto.ErrorResponse;
import com.wipfli.training.policyadmin.exception.DuplicatePolicyNumberException;
import com.wipfli.training.policyadmin.exception.PolicyBusinessException;
import com.wipfli.training.policyadmin.exception.PolicyNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Centralized exception handler for REST APIs.
 * Maps application exceptions to appropriate HTTP
 * status codes and error response objects.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles requests for policies that do not exist.
     */

    @ExceptionHandler(PolicyNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePolicyNotFound(PolicyNotFoundException ex) {
        ErrorResponse body = new ErrorResponse(ex.getPolicyNumber(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    /**
     * Handles attempts to create duplicate policy numbers.
     */

    @ExceptionHandler(DuplicatePolicyNumberException.class)
    public ResponseEntity<ErrorResponse> handleDuplicatePolicyNumber(DuplicatePolicyNumberException ex) {
        ErrorResponse body = new ErrorResponse(ex.getPolicyNumber(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    /**
     * Handles policy validation and business rule violations.
     */

    @ExceptionHandler(PolicyBusinessException.class)
    public ResponseEntity<ErrorResponse> handlePolicyBusinessException(PolicyBusinessException ex) {
        ErrorResponse body = new ErrorResponse(ex.getPolicyNumber(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }
}
