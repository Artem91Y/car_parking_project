package com.example.demo.utils;

import com.example.demo.utils.models.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.*;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
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
        UUID uuid = UUID.randomUUID();
        Response response = mock(Response.class);
        ResponseBody responseBody = mock(ResponseBody.class);
        Call call = mock(Call.class);
        when(objectMapper.writeValueAsString(anyString())).thenReturn("{}");
        when(okHttpClient.newCall(any())).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.body()).thenReturn(responseBody);
        when(responseBody.string()).thenReturn(uuid.toString());
        UUID result = apiConnection.createPayment(new PaymentRequest(new Amount(112, "RUB"), new PaymentRequestMethod(new CardRequest("5555555555554444", 2029, "12")), new ConfirmationRequest(), true));
        assertNotNull(result);
        assertEquals(result, uuid);
    }

    @Test
    public void testRefundPositive() throws Exception {
        apiConnection.refund(UUID.fromString("2e4aeefb-000f-5000-a000-1cac1699da28"), (int)(166 * 0.5));
    }

    @Test
    public void testRefundNegative(){
        when(okHttpClient.newCall(any(Request.class))).thenThrow(new IOException("Http request fail"));
        assertThrows(IOException.class, () -> apiConnection.refund(UUID.fromString("2e4aeefb-000f-5000-a000-1cac1699da28"), (int)(166 * 0.5)), "Http request fail");
    }
}
