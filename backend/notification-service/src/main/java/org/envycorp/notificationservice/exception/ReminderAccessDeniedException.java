package org.envycorp.notificationservice.exception;

public class ReminderAccessDeniedException extends RuntimeException {
    public ReminderAccessDeniedException(String message) {
        super(message);
    }
}
