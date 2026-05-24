package com.mary.booking.exception;

public class UnauthorizedBookingAccessException extends RuntimeException{
    public UnauthorizedBookingAccessException(String message) {
        super(message);
    }
}
