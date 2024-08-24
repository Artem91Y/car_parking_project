package com.example.demo.utils;

import com.example.demo.dtos.ApiKassaConnectionException;
import com.example.demo.dtos.CancellationPaymentException;
import com.example.demo.dtos.CaptureFailedException;
import com.example.demo.dtos.FailRefundPaymentException;
import com.example.demo.utils.models.Amount;
import com.example.demo.utils.models.PaymentRequest;
import com.example.demo.utils.models.RefundDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

@Component
public class ApiConnection {


    private URL baseUrl;

    @Autowired
    private ObjectMapper objectMapper;

    private final String secretKey = "test_5O4maTCzMLnqNnYz6iSZZHKm5McLYamIEAlT9jXIECI";
    private final String shopID = "417160";


    {
        try {
            baseUrl = new URL("https://api.yookassa.ru/v3/");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public UUID createPayment(PaymentRequest paymentRequest) throws CaptureFailedException, CancellationPaymentException, ApiKassaConnectionException {
        ObjectMapper objectMapper1 = new ObjectMapper();
        System.out.println(paymentRequest);
        Response response;
        OkHttpClient okHttpClient = new OkHttpClient();
        String json;
        try {
            json = objectMapper1.writeValueAsString(paymentRequest);
        } catch (JsonProcessingException e) {
            return null;
        }
        System.out.println(json);
        Request request = new Request.Builder()
                .post(RequestBody.create(MediaType.parse("APPLICATION/JSON"), json))
                .url(baseUrl + "payments")
                .addHeader("Authorization", Credentials.basic(shopID, secretKey))
                .addHeader("Idempotence-Key", UUID.randomUUID().toString())
                .build();
        try {
            response = okHttpClient.newCall(request).execute();
        } catch (IOException e) {
            throw new ApiKassaConnectionException("Connect fail");
        }
        return validation(response);

    }

    private void capturePayment(UUID id) throws IOException {
        OkHttpClient okHttpClient = new OkHttpClient();
        Response response;
        Request request = new Request.Builder()
                .method("POST", RequestBody.create(MediaType.parse("APPLICATION/JSON"), "{}"))
                .header("Content-Length", "0")
                .url(baseUrl + "payments/" + id + "/capture")
                .addHeader("Authorization", Credentials.basic(shopID, secretKey))
                .addHeader("Idempotence-Key", UUID.randomUUID().toString())
                .build();
        response = okHttpClient.newCall(request).execute();
        System.out.println(response.body().string());
    }

    private UUID validation(Response response) throws CancellationPaymentException, CaptureFailedException {
        JsonNode node = null;
        String response1 = null;
        try {
            response1 = response.body().string();
            System.out.println(response1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            node = objectMapper.readTree(response1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (node.has("status") && node.get("status").asText().equals("canceled")) {
            if (node.has("cancellation_details")) {
                throw new CancellationPaymentException(String.valueOf(node.get("cancellation_details")));
            }
        }
        if (node.has("type") && node.get("type").asText().equals("error")){
            throw new CancellationPaymentException(String.valueOf(node.get("description")));
        }
        UUID paymentId = null;
        if (node.has("id")) {
            paymentId = UUID.fromString(node.get("id").asText());
        }
        try {
            capturePayment(paymentId);
        } catch (IOException e) {
            throw new CaptureFailedException("Capture failed");
        }
        return paymentId;
    }

    public  void refund(UUID paymentId, int refundPrice) throws FailRefundPaymentException {
        ObjectMapper objectMapper1 = new ObjectMapper();
        OkHttpClient okHttpClient = new OkHttpClient();
        Response response;
        String json = null;
        try {
            json = objectMapper1.writeValueAsString(new RefundDto(new Amount(refundPrice, "RUB"), paymentId));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        Request request = new Request.Builder()
                .post(RequestBody.create(MediaType.parse("APPLICATION/JSON"), json))
                .url(baseUrl + "refunds")
                .addHeader("Authorization", Credentials.basic(shopID, secretKey))
                .addHeader("Idempotence-Key", UUID.randomUUID().toString())
                .build();
        try {
            response = okHttpClient.newCall(request).execute();
            System.out.println(response.body().string());
        } catch (IOException e) {
            throw new FailRefundPaymentException(e.getMessage());
        }
    }
}
