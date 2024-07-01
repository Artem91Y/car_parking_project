package com.example.demo.dtos;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@EqualsAndHashCode
@ToString
public class ParkingPlaceRequest {
    private Integer number;
    private Integer pricePerHour;
}
