package com.parkinglot.exception;

public class DuplicateActiveBookingException extends RuntimeException {
    public DuplicateActiveBookingException(String message) {
        super(message);
    }
}
