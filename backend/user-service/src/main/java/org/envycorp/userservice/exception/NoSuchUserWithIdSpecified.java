package org.envycorp.userservice.exception;

public class NoSuchUserWithIdSpecified extends RuntimeException {
    public NoSuchUserWithIdSpecified(String message) {
        super(message);
    }
}
