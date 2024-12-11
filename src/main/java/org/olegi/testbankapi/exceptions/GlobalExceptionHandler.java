package org.olegi.testbankapi.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<String> handleUserNotFoundException(AccountNotFoundException e) {
        log.error("Account not found: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found: " + e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("Invalid input: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid input: " + e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception e) {
        log.error("An unexpected error occurred: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + e.getMessage());
    }

    @ExceptionHandler(DepositMustBePositiveException.class)
    public ResponseEntity<String> handleDepositMustBePositiveException(DepositMustBePositiveException e) {
        log.error("Deposit error: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Deposit error: " + e.getMessage());
    }

    @ExceptionHandler(AccountAlreadyExistsException.class)
    public ResponseEntity<String> handleAccountAlreadyExistsException(AccountAlreadyExistsException e) {
        log.error("Account error: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Account error: " + e.getMessage());
    }
}
