package com.example.demo.utils;

import com.example.demo.utils.models.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;

public class ApiConnectionTest {
    @InjectMocks
    private ApiConnection apiConnection;

    @Mock
    private ObjectMapper objectMapper;


    public ApiConnectionTest() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreatePaymentPositive() throws Exception {
        UUID result = null;
        result = apiConnection.createPayment(new PaymentRequest(new Amount(112, "RUB"), new PaymentRequestMethod(new CardRequest("5555555555554444", 2029, "12")), new ConfirmationRequest(), true));
        System.out.println(result);
    }

    @Test
    public void testRefundPositive() throws Exception {
        apiConnection.refund(UUID.fromString("2e4aeefb-000f-5000-a000-1cac1699da28"), (int)(166 * 0.5));
    }
}
