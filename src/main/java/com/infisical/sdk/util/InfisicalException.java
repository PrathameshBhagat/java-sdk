package com.infisical.sdk.util;


import com.infisical.sdk.ApiException;
import lombok.Getter;

public class InfisicalException extends Exception {
    @Getter
    private final int statusCode;

    @Getter
    private final String message;

    public InfisicalException(String message,  int statusCode) {
        super(message);  // Pass message to the parent Exception class
        this.statusCode = statusCode;
        this.message = message;
    }

    // Constructor that takes the original ApiException
    public InfisicalException(ApiException cause) {
        this.statusCode = cause.getCode();

        if (cause.getResponseBody() != null) {
            this.message = cause.getResponseBody();
        } else {
            this.message = null;
        }
    }
}