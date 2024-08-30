package com.example.demo.utils;

import com.example.demo.utils.models.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import org.apache.coyote.Response;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class ApiConnectionTest {
    @InjectMocks
    private ApiConnection apiConnection;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private OkHttpClient okHttpClient;




    public ApiConnectionTest() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreatePaymentPositive() throws Exception {
//       TODO think about test cases
        UUID result = null;
        when(okHttpClient.newCall(any(Request.class)).execute()).thenReturn(new Response());
        result = apiConnection.createPayment(new PaymentRequest(new Amount(112, "RUB"), new PaymentRequestMethod(new CardRequest("5555555555554444", 2029, "12")), new ConfirmationRequest(), true));
        System.out.println(result);
    }

    @Test
    public void testRefundPositive() throws Exception {
        apiConnection.refund(UUID.fromString("2e4aeefb-000f-5000-a000-1cac1699da28"), (int)(166 * 0.5));
    }
}
