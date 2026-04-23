package com.kyc.auth.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class KycException extends RuntimeException {
    private final HttpStatus status;
    private final String errorCode;

    public KycException(String message, HttpStatus status, String errorCode) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }

    public static KycException notFound(String message) {
        return new KycException(message, HttpStatus.NOT_FOUND, "NOT_FOUND");
    }

    public static KycException conflict(String message) {
        return new KycException(message, HttpStatus.CONFLICT, "CONFLICT");
    }

    public static KycException badRequest(String message) {
        return new KycException(message, HttpStatus.BAD_REQUEST, "BAD_REQUEST");
    }

    public static KycException unauthorized(String message) {
        return new KycException(message, HttpStatus.UNAUTHORIZED, "UNAUTHORIZED");
    }
}
