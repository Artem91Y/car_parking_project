package com.example.demo.utils.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RefundDto {
    private Amount amount;

    @JsonProperty("payment_id")
    private UUID paymentId;
}
