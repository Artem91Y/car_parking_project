package com.example.demo.controllers;

import com.example.demo.dtos.ApiKassaConnectionException;
import com.example.demo.dtos.CancellationPaymentException;
import com.example.demo.dtos.CaptureFailedException;
import com.example.demo.dtos.FailRefundPaymentException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class CustomExceptionHandler {
    @ExceptionHandler({FailRefundPaymentException.class, CancellationPaymentException.class, CaptureFailedException.class, ApiKassaConnectionException.class})
    public ResponseEntity<String> handleFailRefundPaymentException(Exception e, WebRequest webRequest) {
        System.out.println("1");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }


}
