package com.example.demo.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
public class Card {

    private long first6;

    private int last4;

    @JsonProperty("expiry_year")
    private int expiryYear;

    @JsonProperty("expiry_month")
    private int expiryMonth;

    @JsonProperty("card_type")
    private CardType cardType;

    @JsonProperty("card_product")
    private CardProduct cardProduct;

    @JsonProperty("issuer_country")
    private String issuerCountry;

    @JsonProperty("issuer_name")
    private String issuerName;

    private String source;
}
enum CardType{
    MasterCard,
    Visa,
    Mir,
    UnionPay,
    JCB,
    AmericanExpress,
    DinersClub,
    DiscoverCard,
    InstaPayment,
    InstaPaymentTM,
    Laser,
    Dankort,
    Solo,
    Switch,
    Unknown
}
@Setter
@Getter
@AllArgsConstructor
class CardProduct{

    private String code;

    private String name;
}
