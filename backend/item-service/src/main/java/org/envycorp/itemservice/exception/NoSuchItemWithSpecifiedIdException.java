package org.envycorp.itemservice.exception;

public class NoSuchItemWithSpecifiedIdException extends RuntimeException {
    public NoSuchItemWithSpecifiedIdException(String message) {
        super(message);
    }
}
