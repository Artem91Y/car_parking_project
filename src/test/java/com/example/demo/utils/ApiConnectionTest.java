package com.example.demo.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.Response;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class ApiConnectionTest {
    @InjectMocks
    private ApiConnection apiConnection;

    @Mock
    private ObjectMapper objectMapper;


    public ApiConnectionTest() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreatePaymentPositive(){
        Payment payment = apiConnection.createPayment(111, "RUB");
        System.out.println(payment);
    }
}
