package com.example.demo.controllers;

import com.example.demo.dtos.CarRequest;
import com.example.demo.models.BookingRecord;
import com.example.demo.models.Car;
import com.example.demo.services.CarService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
public class CarController {
    private final CarService carService;

    public CarController(CarService carService) {
        this.carService = carService;
    }

    @PostMapping("/saveCar")
    public ResponseEntity<String> saveCar(@RequestBody CarRequest carRequest) {
        return carService.saveCar(carRequest);
    }

    @PutMapping("/updateCar")
    public ResponseEntity<String> updateCar(@RequestParam String number, @RequestBody CarRequest carRequest) {
        return carService.updateCar(number, carRequest);
    }

    @GetMapping("/getCar")
    public ResponseEntity<Car> getCar(@RequestParam String number) {
        return carService.getCar(number);
    }

    @GetMapping("/getCarsBookingRecord")
    public ResponseEntity<Set<BookingRecord>> getCarsBookingRecords(@RequestParam String number) {
        return carService.getCarsBookingRecords(number);
    }

    @DeleteMapping("/deleteCar")
    public ResponseEntity<Car> deleteCar(@RequestParam String number) {
        return carService.deleteCar(number);
    }
}
