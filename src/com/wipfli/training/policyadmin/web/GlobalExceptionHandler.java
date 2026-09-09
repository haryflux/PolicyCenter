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
 * Assignment 11: catches business exceptions thrown by ANY controller in this
 * app and turns them into a proper HTTP status + clean JSON body, instead of
 * letting them fall through to Spring's generic 500 error page.
 *
 * This replaces what the console Main used to do with:
 *     catch (PolicyBusinessException e) { System.out.println("REFUSED [...] " + e.getMessage()); }
 * The rule itself never changes - only WHERE it's turned into user-facing
 * output. Console printed it; here we return it as JSON with a status code.
 *
 * @RestControllerAdvice applies to every @RestController in the app - one
 * central place, instead of a try/catch repeated in every endpoint.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles the "policy doesn't exist" case. PolicyNotFoundException is a
     * CHECKED exception (Assignment 6's deliberate choice), which is why every
     * PolicyController method that can hit it declares "throws PolicyNotFoundException"
     * - but Spring still routes it here to be converted into a response.
     */
    @ExceptionHandler(PolicyNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePolicyNotFound(PolicyNotFoundException ex) {
        ErrorResponse body = new ErrorResponse(ex.getPolicyNumber(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    /**
     * Handles "this policy number is already taken" specifically as a 409
     * Conflict - a more precise status than a generic 400, since the request
     * itself is well-formed, it just conflicts with existing data.
     */
    @ExceptionHandler(DuplicatePolicyNumberException.class)
    public ResponseEntity<ErrorResponse> handleDuplicatePolicyNumber(DuplicatePolicyNumberException ex) {
        ErrorResponse body = new ErrorResponse(ex.getPolicyNumber(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    /**
     * Catches every OTHER business rule violation in one place - invalid data,
     * illegal status changes, renewal refusals. All 3 share the same parent
     * (PolicyBusinessException) and need the same treatment: 400 Bad Request,
     * because in each case the client asked for something the business rules
     * don't allow right now. This is the handler that fixes Test 5's 500 error -
     * IllegalStatusChangeException ("cannot renew a policy that is EXPIRED")
     * now lands here instead of falling through to a generic error page.
     */
    @ExceptionHandler(PolicyBusinessException.class)
    public ResponseEntity<ErrorResponse> handlePolicyBusinessException(PolicyBusinessException ex) {
        ErrorResponse body = new ErrorResponse(ex.getPolicyNumber(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }
}
