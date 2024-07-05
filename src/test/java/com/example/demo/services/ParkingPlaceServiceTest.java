package com.example.demo.services;

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
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.awt.print.Book;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    private BookingRecordRepository bookingRecordRepository;


    public ParkingPlaceServiceTest() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void TestSaveParkingPlacePositive() {
        when(parkingPlaceRepository.findByNumber(1)).thenReturn(Optional.empty());
        ResponseEntity<String> response = parkingPlaceService.saveParkingPlace(new ParkingPlaceRequest(1, 200));
        ResponseEntity<String> expected = ResponseEntity.status(HttpStatus.CREATED)
                .body("Parking place is created successfully");
        assertEquals(response, expected);
    }

    @Test
    public void TestSaveParkingPlaceNegativeDuplicateParkingPlace() {
        when(parkingPlaceRepository.findByNumber(1)).thenReturn(Optional.of(new ParkingPlace(1L, 1, null, 200)));
        ResponseEntity<String> response = parkingPlaceService.saveParkingPlace(new ParkingPlaceRequest(1, 200));
        ResponseEntity<String> expected = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("This parking place is already exist");
        assertEquals(response, expected);
    }

    @Test
    public void TestUpdateParkingPlacePositive() {
        when(parkingPlaceRepository.findByNumber(1)).thenReturn(Optional.of(new ParkingPlace(1L, 1, null, 200)));
        ResponseEntity<String> response = parkingPlaceService.updateParkingPlace(1, new ParkingPlaceRequest(2, 200));
        ResponseEntity<String> expected = ResponseEntity.status(HttpStatus.CREATED)
                .body("Parking place is updated successfully");
        assertEquals(response, expected);
    }

    @Test
    public void TestUpdateParkingPlaceNegativeExistingParkingPlace() {
        when(parkingPlaceRepository.findByNumber(1)).thenReturn(Optional.of(new ParkingPlace(1L, 1, null, 200)));
        ResponseEntity<String> response = parkingPlaceService.updateParkingPlace(1, new ParkingPlaceRequest(1, 200));
        ResponseEntity<String> expected = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("This parking place is already exist");
        assertEquals(response, expected);
    }

    @Test
    public void TestUpdateParkingPlaceNegativeNoParkingPlace() {
        when(parkingPlaceRepository.findByNumber(1)).thenReturn(Optional.empty());
        ResponseEntity<String> response = parkingPlaceService.updateParkingPlace(1, new ParkingPlaceRequest(2, 200));
        ResponseEntity<String> expected = ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("No such parking place");
        assertEquals(response, expected);
    }

    @Test
    public void TestDeleteParkingPlacePositive() {
        when(parkingPlaceRepository.findByNumber(1)).thenReturn(Optional.of(new ParkingPlace(1L, 1, null, 200)));
        ResponseEntity<String> response = parkingPlaceService.deleteParkingPlace(1);
        ResponseEntity<String> expected = ResponseEntity.status(HttpStatus.OK)
                .body(new ParkingPlace(1L, 1, null, 200).toString());
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
        Person person = new Person(1L, "John", 1500, null, null, "smith");
        SecurityContext securityContext = mock(SecurityContext.class);
        when(parkingPlaceRepository.findByNumber(1)).thenReturn(Optional.of(new ParkingPlace(1L, 1, null, 200)));
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(carRepository.findCarByNumber("u123ir")).thenReturn(Optional.of(new Car(1L, "u123ir", TypeOfCar.USUAL_CAR, person, null)));
        when(personRepository.findByUsername("smith")).thenReturn(Optional.of(person));
        when(authentication.getName()).thenReturn("smith");
        ResponseEntity<String> response = parkingPlaceService.buyParkingPlace("2024-09-05 09:00", "2024-09-05 15:00", "u123ir", 1);
        ResponseEntity<String> expected = ResponseEntity.status(HttpStatus.OK).body("Parking place is bought successfully");
        assertEquals(response, expected);
    }

    @Test
    public void TestBuyParkingPlaceNegativeNoParkingPlace() {
        Person person = new Person(1L, "John", 1500, null, null, "smith");
        SecurityContext securityContext = mock(SecurityContext.class);
        when(parkingPlaceRepository.findByNumber(1)).thenReturn(Optional.empty());
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(carRepository.findCarByNumber("u123ir")).thenReturn(Optional.of(new Car(1L, "u123ir", TypeOfCar.USUAL_CAR, person, null)));
        when(personRepository.findByUsername("smith")).thenReturn(Optional.of(person));
        when(authentication.getName()).thenReturn("smith");
        ResponseEntity<String> response = parkingPlaceService.buyParkingPlace("2024-09-05 09:00", "2024-09-05 15:00", "u123ir", 1);
        ResponseEntity<String> expected = ResponseEntity.status(HttpStatus.NOT_FOUND).body("No such parking place");
        assertEquals(response, expected);
    }

    @Test
    public void TestBuyParkingPlaceNegativeNoCar() {
        Person person = new Person(1L, "John", 1500, null, null, "smith");
        SecurityContext securityContext = mock(SecurityContext.class);
        when(parkingPlaceRepository.findByNumber(1)).thenReturn(Optional.of(new ParkingPlace(1L, 1, null, 200)));
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(carRepository.findCarByNumber("u123ir")).thenReturn(Optional.empty());
        when(personRepository.findByUsername("smith")).thenReturn(Optional.of(person));
        when(authentication.getName()).thenReturn("smith");
        ResponseEntity<String> response = parkingPlaceService.buyParkingPlace("2024-09-05 09:00", "2024-09-05 15:00", "u123ir", 1);
        ResponseEntity<String> expected = ResponseEntity.status(HttpStatus.NOT_FOUND).body("No such car");
        assertEquals(response, expected);
    }

    @Test
    public void TestBuyParkingPlaceNegativePastTime() {
        Person person = new Person(1L, "John", 1500, null, null, "smith");
        SecurityContext securityContext = mock(SecurityContext.class);
        when(parkingPlaceRepository.findByNumber(1)).thenReturn(Optional.of(new ParkingPlace(1L, 1, null, 200)));
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(carRepository.findCarByNumber("u123ir")).thenReturn(Optional.of(new Car(1L, "u123ir", TypeOfCar.USUAL_CAR, person, null)));
        when(personRepository.findByUsername("smith")).thenReturn(Optional.of(person));
        when(authentication.getName()).thenReturn("smith");
        ResponseEntity<String> response = parkingPlaceService.buyParkingPlace("2021-09-05 09:00", "2024-09-05 15:00", "u123ir", 1);
        ResponseEntity<String> expected = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("You gave past time");
        assertEquals(response, expected);
    }

    @Test
    public void TestBuyParkingPlaceNegativeNoMoney() {
        Person person = new Person(1L, "John", 0, null, null, "smith");
        SecurityContext securityContext = mock(SecurityContext.class);
        when(parkingPlaceRepository.findByNumber(1)).thenReturn(Optional.of(new ParkingPlace(1L, 1, null, 200)));
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(carRepository.findCarByNumber("u123ir")).thenReturn(Optional.of(new Car(1L, "u123ir", TypeOfCar.USUAL_CAR, person, null)));
        when(personRepository.findByUsername("smith")).thenReturn(Optional.of(person));
        when(authentication.getName()).thenReturn("smith");
        ResponseEntity<String> response = parkingPlaceService.buyParkingPlace("2024-09-05 09:00", "2024-09-05 15:00", "u123ir", 1);
        ResponseEntity<String> expected = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("You haven't enough money");
        assertEquals(response, expected);
    }

    @Test
    public void TestBuyParkingPlaceNegativeWrongTime() {
        Person person = new Person(1L, "John", 1500, null, null, "smith");
        SecurityContext securityContext = mock(SecurityContext.class);
        when(parkingPlaceRepository.findByNumber(1)).thenReturn(Optional.of(new ParkingPlace(1L, 1, null, 200)));
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(carRepository.findCarByNumber("u123ir")).thenReturn(Optional.of(new Car(1L, "u123ir", TypeOfCar.USUAL_CAR, person, null)));
        when(personRepository.findByUsername("smith")).thenReturn(Optional.of(person));
        when(authentication.getName()).thenReturn("smith");
        ResponseEntity<String> response = parkingPlaceService.buyParkingPlace("2024-009-05 15:00", "2024-09-05 09:00", "u123ir", 1);
        ResponseEntity<String> expected = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Incorrect format of dates");
        assertEquals(response, expected);
    }

    @Test
    public void TestBuyParkingPlaceNegativeBookedTime() {
        Person person = new Person(1L, "John", 1500, null, null, "smith");
        SecurityContext securityContext = mock(SecurityContext.class);
        when(parkingPlaceRepository.findByNumber(1)).thenReturn(Optional.of(new ParkingPlace(1L, 1, Set.of(new BookingRecord(1L, null, null, LocalDateTime.of(LocalDate.of(2024, 9, 5), LocalTime.of(9, 0)), LocalDateTime.of(LocalDate.of(2024, 9, 5), LocalTime.of(15, 0)), UUID.randomUUID(), 0)), 200)));
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(carRepository.findCarByNumber("u123ir")).thenReturn(Optional.of(new Car(1L, "u123ir", TypeOfCar.USUAL_CAR, person, null)));
        when(personRepository.findByUsername("smith")).thenReturn(Optional.of(person));
        when(authentication.getName()).thenReturn("smith");
        ResponseEntity<String> response = parkingPlaceService.buyParkingPlace("2024-09-05 09:00", "2024-09-05 15:00", "u123ir", 1);
        ResponseEntity<String> expected = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("This time is already booked");
        assertEquals(response, expected);
    }

    @Test
    public void TestDeleteBookingRecordPositive() {
        UUID registrationNumber = UUID.randomUUID();
        BookingRecord bookingRecord = new BookingRecord(1L, new Car(1L, "u123ir", TypeOfCar.USUAL_CAR, new Person(1L, "Nicolas", 1200, null, null, "smith"), null), null, null, null, registrationNumber, 1200);
        when(bookingRecordRepository.deleteByRegistrationNumber(registrationNumber)).thenReturn(Optional.of(bookingRecord));
        when(bookingRecordRepository.findByRegistrationNumber(registrationNumber)).thenReturn(Optional.of(bookingRecord));
        ResponseEntity<BookingRecord> response = parkingPlaceService.deleteBookingRecord(registrationNumber);
        ResponseEntity<BookingRecord> expected = ResponseEntity.status(HttpStatus.OK).body(new BookingRecord(1L, null, null, null, null, registrationNumber, 1200));
        assertEquals(response, expected);
    }

    @Test
    public void TestDeleteBookingRecordNegativeNoBookingRecord() {
        UUID registrationNumber = UUID.randomUUID();
        when(bookingRecordRepository.deleteByRegistrationNumber(registrationNumber)).thenReturn(Optional.empty());
        when(bookingRecordRepository.findByRegistrationNumber(registrationNumber)).thenReturn(Optional.empty());
        ResponseEntity<BookingRecord> response = parkingPlaceService.deleteBookingRecord(registrationNumber);
        ResponseEntity<BookingRecord> expected = ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        assertEquals(response, expected);
    }

    @Test
    public void TestGetParkingPlacesBookingRecordsPositive() {
        UUID registrationNumber = UUID.randomUUID();
        when(parkingPlaceRepository.findByNumber(1)).thenReturn(Optional.of(new ParkingPlace(1L, 1, Set.of(new BookingRecord(1L, null, null, LocalDateTime.of(LocalDate.of(2024, 9, 5), LocalTime.of(9, 0)), LocalDateTime.of(LocalDate.of(2024, 9, 5), LocalTime.of(15, 0)), registrationNumber, 1200)), 200)));
        ResponseEntity<Set<BookingRecord>> response = parkingPlaceService.getParkingPlacesBookingRecords(1);
        ResponseEntity<Set<BookingRecord>> expected = ResponseEntity.status(HttpStatus.OK).body(Set.of(new BookingRecord(1L, null, null, LocalDateTime.of(LocalDate.of(2024, 9, 5), LocalTime.of(9, 0)), LocalDateTime.of(LocalDate.of(2024, 9, 5), LocalTime.of(15, 0)), registrationNumber, 1200)));
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
