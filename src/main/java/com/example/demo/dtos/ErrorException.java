package com.example.demo.dtos;

public class ErrorException extends RuntimeException{
    public ErrorException(String message) {
        super(message);
    }
}
