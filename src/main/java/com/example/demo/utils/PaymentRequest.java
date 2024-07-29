package com.example.demo.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

@AllArgsConstructor
@Setter
@Getter
public class PaymentRequest {
    private Amount amount;

    @JsonProperty("payment_method_data")
    private PaymentRequestMethod paymentMethod;

    private ConfirmationRequest confirmation;
}
@AllArgsConstructor
@Setter
@Getter
@NoArgsConstructor
class ConfirmationRequest{
    private String type = "redirect";
    private String locale = "ru_RU";
    private boolean enforce = false;
    @JsonProperty("return_url")
    private URL returnUrl;

    {
        try {
            returnUrl = new URL("https://www.google.com/");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
class PaymentRequestMethod{

    public PaymentRequestMethod(CardRequest card) {
        this.card = card;
    }

    private String type = "bank_card";

    private CardRequest card;
}

@AllArgsConstructor
@Setter
@Getter
class CardRequest{
    private String number;

    @JsonProperty("expiry_year")
    private int expiryYear;

    @JsonProperty("expiry_month")
    private int expiryMonth;

    private String cardholder;

    private String csc;

    public CardRequest(String number, int expiryYear, int expiryMonth) {
        this.number = number;
        this.expiryYear = expiryYear;
        this.expiryMonth = expiryMonth;
    }
}
