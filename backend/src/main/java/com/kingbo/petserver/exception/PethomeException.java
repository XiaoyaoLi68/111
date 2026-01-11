package com.kingbo.petserver.exception;


public class PethomeException extends RuntimeException {
    private String message;

    public PethomeException(String message) {
        super(message);
        this.message = message;
    }

    public static void throwPetException(String message) {
        throw new PethomeException(message);
    }
}
