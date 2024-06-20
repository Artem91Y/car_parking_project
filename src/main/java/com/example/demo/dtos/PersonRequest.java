package com.example.demo.dtos;

import com.example.demo.models.Car;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@EqualsAndHashCode
public class PersonRequest {
    private String fullName;

    @NotNull
    private List<Car> cars;
}
