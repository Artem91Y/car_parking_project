package com.example.demo.utils;

import com.example.demo.models.Car;
import com.example.demo.repos.PersonRepository;
import org.springframework.security.core.context.SecurityContextHolder;

public class CheckTheOwnerOfTheCar {
    public static Boolean CheckTheOwnerOfTheCarByContext(Car car, PersonRepository personRepository) {
        return car.getPerson().equals(personRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName()).get());
    }
}
