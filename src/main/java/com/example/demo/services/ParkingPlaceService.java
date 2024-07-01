package com.example.demo.services;

import com.example.demo.dtos.ParkingPlaceRequest;
import com.example.demo.models.BookingRecord;
import com.example.demo.models.Car;
import com.example.demo.models.ParkingPlace;
import com.example.demo.models.Person;
import com.example.demo.repos.BookingRecordRepository;
import com.example.demo.repos.CarRepository;
import com.example.demo.repos.ParkingPlaceRepository;
import com.example.demo.repos.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.UUID;

@Service
public class ParkingPlaceService {
    @Autowired
    private final ParkingPlaceRepository parkingPlaceRepository;
    @Autowired
    private final PersonRepository personRepository;
    @Autowired
    private final BookingRecordRepository bookingRecordRepository;
    @Autowired
    private final CarRepository carRepository;

    public ParkingPlaceService(ParkingPlaceRepository parkingPlaceRepository, PersonRepository personRepository, BookingRecordRepository bookingRecordRepository, CarRepository carRepository) {
        this.parkingPlaceRepository = parkingPlaceRepository;
        this.personRepository = personRepository;
        this.bookingRecordRepository = bookingRecordRepository;
        this.carRepository = carRepository;
    }

    public ResponseEntity<String> saveParkingPlace(ParkingPlaceRequest parkingPlaceRequest) {
        if (parkingPlaceRequest.getNumber() == null || parkingPlaceRequest.getPricePerHour() == null){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Parking place isn't full to be created");
        }
        if (parkingPlaceRepository.findByNumber(parkingPlaceRequest.getNumber()).isPresent()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("This parking place is already exist");
        }
        ParkingPlace parkingPlace = new ParkingPlace();
        parkingPlace.setNumber(parkingPlaceRequest.getNumber());
        parkingPlace.setPricePerHour(parkingPlaceRequest.getPricePerHour());

        try {
            parkingPlaceRepository.save(parkingPlace);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Parking place is created successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Parking place isn't created");
        }
    }

    public ResponseEntity<String> updateParkingPlace(Integer number, ParkingPlaceRequest parkingPlaceRequest) {
        if (parkingPlaceRepository.findByNumber(number).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No such parking place");
        }
        if (parkingPlaceRepository.findByNumber(parkingPlaceRequest.getNumber()).isPresent()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("This parking place is already exist");
        }
        ParkingPlace parkingPlace = new ParkingPlace();
        parkingPlace.setNumber(parkingPlaceRequest.getNumber());
        if (parkingPlaceRequest.getPricePerHour() != 0) {
            parkingPlace.setPricePerHour(parkingPlaceRequest.getPricePerHour());
        }
        try {
            parkingPlaceRepository.save(parkingPlace);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Parking place is updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Parking place isn't updated");
        }
    }

    public ResponseEntity<String> deleteParkingPlace(Integer number) {
        if (parkingPlaceRepository.findByNumber(number).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No such parking place");
        }
        ParkingPlace parkingPlace = parkingPlaceRepository.findByNumber(number).get();
        try {
            parkingPlaceRepository.delete(parkingPlace);
            return ResponseEntity.status(HttpStatus.OK).body(parkingPlace.toString());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Parking place isn't deleted");
        }
    }

