package com.example.demo.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@Setter
@Getter
class PaymentMethod {

    private String type = "bank_card";

    private UUID id;

    private boolean saved;

    private String title;

    private Card card;
}
