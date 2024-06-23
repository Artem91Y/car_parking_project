package com.example.demo.dtos;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@EqualsAndHashCode
public class ParkingPlaceRequest {
    private int number;
    private int pricePerHour;
}
