package com.example.demo.utils.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@ToString
@Builder
public class Payment {

    public Payment(UUID id, StatusOfPayment status, Amount incomeAmount, Recipient recipient, LocalDateTime createdAt, boolean test, boolean refundable, boolean paid) {
        this.id = id;
        this.status = status;
        this.incomeAmount = incomeAmount;
        this.recipient = recipient;
        this.createdAt = createdAt;
        this.test = test;
        this.refundable = refundable;
        this.paid = paid;
    }


    private Amount amount;

    private UUID id;

    private StatusOfPayment status;

    @JsonProperty(value = "income_amount")
    private Amount incomeAmount;

    private String description;

    private Recipient recipient;

    @JsonProperty("captured_at")
    private LocalDateTime capturedAt;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("expires_at")
    private LocalDateTime expiresAt;

    private Confirmation confirmation;

    private boolean test;

    private boolean refundable;

    @JsonProperty("refunded_amount")
    private int refundedAmount;

    private boolean paid;

    @JsonProperty("receipt_registration")
    private ReceiptRegistration receiptRegistration;

    private Map<String, String> metadata;

    @JsonProperty("cancellation_details")
    private CancellationDetails cancellationDetails;

    @JsonProperty("authorization_details")
    private AuthorizationDetails authorizationDetails;

    private List<Transfer> transfers;

    private Deal deal;

    @JsonProperty("merchant_customer_id")
    private String merchantCustomerId;


}

@AllArgsConstructor
@Setter
@Getter
class Deal {

    private UUID id;

    private Settlements settlements;
}

@AllArgsConstructor
@Setter
@Getter
class Settlements {

    private String type = "payout";

    private Amount amount;
}

@Getter
@Setter
@AllArgsConstructor
class Transfer {

    private Amount amount;

    @JsonProperty("account_id")
    private int accountId = 417160;

    private StatusOfPayment status;

    @JsonProperty("platform_fee_amount")
    private Amount platformFeeAmount;
}

@AllArgsConstructor
@Getter
@Setter
class AuthorizationDetails {

    private long rrn;

    @JsonProperty("auth_code")
    private long authCode;

    @JsonProperty("three_d_secure")
    private ThreeDSecure threeDSecure;
}

@AllArgsConstructor
@Getter
@Setter
class ThreeDSecure {

    private boolean applied;
}

@AllArgsConstructor
@Setter
@Getter
class CancellationDetails {

    private Party party;

    private String reason;
}

enum Party {
    yoo_money,
    payment_network,
    merchant
}

enum ReceiptRegistration {
    pending,
    succeeded,
    canceled
}

@AllArgsConstructor
@Setter
@Getter
class Confirmation {

    private String type = "embedded";

    @JsonProperty("confirmation_token")
    private String confirmationToken;
}

enum StatusOfPayment {
    pending,
    waiting_for_capture,
    succeeded,
    canceled
}

@AllArgsConstructor
@Getter
@Setter
class Recipient {

    @JsonProperty("gateway_id")
    private UUID gatewayId;

    @JsonProperty("account_id")
    private final int accountId = 417160;

}
