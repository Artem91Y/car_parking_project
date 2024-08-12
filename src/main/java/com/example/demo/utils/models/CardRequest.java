package com.example.demo.utils.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@AllArgsConstructor
@Setter
@Getter
@ToString
@NoArgsConstructor
public class CardRequest {
    private String number;

    @JsonProperty("expiry_year")
    private int expiryYear;

    @JsonProperty("expiry_month")
    private String expiryMonth;

    private String cardholder;

    private String csc;

    public CardRequest(String number, int expiryYear, String expiryMonth) {
        this.number = number;
        this.expiryYear = expiryYear;
        this.expiryMonth = expiryMonth;
    }
}
