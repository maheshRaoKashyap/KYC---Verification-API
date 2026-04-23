package com.kyc.user.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class UserException extends RuntimeException {
    private final HttpStatus status;
    private final String errorCode;

    public UserException(String message, HttpStatus status, String errorCode) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }

    public static UserException notFound(String msg)  { return new UserException(msg, HttpStatus.NOT_FOUND, "NOT_FOUND"); }
    public static UserException conflict(String msg)  { return new UserException(msg, HttpStatus.CONFLICT, "CONFLICT"); }
    public static UserException forbidden(String msg) { return new UserException(msg, HttpStatus.FORBIDDEN, "FORBIDDEN"); }
    public static UserException badRequest(String msg){ return new UserException(msg, HttpStatus.BAD_REQUEST, "BAD_REQUEST"); }
}
