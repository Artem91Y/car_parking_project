package com.example.demo.utils.models;

import com.example.demo.utils.models.Amount;
import com.example.demo.utils.models.ConfirmationRequest;
import com.example.demo.utils.models.PaymentRequestMethod;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@AllArgsConstructor
@Setter
@Getter
@ToString
@Builder
public class PaymentRequest {
    private Amount amount;

    @JsonProperty("payment_method_data")
    private PaymentRequestMethod paymentMethod;

    private ConfirmationRequest confirmation;

    private boolean refundable;


}


