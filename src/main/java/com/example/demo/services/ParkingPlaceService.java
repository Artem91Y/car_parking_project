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
        if (parkingPlaceRepository.findByNumber(parkingPlaceRequest.getNumber()).isEmpty()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("This parking place is already exist");
        }
        if (parkingPlaceRequest.getPricePerHour() == 0) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("This number is already used");
        }
        ParkingPlace parkingPlace = new ParkingPlace();
        parkingPlace.setNumber(parkingPlaceRequest.getNumber());

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
        if (parkingPlaceRepository.findByNumber(parkingPlaceRequest.getNumber()).isEmpty()) {
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

    public ResponseEntity<ParkingPlace> deleteParkingPlace(int number) {
        if (parkingPlaceRepository.findByNumber(number).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        ParkingPlace parkingPlace = parkingPlaceRepository.findByNumber(number).get();
        try {
            parkingPlaceRepository.delete(parkingPlace);
            return ResponseEntity.status(HttpStatus.OK).body(parkingPlace);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public ResponseEntity<ParkingPlace> getParkingPlace(int number) {
        try {
            if (parkingPlaceRepository.findByNumber(number).isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            ParkingPlace parkingPlace = parkingPlaceRepository.findByNumber(number).get();
            return ResponseEntity.status(HttpStatus.OK).body(parkingPlace);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public ResponseEntity<String> buyParkingPlace(LocalDateTime startTime, LocalDateTime endTime, String carNumber, int parkingPlaceNumber) {
        if (parkingPlaceRepository.findByNumber(parkingPlaceNumber).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No such parking place");
        }
        if (carRepository.findCarByNumber(carNumber).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No such car");
        }
        if (SecurityContextHolder.getContext().getAuthentication().getName().isEmpty()
                || personRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName()).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("You are not authorized");
        }
        try {
            Person person = personRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName()).get();
            Car car = carRepository.findCarByNumber(carNumber).get();
            ParkingPlace parkingPlace = parkingPlaceRepository.findByNumber(parkingPlaceNumber).get();
            BookingRecord bookingRecord = new BookingRecord();
            bookingRecord.setCar(car);
            bookingRecord.setParkingPlace(parkingPlace);
            bookingRecord.setEndTime(endTime);
            bookingRecord.setStartTime(startTime);
            Set<BookingRecord> bookingRecordSetParkingPlace = parkingPlace.getBookingRecords();
            bookingRecordSetParkingPlace.add(bookingRecord);
            Set<BookingRecord> bookingRecordSetCar = car.getBookingRecords();
            bookingRecordSetCar.add(bookingRecord);
            car.setBookingRecords(bookingRecordSetCar);
            parkingPlace.setBookingRecords(bookingRecordSetParkingPlace);
            float dateDiff = (endTime.getYear() - startTime.getYear()) * 365 * 24 + (endTime.getMonth().length(false) - startTime.getMonth().length(false)) * 24 * 12 + (endTime.getHour() - startTime.getHour()) + (endTime.getMinute() - startTime.getMinute()) / 60;
            if (person.getMoney() < dateDiff * parkingPlace.getPricePerHour()) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("You haven't enough money");
            }
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
