package com.example.demo.utils;

import com.example.demo.dtos.*;
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

    private final URL baseUrl;
    private final ObjectMapper objectMapper;
    private final String secretKey = "test_5O4maTCzMLnqNnYz6iSZZHKm5McLYamIEAlT9jXIECI";
    private final String shopID = "417160";
    private final OkHttpClient okHttpClient;

    {
        try {
            baseUrl = new URL("https://api.yookassa.ru/v3/");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public ApiConnection(ObjectMapper objectMapper, OkHttpClient okHttpClient) {
        this.objectMapper = objectMapper;
        this.okHttpClient = okHttpClient;
    }

    public UUID createPayment(PaymentRequest paymentRequest) throws CaptureFailedException, CancellationPaymentException, ApiKassaConnectionException {
        System.out.println(paymentRequest);
        Response response;
        String json;
        try {
            json = objectMapper.writeValueAsString(paymentRequest);
        } catch (JsonProcessingException e) {
            throw new ErrorException("Bad payment request");
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
        UUID paymentId = null;
        JsonNode node;
        String createResponse;
        try {
            createResponse = response.body().string();
            System.out.println(createResponse);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            node = objectMapper.readTree(createResponse);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        checkCancelled(node);
        checkError(node);
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

    public void refund(UUID paymentId, int refundPrice) throws FailRefundPaymentException {
        Response response;
        String json;
        try {
            json = objectMapper.writeValueAsString(new RefundDto(new Amount(refundPrice, "RUB"), paymentId));
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

    public void checkCancelled(JsonNode node){
        if (node.has("status") && node.get("status").asText().equals("canceled") && node.has("cancellation_details")) { // case 1
            throw new CancellationPaymentException(String.valueOf(node.get("cancellation_details")));
        }
    }

    public void checkError(JsonNode node){
        if (node.has("type") && node.get("type").asText().equals("error")) { // case 2
            throw new CancellationPaymentException(String.valueOf(node.get("description")));
        }
    }
}
