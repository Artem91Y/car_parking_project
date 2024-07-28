package com.example.demo.utils;

import com.example.demo.models.BookingRecord;
import com.example.demo.models.ParkingPlace;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

public class CheckIfTimeIsBooked {
    public static boolean checkIfTimeIsBooked(ParkingPlace parkingPlace, LocalDateTime startTime, LocalDateTime endTime){
        if (parkingPlace.getBookingRecords() != null) {
            for (BookingRecord bookingRecord2 : parkingPlace.getBookingRecords()) {
                if ((startTime.isAfter(bookingRecord2.getStartTime()) || startTime.equals(bookingRecord2.getStartTime())) && (startTime.isBefore(bookingRecord2.getEndTime()) || startTime.equals(bookingRecord2.getStartTime()))
                        || (endTime.isBefore(bookingRecord2.getEndTime()) || endTime.equals(bookingRecord2.getEndTime()) && endTime.isAfter(bookingRecord2.getStartTime()) || endTime.equals(bookingRecord2.getEndTime()))) {
                    return true;
                }
            }
        }
        return false;
    }
}
