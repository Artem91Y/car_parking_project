package com.example.demo.utils.models;

import com.example.demo.utils.models.CardRequest;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class PaymentRequestMethod {

    public PaymentRequestMethod(CardRequest card) {
        this.card = card;
    }

    private final String type = "bank_card";

    private CardRequest card;
}