    public ResponseEntity<String> getParkingPlace(Integer number) {
        try {
            if (parkingPlaceRepository.findByNumber(number).isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No such parking place");

            }
            ParkingPlace parkingPlace = parkingPlaceRepository.findByNumber(number).get();
            return ResponseEntity.status(HttpStatus.OK).body(parkingPlace.toString());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public ResponseEntity<String> buyParkingPlace(String startTime, String endTime, String carNumber, int parkingPlaceNumber) {
        if (parkingPlaceRepository.findByNumber(parkingPlaceNumber).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No such parking place");
        }
        if (carRepository.findCarByNumber(carNumber).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No such car");
        }
        try {
            Person person = personRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName()).get();
            Car car = carRepository.findCarByNumber(carNumber).get();
            ParkingPlace parkingPlace = parkingPlaceRepository.findByNumber(parkingPlaceNumber).get();
            BookingRecord bookingRecord = new BookingRecord();
            bookingRecord.setCar(car);
            bookingRecord.setParkingPlace(parkingPlace);
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDateTime startTime2 = LocalDateTime.parse(startTime, dateTimeFormatter);
            LocalDateTime endTime2 = LocalDateTime.parse(endTime, dateTimeFormatter);
            if (parkingPlace.getBookingRecords() != null) {
                for (BookingRecord bookingRecord2 : parkingPlace.getBookingRecords()) {
                    if ((startTime2.isAfter(bookingRecord2.getStartTime()) || startTime2.equals(bookingRecord2.getStartTime())) && (startTime2.isBefore(bookingRecord2.getEndTime()) || startTime2.equals(bookingRecord2.getStartTime()))
                            || (endTime2.isBefore(bookingRecord2.getEndTime())|| endTime2.equals(bookingRecord2.getEndTime()) && endTime2.isAfter(bookingRecord2.getStartTime()) || endTime2.equals(bookingRecord2.getEndTime()))) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("This time is already booked");
                    }
                }
            }
            if (startTime2.isBefore(LocalDateTime.now()) || endTime2.isBefore(LocalDateTime.now())) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("You gave past time");
            }
            bookingRecord.setEndTime(endTime2);
            bookingRecord.setStartTime(startTime2);
            Set<BookingRecord> bookingRecordSetParkingPlace = parkingPlace.getBookingRecords();
            if (bookingRecordSetParkingPlace!=null){
                bookingRecordSetParkingPlace.add(bookingRecord);
            } else {
                bookingRecordSetParkingPlace = Set.of(bookingRecord);
            }
            Set<BookingRecord> bookingRecordSetCar = car.getBookingRecords();
            if (bookingRecordSetCar != null){
                bookingRecordSetCar.add(bookingRecord);
            } else {
                bookingRecordSetCar = Set.of(bookingRecord);
            }
            car.setBookingRecords(bookingRecordSetCar);
            parkingPlace.setBookingRecords(bookingRecordSetParkingPlace);
            ZonedDateTime zoneDateTime = startTime2.atZone(ZoneId.of("Europe/Moscow"));
            long sec = zoneDateTime.toInstant().toEpochMilli() / 1000;
            ZonedDateTime zoneDateTime2 = endTime2.atZone(ZoneId.of("Europe/Moscow"));
            long sec2 = zoneDateTime2.toInstant().toEpochMilli() / 1000;
            double dateDiff = (double) (sec2 - sec) / 60 / 60.0;
            if (dateDiff < 0) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("You gave wrong time");
            }
            if (person.getMoney() < dateDiff * parkingPlace.getPricePerHour()) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("You haven't enough money");
            }
            System.out.println(dateDiff * parkingPlace.getPricePerHour());
            System.out.println(parkingPlace);
            person.setMoney(person.getMoney() - (int) (dateDiff * parkingPlace.getPricePerHour()));
            personRepository.save(person);
            carRepository.save(car);
            bookingRecordRepository.save(bookingRecord);
            return ResponseEntity.status(HttpStatus.OK).body("Parking place is bought successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Parking place isn't bought");
        }
    }

    public ResponseEntity<BookingRecord> deleteBookingRecord(UUID registrationNumber) {
        if (bookingRecordRepository.findByRegistrationNumber(registrationNumber).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        try {
            return ResponseEntity.status(HttpStatus.OK).body(bookingRecordRepository.deleteByRegistrationNumber(registrationNumber).get());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public ResponseEntity<Set<BookingRecord>> getParkingPlacesBookingRecords(int number) {
        if (parkingPlaceRepository.findByNumber(number).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        try {
            return ResponseEntity.status(HttpStatus.OK).body(parkingPlaceRepository.findByNumber(number).get().getBookingRecords());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
