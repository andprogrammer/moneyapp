package com.moneyapp.exception;

public class ResponseError {

    private String message;

    public ResponseError(String message) {
        this.message = String.format(message);
    }

    public ResponseError(Exception e) {
        this.message = e.getMessage();
    }

    public String getMessage() {
        return this.message;
    }
}
