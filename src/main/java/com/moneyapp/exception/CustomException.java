package com.moneyapp.exception;

public class CustomException extends Exception {

    public CustomException(String message) {
        super(message);
    }

    public CustomException(String message, Throwable exception) {
        super(message, exception);
    }
}
