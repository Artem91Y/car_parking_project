package com.example.demo.services;

import com.example.demo.dtos.ApiKassaConnectionException;
import com.example.demo.dtos.CancellationPaymentException;
import com.example.demo.dtos.CaptureFailedException;
import com.example.demo.dtos.ParkingPlaceRequest;
import com.example.demo.models.BookingRecord;
import com.example.demo.models.Car;
import com.example.demo.models.ParkingPlace;
import com.example.demo.models.Person;
import com.example.demo.models.enums.TypeOfCar;
import com.example.demo.repos.BookingRecordRepository;
import com.example.demo.repos.CarRepository;
import com.example.demo.repos.ParkingPlaceRepository;
import com.example.demo.repos.PersonRepository;
import com.example.demo.utils.ApiConnection;
import com.example.demo.utils.models.PaymentRequest;
import com.example.demo.utils.models.CardRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ParkingPlaceServiceTest {

    @InjectMocks
    private ParkingPlaceService parkingPlaceService;
    @Mock
    private ParkingPlaceRepository parkingPlaceRepository;
    @Mock
    private CarRepository carRepository;
    @Mock
    private Authentication authentication;
    @Mock
    private PersonRepository personRepository;
    @Mock
    private ApiConnection apiConnection;
    @Mock
    private BookingRecordRepository bookingRecordRepository;

    private ParkingPlace parkingPlace;

    private ParkingPlaceRequest parkingPlaceRequest;

    @BeforeEach
    void init() {
        parkingPlaceRequest = new ParkingPlaceRequest(1, 200);
        parkingPlace = new ParkingPlace(1L, 1, Set.of(new BookingRecord(1L, null, null, LocalDateTime.of(LocalDate.of(2024, 9, 5), LocalTime.of(9, 0)), LocalDateTime.of(LocalDate.of(2024, 9, 5), LocalTime.of(15, 0)), UUID.randomUUID(), 0, UUID.randomUUID())), 200);
    }


    public ParkingPlaceServiceTest() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void TestSaveParkingPlacePositive() {
        when(parkingPlaceRepository.findByNumber(1)).thenReturn(Optional.empty());
        ResponseEntity<String> response = parkingPlaceService.saveParkingPlace(parkingPlaceRequest);
        ResponseEntity<String> expected = ResponseEntity.status(HttpStatus.CREATED)
                .body("Parking place is created successfully");
        assertEquals(response, expected);
    }

    @Test
    public void TestSaveParkingPlaceNegativeDuplicateParkingPlace() {
        when(parkingPlaceRepository.findByNumber(1)).thenReturn(Optional.of(parkingPlace));
        ResponseEntity<String> response = parkingPlaceService.saveParkingPlace(parkingPlaceRequest);
        ResponseEntity<String> expected = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("This parking place is already exist");
        assertEquals(response, expected);
    }

    @Test
    public void TestSaveParkingPlaceNegativeDBFail() {
        when(parkingPlaceRepository.findByNumber(1)).thenThrow(new DataAccessException("DB error") {});
        assertThrows(DataAccessException.class, () -> parkingPlaceService.saveParkingPlace(parkingPlaceRequest));
    }

    @Test
    public void TestUpdateParkingPlacePositive() {
        when(parkingPlaceRepository.findByNumber(2)).thenReturn(Optional.of(parkingPlace));
        ResponseEntity<String> response = parkingPlaceService.updateParkingPlace(2, parkingPlaceRequest);
        ResponseEntity<String> expected = ResponseEntity.status(HttpStatus.CREATED)
                .body("Parking place is updated successfully");
        assertEquals(response, expected);
    }

    @Test
    public void TestUpdateParkingPlaceNegativeExistingParkingPlace() {
        when(parkingPlaceRepository.findByNumber(1)).thenReturn(Optional.of(parkingPlace));
        ResponseEntity<String> response = parkingPlaceService.updateParkingPlace(1, parkingPlaceRequest);
        ResponseEntity<String> expected = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("This parking place is already exist");
        assertEquals(response, expected);
    }

    @Test
    public void TestUpdateParkingPlaceNegativeNoParkingPlace() {
        when(parkingPlaceRepository.findByNumber(2)).thenReturn(Optional.empty());
        ResponseEntity<String> response = parkingPlaceService.updateParkingPlace(2, parkingPlaceRequest);
        ResponseEntity<String> expected = ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("No such parking place");
        assertEquals(response, expected);
    }

    @Test
    public void TestUpdateParkingPlaceNegativeDBFail() {
        when(parkingPlaceRepository.findByNumber(2)).thenThrow(new DataAccessException("DB error") {});
        assertThrows(DataAccessException.class, () -> parkingPlaceService.updateParkingPlace(2, parkingPlaceRequest));
    }

    @Test
    public void TestDeleteParkingPlacePositive() {
        when(parkingPlaceRepository.findByNumber(1)).thenReturn(Optional.of(parkingPlace));
        ResponseEntity<String> response = parkingPlaceService.deleteParkingPlace(1);
        ResponseEntity<String> expected = ResponseEntity.status(HttpStatus.OK)
                .body(parkingPlace.toString());
        assertEquals(response, expected);
    }

    @Test
    public void TestDeleteParkingPlaceNegativeNoParkingPlace() {
        when(parkingPlaceRepository.findByNumber(1)).thenReturn(Optional.empty());
        ResponseEntity<String> response = parkingPlaceService.deleteParkingPlace(1);
        ResponseEntity<String> expected = ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("No such parking place");
        assertEquals(response, expected);
    }

    @Test
    public void TestDeleteParkingPlaceNegativeDBFail() {
        when(parkingPlaceRepository.findByNumber(1)).thenThrow(new DataAccessException("DB error") {});
        assertThrows(DataAccessException.class, () -> parkingPlaceService.deleteParkingPlace(1));
    }

    @Test
    public void TestGetParkingPlaceNegativeNoParkingPlace() {
        when(parkingPlaceRepository.findByNumber(1)).thenReturn(Optional.empty());
        ResponseEntity<String> response = parkingPlaceService.getParkingPlace(1);
        ResponseEntity<String> expected = ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("No such parking place");
        assertEquals(response, expected);
    }

    @Test
    public void TestGetParkingPlacePositive() {
        when(parkingPlaceRepository.findByNumber(1)).thenReturn(Optional.of(new ParkingPlace(1L, 1, null, 200)));
        ResponseEntity<String> response = parkingPlaceService.getParkingPlace(1);
        ResponseEntity<String> expected = ResponseEntity.status(HttpStatus.OK)
                .body(new ParkingPlace(1L, 1, null, 200).toString());
        assertEquals(response, expected);
    }

    @Test
    public void TestBuyParkingPlacePositive() {
        Person person = new Person(1L, "John", null, null, "smith");
        SecurityContext securityContext = mock(SecurityContext.class);
        when(parkingPlaceRepository.findByNumber(1)).thenReturn(Optional.of(new ParkingPlace(1L, 1, null, 200)));
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(carRepository.findCarByNumber("u123ir")).thenReturn(Optional.of(new Car(1L, "u123ir", TypeOfCar.USUAL_CAR, person, null)));
        when(personRepository.findByUsername("smith")).thenReturn(Optional.of(person));
        when(authentication.getName()).thenReturn("smith");
        ResponseEntity<String> response = parkingPlaceService.buyParkingPlace("2024-09-05 09:00", "2024-09-05 15:00", "u123ir", 1, new CardRequest());
        ResponseEntity<String> expected = ResponseEntity.status(HttpStatus.OK).body("Parking place is bought successfully");
        assertEquals(response, expected);
    }

    @Test
    public void TestBuyParkingPlaceNegativeNoParkingPlace() {
        Person person = new Person(1L, "John", null, null, "smith");
        SecurityContext securityContext = mock(SecurityContext.class);
        when(parkingPlaceRepository.findByNumber(1)).thenReturn(Optional.empty());
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(carRepository.findCarByNumber("u123ir")).thenReturn(Optional.of(new Car(1L, "u123ir", TypeOfCar.USUAL_CAR, person, null)));
        when(personRepository.findByUsername("smith")).thenReturn(Optional.of(person));
        when(authentication.getName()).thenReturn("smith");
        ResponseEntity<String> response = parkingPlaceService.buyParkingPlace("2024-09-05 09:00", "2024-09-05 15:00", "u123ir", 1, new CardRequest());
        ResponseEntity<String> expected = ResponseEntity.status(HttpStatus.NOT_FOUND).body("No such parking place");
        assertEquals(response, expected);
    }

    @Test
    public void TestBuyParkingPlaceNegativeNoCar() {
        Person person = new Person(1L, "John", null, null, "smith");
        SecurityContext securityContext = mock(SecurityContext.class);
        when(parkingPlaceRepository.findByNumber(1)).thenReturn(Optional.of(new ParkingPlace(1L, 1, null, 200)));
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(carRepository.findCarByNumber("u123ir")).thenReturn(Optional.empty());
        when(personRepository.findByUsername("smith")).thenReturn(Optional.of(person));
        when(authentication.getName()).thenReturn("smith");
        ResponseEntity<String> response = parkingPlaceService.buyParkingPlace("2024-09-05 09:00", "2024-09-05 15:00", "u123ir", 1, new CardRequest());
        ResponseEntity<String> expected = ResponseEntity.status(HttpStatus.NOT_FOUND).body("No such car");
        assertEquals(response, expected);
    }

    @Test
    public void TestBuyParkingPlaceNegativePastTime() {
        Person person = new Person(1L, "John", null, null, "smith");
        SecurityContext securityContext = mock(SecurityContext.class);
        when(parkingPlaceRepository.findByNumber(1)).thenReturn(Optional.of(new ParkingPlace(1L, 1, null, 200)));
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(carRepository.findCarByNumber("u123ir")).thenReturn(Optional.of(new Car(1L, "u123ir", TypeOfCar.USUAL_CAR, person, null)));
        when(personRepository.findByUsername("smith")).thenReturn(Optional.of(person));
        when(authentication.getName()).thenReturn("smith");
        ResponseEntity<String> response = parkingPlaceService.buyParkingPlace("2021-09-05 09:00", "2024-09-05 15:00", "u123ir", 1, new CardRequest());
        ResponseEntity<String> expected = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("You gave past time");
        assertEquals(response, expected);
    }

    @Test
    public void TestBuyParkingPlaceNegativeNoMoney() {
        Person person = new Person(1L, "John", null, null, "smith");
        SecurityContext securityContext = mock(SecurityContext.class);
        try {
            when(apiConnection.createPayment(any(PaymentRequest.class))).thenThrow(new CancellationPaymentException("You haven't enough money"));
        } catch (CaptureFailedException | CancellationPaymentException | ApiKassaConnectionException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        when(parkingPlaceRepository.findByNumber(1)).thenReturn(Optional.of(new ParkingPlace(1L, 1, null, 200)));
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(carRepository.findCarByNumber("u123ir")).thenReturn(Optional.of(new Car(1L, "u123ir", TypeOfCar.USUAL_CAR, person, null)));
        when(personRepository.findByUsername("smith")).thenReturn(Optional.of(person));
        when(authentication.getName()).thenReturn("smith");
        ResponseEntity<String> response = parkingPlaceService.buyParkingPlace("2024-09-05 09:00", "2024-09-05 15:00", "u123ir", 1, new CardRequest("5555555555554600", 2399, "01"));
        ResponseEntity<String> expected = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("You haven't enough money");
        assertEquals(response, expected);
    }

    @Test
    public void TestBuyParkingPlaceNegativeWrongTime() {
        Person person = new Person(1L, "John", null, null, "smith");
        SecurityContext securityContext = mock(SecurityContext.class);
        when(parkingPlaceRepository.findByNumber(1)).thenReturn(Optional.of(new ParkingPlace(1L, 1, null, 200)));
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(carRepository.findCarByNumber("u123ir")).thenReturn(Optional.of(new Car(1L, "u123ir", TypeOfCar.USUAL_CAR, person, null)));
        when(personRepository.findByUsername("smith")).thenReturn(Optional.of(person));
        when(authentication.getName()).thenReturn("smith");
        ResponseEntity<String> response = parkingPlaceService.buyParkingPlace("2024-009-05 15:00", "2024-09-05 09:00", "u123ir", 1, new CardRequest());
        ResponseEntity<String> expected = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Incorrect format of dates");
        assertEquals(response, expected);
    }

    @Test
    public void TestBuyParkingPlaceNegativeBookedTime() {
        Person person = new Person(1L, "John", null, null, "smith");
        SecurityContext securityContext = mock(SecurityContext.class);
        when(parkingPlaceRepository.findByNumber(1)).thenReturn(Optional.of(new ParkingPlace(1L, 1, Set.of(new BookingRecord(1L, null, null, LocalDateTime.of(LocalDate.of(2024, 9, 5), LocalTime.of(9, 0)), LocalDateTime.of(LocalDate.of(2024, 9, 5), LocalTime.of(15, 0)), UUID.randomUUID(), 0, UUID.randomUUID())), 200)));
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(carRepository.findCarByNumber("u123ir")).thenReturn(Optional.of(new Car(1L, "u123ir", TypeOfCar.USUAL_CAR, person, null)));
        when(personRepository.findByUsername("smith")).thenReturn(Optional.of(person));
        when(authentication.getName()).thenReturn("smith");
        ResponseEntity<String> response = parkingPlaceService.buyParkingPlace("2024-09-05 09:00", "2024-09-05 15:00", "u123ir", 1, new CardRequest());
        ResponseEntity<String> expected = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("This time is already booked");
        assertEquals(response, expected);
    }

    @Test
    public void TestDeleteBookingRecordPositive() {
        UUID registrationNumber = UUID.randomUUID();
        BookingRecord bookingRecord = new BookingRecord(1L, new Car(1L, "u123ir", TypeOfCar.USUAL_CAR, new Person(1L, "Nicolas", null, null, "smith"), null), null, null, null, registrationNumber, 1200, UUID.randomUUID());
        when(bookingRecordRepository.findByRegistrationNumber(registrationNumber)).thenReturn(Optional.of(bookingRecord));
        ResponseEntity<BookingRecord> response = parkingPlaceService.deleteBookingRecord(registrationNumber);
        ResponseEntity<BookingRecord> expected = ResponseEntity.status(HttpStatus.OK).body(bookingRecord);
        assertEquals(response, expected);
    }

    @Test
    public void TestDeleteBookingRecordNegativeNoBookingRecord() {
        UUID registrationNumber = UUID.randomUUID();
        when(bookingRecordRepository.findByRegistrationNumber(registrationNumber)).thenReturn(Optional.empty());
        when(bookingRecordRepository.findByRegistrationNumber(registrationNumber)).thenReturn(Optional.empty());
        ResponseEntity<BookingRecord> response = parkingPlaceService.deleteBookingRecord(registrationNumber);
        ResponseEntity<BookingRecord> expected = ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        assertEquals(response, expected);
    }

    @Test
    public void TestGetParkingPlacesBookingRecordsPositive() {
        UUID registrationNumber = UUID.randomUUID();
        BookingRecord bookingRecord = new BookingRecord(1L, null, null, LocalDateTime.of(LocalDate.of(2024, 9, 5), LocalTime.of(9, 0)), LocalDateTime.of(LocalDate.of(2024, 9, 5), LocalTime.of(15, 0)), registrationNumber, 1200, UUID.randomUUID());
        when(parkingPlaceRepository.findByNumber(1)).thenReturn(Optional.of(new ParkingPlace(1L, 1, Set.of(bookingRecord), 200)));
        ResponseEntity<Set<BookingRecord>> response = parkingPlaceService.getParkingPlacesBookingRecords(1);
        ResponseEntity<Set<BookingRecord>> expected = ResponseEntity.status(HttpStatus.OK).body(Set.of(bookingRecord));
        assertEquals(response, expected);
    }

    @Test
    public void TestGetParkingPlacesBookingRecordsNegativeNoParkingPlace() {
        when(parkingPlaceRepository.findByNumber(1)).thenReturn(Optional.empty());
        ResponseEntity<Set<BookingRecord>> response = parkingPlaceService.getParkingPlacesBookingRecords(1);
        ResponseEntity<Set<BookingRecord>> expected = ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        assertEquals(response, expected);
    }
}
