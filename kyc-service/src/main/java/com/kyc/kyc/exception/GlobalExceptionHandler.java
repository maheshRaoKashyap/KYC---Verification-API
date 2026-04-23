package com.kyc.kyc.exception;

import com.kyc.kyc.dto.KycDtos.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(KycException.class)
    public ResponseEntity<ApiResponse<Void>> handle(KycException ex) {
        return ResponseEntity.status(ex.getStatus())
            .body(ApiResponse.error(ex.getMessage(), ex.getErrorCode()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String,String>>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String,String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(e ->
            errors.put(((FieldError)e).getField(), e.getDefaultMessage()));
        return ResponseEntity.badRequest()
            .body(ApiResponse.<Map<String,String>>builder()
                .success(false).message("Validation failed").data(errors).errorCode("VALIDATION_ERROR").build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneral(Exception ex) {
        log.error("Unexpected error: ", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.error("An unexpected error occurred", "INTERNAL_ERROR"));
    }
}
