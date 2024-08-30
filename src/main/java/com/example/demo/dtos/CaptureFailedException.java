package com.example.demo.dtos;

public class CaptureFailedException extends ErrorException{
    public CaptureFailedException(String message) {
        super(message);
    }
}
