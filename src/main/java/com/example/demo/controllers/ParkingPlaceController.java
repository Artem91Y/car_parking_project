package com.example.demo.controllers;

import com.example.demo.dtos.ParkingPlaceRequest;
import com.example.demo.models.ParkingPlace;
import com.example.demo.services.ParkingPlaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
public class ParkingPlaceController {
    @Autowired
    private final ParkingPlaceService parkingPlaceService;


    public ParkingPlaceController(ParkingPlaceService parkingPlaceService) {
        this.parkingPlaceService = parkingPlaceService;
    }

    @PostMapping("/saveParkingPlace")
    public ResponseEntity<String> saveParkingPlace(@RequestBody ParkingPlaceRequest parkingPlaceRequest) {
        return parkingPlaceService.saveParkingPlace(parkingPlaceRequest);
    }

    @PutMapping("/updateParkingPlace/{number}")
    public ResponseEntity<String> updateParkingPlace(@PathVariable int number, @RequestBody ParkingPlaceRequest parkingPlaceRequest) {
        return parkingPlaceService.updateParkingPlace(number, parkingPlaceRequest);
    }

    @DeleteMapping("/deleteParkingPlace/{number}")
    public ResponseEntity<ParkingPlace> deleteParkingPlace(@PathVariable int number) {
        return parkingPlaceService.deleteParkingPlace(number);
    }

    @PutMapping("/buyParkingPlace/{parkingPlaceNumber}")
    public ResponseEntity<String> buyParkingPlace(@PathVariable int parkingPlaceNumber, @RequestParam String carNumber, @RequestParam LocalDateTime startTime, @RequestParam LocalDateTime endTime) {
        return parkingPlaceService.buyParkingPlace(startTime, endTime, carNumber, parkingPlaceNumber);
    }

    @GetMapping("/getParkingPlace/{number}")
    public ResponseEntity<ParkingPlace> getParkingPlace(@PathVariable int number) {
        return parkingPlaceService.getParkingPlace(number);
    }
}