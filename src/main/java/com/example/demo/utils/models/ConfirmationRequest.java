package com.example.demo.utils.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.net.MalformedURLException;
import java.net.URL;

@AllArgsConstructor
@Setter
@ToString
@Getter
@NoArgsConstructor
public class ConfirmationRequest {

    private String type = "redirect";

    private String locale;

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
