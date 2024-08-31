package com.example.demo.services;

import com.example.demo.dtos.CancellationPaymentException;
import com.example.demo.dtos.ErrorException;
import com.example.demo.dtos.NotFoundException;
import com.example.demo.dtos.ParkingPlaceRequest;
import com.example.demo.models.BookingRecord;
import com.example.demo.models.Car;
import com.example.demo.models.ParkingPlace;
import com.example.demo.models.Person;
import com.example.demo.repos.BookingRecordRepository;
import com.example.demo.repos.CarRepository;
import com.example.demo.repos.ParkingPlaceRepository;
import com.example.demo.repos.PersonRepository;
import com.example.demo.utils.ApiConnection;
import com.example.demo.utils.CheckIfTimeIsBooked;
import com.example.demo.utils.CountDatesDifference;
import com.example.demo.utils.CountMoney;
import com.example.demo.utils.models.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.DateTimeException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class ParkingPlaceService {
    private final ParkingPlaceRepository parkingPlaceRepository;
    private final ApiConnection apiConnection;
    private final PersonRepository personRepository;
    private final BookingRecordRepository bookingRecordRepository;
    private final CarRepository carRepository;
    @Value("${ratio-of-tax}")
    private double ratioOfTax;

    public ParkingPlaceService(ParkingPlaceRepository parkingPlaceRepository,
                               ApiConnection apiConnection,
                               PersonRepository personRepository,
                               BookingRecordRepository bookingRecordRepository,
                               CarRepository carRepository
    ) {
        this.parkingPlaceRepository = parkingPlaceRepository;
        this.apiConnection = apiConnection;
        this.personRepository = personRepository;
        this.bookingRecordRepository = bookingRecordRepository;
        this.carRepository = carRepository;
    }

    public ResponseEntity<String> saveParkingPlace(ParkingPlaceRequest parkingPlaceRequest) {
        if (parkingPlaceRequest.getNumber() == null || parkingPlaceRequest.getPricePerHour() == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Parking place isn't full to be created");
        }
        if (parkingPlaceRepository.findByNumber(parkingPlaceRequest.getNumber()).isPresent()) {
            throw new ErrorException("This parking place is already exist");
        }
        ParkingPlace parkingPlace = new ParkingPlace();
        parkingPlace.setNumber(parkingPlaceRequest.getNumber());
        parkingPlace.setPricePerHour(parkingPlaceRequest.getPricePerHour());

        try {
            parkingPlaceRepository.save(parkingPlace);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Parking place is created successfully");
        } catch (Exception e) {
            throw new ErrorException("Parking place isn't created");
        }
    }

    public ResponseEntity<String> updateParkingPlace(int number, ParkingPlaceRequest parkingPlaceRequest) {
        if (parkingPlaceRepository.findByNumber(number).isEmpty()) {
            throw new NotFoundException("No such parking place");
        }
        if (parkingPlaceRepository.findByNumber(parkingPlaceRequest.getNumber()).isPresent()) {
            throw new ErrorException("This parking place is already exist");
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
            throw new ErrorException("Failed to update parking place");
        }
    }

    public ResponseEntity<String> deleteParkingPlace(int number) {
        if (parkingPlaceRepository.findByNumber(number).isEmpty()) {
            throw new NotFoundException("No such parking place");
        }
        ParkingPlace parkingPlace = parkingPlaceRepository.findByNumber(number).get();
        try {
            parkingPlaceRepository.delete(parkingPlace);
            return ResponseEntity.status(HttpStatus.OK).body(parkingPlace.toString());
        } catch (Exception e) {
            throw new ErrorException("Failed to delete parking place");
        }
    }

    public ResponseEntity<String> getParkingPlace(int number) {
        if (parkingPlaceRepository.findByNumber(number).isEmpty()) {
            throw new NotFoundException("No such parking place");
        }
        try {
            ParkingPlace parkingPlace = parkingPlaceRepository.findByNumber(number).get();
            return ResponseEntity.status(HttpStatus.OK).body(parkingPlace.toString());
        } catch (Exception e) {
            throw new ErrorException("Failed to get parking place");
        }
    }

    public ResponseEntity<String> buyParkingPlace(String startTime, String endTime, String carNumber,
                                                  int parkingPlaceNumber,
                                                  CardRequest cardRequest) {
        if (parkingPlaceRepository.findByNumber(parkingPlaceNumber).isEmpty()) {
            throw new NotFoundException("No such parking place");
        }
        if (carRepository.findCarByNumber(carNumber).isEmpty()) {
            throw new NotFoundException("No such car");
        }

            Person person = personRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName()).get();
            Car car = carRepository.findCarByNumber(carNumber).get();
            ParkingPlace parkingPlace = parkingPlaceRepository.findByNumber(parkingPlaceNumber).get();
            BookingRecord bookingRecord = new BookingRecord();
            bookingRecord.setCar(car);
            bookingRecord.setParkingPlace(parkingPlace);
            bookingRecord.setRegistrationNumber(UUID.randomUUID());
            Duration dateDiff = null;
            LocalDateTime startTime2;
            LocalDateTime endTime2;
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            try {
                startTime2 = LocalDateTime.parse(startTime, dateTimeFormatter); // validate startTime
                endTime2 = LocalDateTime.parse(endTime, dateTimeFormatter);
            } catch (DateTimeParseException e){
                throw new CancellationPaymentException("Incorrect format of dates");
            }
            try {
                dateDiff = CountDatesDifference.countDateDiffFromStrings(startTime2, endTime2);
            } catch (DateTimeException e) {
                throw new CancellationPaymentException(e.getMessage());
            }
            if (CheckIfTimeIsBooked.checkIfTimeIsBooked(parkingPlace, startTime2, endTime2)) {
                throw new CancellationPaymentException("This time is already booked");
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
            int price = CountMoney.countPrice(dateDiff, parkingPlace, bookingRecord);

            PaymentRequest paymentRequest = PaymentRequest
                    .builder()
                    .amount(new Amount(price, "RUB"))
                    .refundable(true)
                    .confirmation(new ConfirmationRequest())
                    .paymentMethod(new PaymentRequestMethod(cardRequest))
                    .build();
            System.out.println(paymentRequest);
            UUID paymentId = apiConnection.createPayment(paymentRequest);
            bookingRecord.setPaymentId(paymentId);
            car.setBookingRecords(bookingRecordSetCar);
            parkingPlace.setBookingRecords(bookingRecordSetParkingPlace);
            personRepository.save(person);
            carRepository.save(car);
            bookingRecordRepository.save(bookingRecord);
            return ResponseEntity.status(HttpStatus.OK).body("Parking place is bought successfully");
    }

    public ResponseEntity<BookingRecord> deleteBookingRecord(UUID registrationNumber) {
        Optional<BookingRecord> deletedBookingRecord = bookingRecordRepository.findByRegistrationNumber(registrationNumber);
        if (deletedBookingRecord.isEmpty()) {
            throw new NotFoundException("No such booking record");
        }

        try {
            BookingRecord bookingRecord = deletedBookingRecord.get();
            apiConnection.refund(bookingRecord.getPaymentId(), (int) (bookingRecord.getPrice() * ratioOfTax));
            bookingRecordRepository.deleteByRegistrationNumber(registrationNumber);
            return ResponseEntity.status(HttpStatus.OK).body(bookingRecord);
        } catch (Exception e) {
            throw new ErrorException("Failed to delete booking record");
        }
    }

    public ResponseEntity<Set<BookingRecord>> getParkingPlacesBookingRecords(int number) {
        if (parkingPlaceRepository.findByNumber(number).isEmpty()) {
            throw new NotFoundException("No such parking place");
        }
        try {
            return ResponseEntity.status(HttpStatus.OK).body(parkingPlaceRepository.findByNumber(number).get().getBookingRecords());
        } catch (Exception e) {
            throw new ErrorException("Failed to get parking place's booking records");
        }
    }
}
