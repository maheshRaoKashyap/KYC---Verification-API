package com.kyc.kyc.exception;

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

    public static KycException notFound(String msg)  { return new KycException(msg, HttpStatus.NOT_FOUND, "NOT_FOUND"); }
    public static KycException conflict(String msg)  { return new KycException(msg, HttpStatus.CONFLICT, "CONFLICT"); }
    public static KycException badRequest(String msg){ return new KycException(msg, HttpStatus.BAD_REQUEST, "BAD_REQUEST"); }
    public static KycException forbidden(String msg) { return new KycException(msg, HttpStatus.FORBIDDEN, "FORBIDDEN"); }
}
