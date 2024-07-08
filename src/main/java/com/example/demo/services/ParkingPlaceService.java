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
import utils.CheckIfTimeIsBooked;
import utils.CountDatesDifference;
import utils.CountMoney;

import java.time.*;
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

    private final double ratioOfTax = 0.5;

    public ParkingPlaceService(ParkingPlaceRepository parkingPlaceRepository, PersonRepository personRepository, BookingRecordRepository bookingRecordRepository, CarRepository carRepository) {
        this.parkingPlaceRepository = parkingPlaceRepository;
        this.personRepository = personRepository;
        this.bookingRecordRepository = bookingRecordRepository;
        this.carRepository = carRepository;
    }

    public ResponseEntity<String> saveParkingPlace(ParkingPlaceRequest parkingPlaceRequest) {
        if (parkingPlaceRequest.getNumber() == null || parkingPlaceRequest.getPricePerHour() == null) {
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

    public ResponseEntity<String> updateParkingPlace(int number, ParkingPlaceRequest parkingPlaceRequest) {
        if (parkingPlaceRepository.findByNumber(number).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No such parking place");
        }
        if (parkingPlaceRepository.findByNumber(parkingPlaceRequest.getNumber()).isPresent()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("This parking place is already exist");
        }
        ParkingPlace parkingPlace = parkingPlaceRepository.findByNumber(number).get();
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

    public ResponseEntity<String> deleteParkingPlace(int number) {
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

    public ResponseEntity<String> getParkingPlace(int number) {
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
            Duration dateDiff = null;
            LocalDateTime startTime2;
            LocalDateTime endTime2;
            try {
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                startTime2 = LocalDateTime.parse(startTime, dateTimeFormatter);
                endTime2 = LocalDateTime.parse(endTime, dateTimeFormatter);
                try {
                    dateDiff = CountDatesDifference.countDateDiffFromStrings(startTime2, endTime2);
                } catch (DateTimeException e) {
                    if (e.getMessage().equals("Past time")) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Past time");
                    }
                    if (e.getMessage().equals("Wrong dates")) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Wrong dates");
                    }
                }
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Incorrect format of dates");
            }
            if (CheckIfTimeIsBooked.checkIfTimeIsBooked(parkingPlace, startTime2, endTime2)) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("This time is already booked");
            }
            bookingRecord.setEndTime(endTime2);
            bookingRecord.setStartTime(startTime2);
            Set<BookingRecord> bookingRecordSetParkingPlace = parkingPlace.getBookingRecords();
            if (bookingRecordSetParkingPlace != null) {
                bookingRecordSetParkingPlace.add(bookingRecord);
            } else {
                bookingRecordSetParkingPlace = Set.of(bookingRecord);
            }
            Set<BookingRecord> bookingRecordSetCar = car.getBookingRecords();
            if (bookingRecordSetCar != null) {
                bookingRecordSetCar.add(bookingRecord);
            } else {
                bookingRecordSetCar = Set.of(bookingRecord);
            }
            CountMoney.writeOffMoney(dateDiff, person, parkingPlace, bookingRecord);
            car.setBookingRecords(bookingRecordSetCar);
            parkingPlace.setBookingRecords(bookingRecordSetParkingPlace);
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
            BookingRecord deletedBookingRecord = bookingRecordRepository.deleteByRegistrationNumber(registrationNumber).get();
            Person person = deletedBookingRecord.getCar().getPerson();
            person.setMoney(person.getMoney() + (int) (deletedBookingRecord.getPrice() * ratioOfTax));
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
