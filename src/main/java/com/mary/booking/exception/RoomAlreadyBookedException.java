package com.mary.booking.exception;

public class RoomAlreadyBookedException extends RuntimeException{
    public RoomAlreadyBookedException(String message) {
        super(message);
    }
}
