package com.gerryron.koopos.usermanagementservice.exception;

import com.gerryron.koopos.usermanagementservice.shared.ApplicationCode;
import com.gerryron.koopos.usermanagementservice.shared.dto.ErrorDetail;
import com.gerryron.koopos.usermanagementservice.shared.dto.ResponseStatus;
import com.gerryron.koopos.usermanagementservice.shared.response.RestResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class RestExceptionHandler {
    public static final Logger log = LoggerFactory.getLogger(RestExceptionHandler.class);

    @ExceptionHandler(KooposException.class)
    public ResponseEntity<RestResponse<Object>> handleKooposException(HttpServletRequest request, KooposException e) {
        log.warn("KooposException occurred:: Method= {} URL= {}  Code= {} Message= {}",
                request.getMethod(), request.getRequestURL(), e.getCode(), e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(RestResponse.builder()
                        .responseStatus(new ResponseStatus(e.getCode(), e.getMessage()))
                        .build());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RestResponse<Object>> handleMethodArgumentNotValidException(
            HttpServletRequest request,
            MethodArgumentNotValidException e) {
        log.warn("MethodArgumentNotValidException occurred:: Method: {} URL= {} message= {}", request.getMethod(),
                request.getRequestURL(), e.getMessage());
        List<ErrorDetail> errorDetails = new ArrayList<>();
        for (FieldError fieldError : e.getFieldErrors()) {
            errorDetails.add(ErrorDetail.builder()
                    .object(fieldError.getObjectName())
                    .field(fieldError.getField())
                    .message(fieldError.getDefaultMessage())
                    .build());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(RestResponse.builder()
                        .responseStatus(new ResponseStatus(ApplicationCode.VALIDATION_ERROR))
                        .errorDetails(errorDetails)
                        .build());
    }
}
