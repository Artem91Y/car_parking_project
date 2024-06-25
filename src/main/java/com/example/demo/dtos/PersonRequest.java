package com.example.demo.dtos;

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
    private List<String> numbers;
}
