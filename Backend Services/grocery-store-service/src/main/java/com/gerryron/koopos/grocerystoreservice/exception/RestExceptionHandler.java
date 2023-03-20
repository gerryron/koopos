package com.gerryron.koopos.grocerystoreservice.exception;

import com.gerryron.koopos.grocerystoreservice.dto.ResponseStatus;
import com.gerryron.koopos.grocerystoreservice.dto.RestResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@ControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(KooposException.class)
    public ResponseEntity<RestResponse<Object>> handleKooposException(HttpServletRequest request, KooposException e) {
        log.warn("KooposException occurred::{}-{} URL= {}", e.getCode(), e.getMessage(), request.getRequestURL());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(RestResponse.builder()
                        .responseStatus(new ResponseStatus(e.getCode(), e.getMessage()))
                        .build());
    }

}
