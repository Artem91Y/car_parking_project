package utils;

import com.example.demo.models.BookingRecord;
import com.example.demo.models.ParkingPlace;
import com.example.demo.models.Person;

import java.time.Duration;

public class CountMoney {
    public static void writeOffMoney(Duration dateDiff, Person person, ParkingPlace parkingPlace, BookingRecord bookingRecord) {
        final double minutesInHour = 60.0;
        if (person.getMoney() < dateDiff.toMinutes() / minutesInHour * parkingPlace.getPricePerHour()) {
            throw new IllegalArgumentException("You haven't enough money");
        }
        bookingRecord.setPrice((int) (dateDiff.toMinutes() / minutesInHour * parkingPlace.getPricePerHour()));
        person.setMoney(person.getMoney() - bookingRecord.getPrice());
    }
}
