package com.gerryron.koopos.grocerystoreservice.exception;

import com.gerryron.koopos.grocerystoreservice.dto.ErrorDetail;
import com.gerryron.koopos.grocerystoreservice.dto.ResponseStatus;
import com.gerryron.koopos.grocerystoreservice.dto.RestResponse;
import com.gerryron.koopos.grocerystoreservice.shared.ApplicationCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@ControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(KooposException.class)
    public ResponseEntity<RestResponse<Object>> handleKooposException(HttpServletRequest request, KooposException e) {
        log.warn("KooposException occurred:: Method= {} URL= {} message= {}-{}", request.getMethod(), request.getRequestURL(),
                e.getCode(), e.getMessage());
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
