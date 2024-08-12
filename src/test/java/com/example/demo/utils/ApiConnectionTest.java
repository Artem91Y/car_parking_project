package com.example.demo.utils;

import com.example.demo.utils.models.Amount;
import com.example.demo.utils.models.CardRequest;
import com.example.demo.utils.models.ConfirmationRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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
        boolean result = false;
        result = apiConnection.createPayment(new PaymentRequest(new Amount(112, "RUB"), new PaymentRequestMethod(new CardRequest("5555555555554444", 2029, "12")), new ConfirmationRequest(), true));
        System.out.println(result);
    }
}
