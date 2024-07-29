package com.example.demo.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class ApiConnection {

    private URL baseUrl;

    @Autowired
    private ObjectMapper objectMapper;

    private String secretKey = "test_5O4maTCzMLnqNnYz6iSZZHKm5McLYamIEAlT9jXIECI";
    private String shopID = "417160";


    {
        try {
            baseUrl = new URL("https://api.yookassa.ru/v3/");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public Payment createPayment(int money, String currency) {
        ObjectMapper objectMapper1 = new ObjectMapper();
        Response response;
        OkHttpClient okHttpClient = new OkHttpClient();
        String json;
        try {
            json = objectMapper1.writeValueAsString(new PaymentRequest(new Amount(money, currency), new PaymentRequestMethod(new CardRequest("5105105105105100", 2029, 12)), new ConfirmationRequest()));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        System.out.println(json);
        Request request = new Request.Builder()
                .post(RequestBody.create(MediaType.parse("APPLICATION/JSON"), json))
                .url(baseUrl + "payments")
                .addHeader("Authorization", Credentials.basic(shopID, secretKey))
                .addHeader("Idempotence-Key", "unique-key-12379")
                .build();
        try {
            response = okHttpClient.newCall(request).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            System.out.println(response.body().string());
            return objectMapper1.readValue(response.body().string(), Payment.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
