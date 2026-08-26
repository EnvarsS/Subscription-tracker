package org.envycorp.notificationservice.controller;

import org.envycorp.notificationservice.exception.ReminderAccessDenied;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ServiceExceptionHandler {
    @ExceptionHandler(ReminderAccessDenied.class)
    public ResponseEntity<String> handleItemsExceptions(Exception ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }
}
