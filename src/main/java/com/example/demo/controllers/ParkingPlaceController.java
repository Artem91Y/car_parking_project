package com.example.demo.controllers;

import com.example.demo.dtos.ParkingPlaceRequest;
import com.example.demo.models.BookingRecord;
import com.example.demo.services.ParkingPlaceService;
import com.example.demo.utils.PaymentRequest;
import com.example.demo.utils.models.CardRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.UUID;

@RestController
public class ParkingPlaceController {
    private final ParkingPlaceService parkingPlaceService;


    public ParkingPlaceController(ParkingPlaceService parkingPlaceService) {
        this.parkingPlaceService = parkingPlaceService;
    }

    @PostMapping("/saveParkingPlace")
    public ResponseEntity<String> saveParkingPlace(@RequestBody ParkingPlaceRequest parkingPlaceRequest) {
        return parkingPlaceService.saveParkingPlace(parkingPlaceRequest);
    }

    @PutMapping("/updateParkingPlace/{number}")
    public ResponseEntity<String> updateParkingPlace(@PathVariable int number,
                                                     @RequestBody ParkingPlaceRequest parkingPlaceRequest) {
        return parkingPlaceService.updateParkingPlace(number, parkingPlaceRequest);
    }

    @DeleteMapping("/deleteParkingPlace/{number}")
    public ResponseEntity<String> deleteParkingPlace(@PathVariable int number) {
        return parkingPlaceService.deleteParkingPlace(number);
    }

    @PutMapping("/buyParkingPlace/{parkingPlaceNumber}")
    public ResponseEntity<String> buyParkingPlace(@PathVariable int parkingPlaceNumber,
                                                  @RequestParam String carNumber,
                                                  @RequestParam String startTime,
                                                  @RequestParam String endTime,
                                                  @RequestBody CardRequest cardRequest) {
        return parkingPlaceService.buyParkingPlace(startTime, endTime, carNumber, parkingPlaceNumber, cardRequest);
    }

    @GetMapping("/getParkingPlace/{number}")
    public ResponseEntity<String> getParkingPlace(@PathVariable int number) {
        return parkingPlaceService.getParkingPlace(number);
    }

    @DeleteMapping("/deleteBookingRecord")
    public ResponseEntity<BookingRecord> deleteBookingRecord(@RequestParam UUID registrationNumber) {
        return parkingPlaceService.deleteBookingRecord(registrationNumber);
    }

    @GetMapping("/getParkingPlacesBookingRecords/{number}")
    public ResponseEntity<Set<BookingRecord>> getParkingPlacesBookingRecords(@PathVariable int number) {
        return parkingPlaceService.getParkingPlacesBookingRecords(number);
    }
}