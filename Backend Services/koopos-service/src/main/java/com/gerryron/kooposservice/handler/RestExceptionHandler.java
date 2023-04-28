package com.gerryron.kooposservice.handler;

import com.gerryron.kooposservice.dto.ErrorDetail;
import com.gerryron.kooposservice.dto.RestResponse;
import com.gerryron.kooposservice.enums.ApplicationCode;
import com.gerryron.kooposservice.exception.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class RestExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(RestExceptionHandler.class);

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<RestResponse<Object>> handleAuthenticationException(HttpServletRequest request, AuthenticationException e) {
        log.warn("AuthenticationException occurred:: Method= {} URL= {}  Code= {} Message= {}",
                request.getMethod(), request.getRequestURL(), e.getCode(), e.getMessage());

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(RestResponse.builder()
                        .responseStatus(e.getCode(), e.getMessage())
                        .build());
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<RestResponse<Object>> handleBadRequestException(HttpServletRequest request, BadRequestException e) {
        log.warn("BadRequestException occurred:: Method= {} URL= {}  Code= {} Message= {}",
                request.getMethod(), request.getRequestURL(), e.getCode(), e.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(RestResponse.builder()
                        .responseStatus(e.getCode(), e.getMessage())
                        .errorDetails(e.getErrorDetails())
                        .build());
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<RestResponse<Object>> handleNotFoundException(HttpServletRequest request, NotFoundException e) {
        log.warn("NotFoundException occurred:: Method= {} URL= {}  Code= {} Message= {}",
                request.getMethod(), request.getRequestURL(), e.getCode(), e.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(RestResponse.builder()
                        .responseStatus(e.getCode(), e.getMessage())
                        .errorDetails(e.getErrorDetails())
                        .build());
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<RestResponse<Object>> handleConflictException(HttpServletRequest request, ConflictException e) {
        log.warn("ConflictException occurred:: Method= {} URL= {} Code= {} Message= {}",
                request.getMethod(), request.getRequestURL(), e.getCode(), e.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(RestResponse.builder()
                        .responseStatus(e.getCode(), e.getMessage())
                        .errorDetails(e.getErrorDetails())
                        .build());
    }

    @ExceptionHandler(KooposException.class)
    public ResponseEntity<RestResponse<Object>> handleKooposException(HttpServletRequest request, KooposException e) {
        log.warn("KooposException occurred:: Method= {} URL= {} Code= {} Message= {}",
                request.getMethod(), request.getRequestURL(), e.getCode(), e.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(RestResponse.builder()
                        .responseStatus(e.getCode(), e.getMessage())
                        .build());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RestResponse<Object>> handleMethodArgumentNotValidException(
            HttpServletRequest request,
            MethodArgumentNotValidException e) {
        log.warn("MethodArgumentNotValidException occurred:: Method: {} URL= {} message= {}", request.getMethod(),
                request.getRequestURL(), e.getMessage());

        List<ErrorDetail> errorDetails = e.getFieldErrors().stream()
                .map(error -> ErrorDetail.builder()
                        .object(error.getObjectName())
                        .field(error.getField())
                        .message(error.getDefaultMessage())
                        .build()
                ).collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(RestResponse.builder()
                        .responseStatus(ApplicationCode.INVALID_PARAMETER)
                        .errorDetails(errorDetails)
                        .build());
    }
}
