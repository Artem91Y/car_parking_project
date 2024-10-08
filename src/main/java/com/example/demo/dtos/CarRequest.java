package com.example.demo.dtos;

import com.example.demo.models.enums.TypeOfCar;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@EqualsAndHashCode
@ToString
public class CarRequest {
    private String number;
    private TypeOfCar type;
}
