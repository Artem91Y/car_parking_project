package com.example.demo.utils;

import com.example.demo.models.BookingRecord;
import com.example.demo.models.ParkingPlace;

import java.time.Duration;

public class CountMoney {
    public static int countPrice(Duration dateDiff,  ParkingPlace parkingPlace, BookingRecord bookingRecord) {
        final double minutesInHour = 60.0;
        bookingRecord.setPrice((int) (dateDiff.toMinutes() / minutesInHour * parkingPlace.getPricePerHour()));
        return bookingRecord.getPrice();
    }
}
