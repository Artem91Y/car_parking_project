package com.example.demo.dtos;

public class FailRefundPaymentException extends ErrorException{
    public FailRefundPaymentException(String message) {
        super(message);
    }
}
