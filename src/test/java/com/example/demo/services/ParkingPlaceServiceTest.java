package com.example.demo.services;

import com.example.demo.dtos.CancellationPaymentException;
import com.example.demo.dtos.NotFoundException;
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
import com.example.demo.utils.models.CardRequest;
import com.example.demo.utils.models.PaymentRequest;
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

    private Person person;

    private Car car;

    @BeforeEach
    void init() {
        parkingPlaceRequest = new ParkingPlaceRequest(1, 200);
        person = new Person(1L, "John", null, null, "smith");
        car = new Car(1L, "u123ir", TypeOfCar.USUAL_CAR, person, null);
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
        assertThrows(NotFoundException.class, () -> parkingPlaceService.getParkingPlace(1), "Failed to get parking place");
    }

    @Test
    public void TestGetParkingPlacePositive() {
        when(parkingPlaceRepository.findByNumber(1)).thenReturn(Optional.of(parkingPlace));
        ResponseEntity<String> response = parkingPlaceService.getParkingPlace(1);
        ResponseEntity<String> expected = ResponseEntity.status(HttpStatus.OK)
                .body(parkingPlace.toString());
        assertEquals(response, expected);
    }

    @Test
    public void TestBuyParkingPlacePositive() {
        SecurityContext securityContext = mock(SecurityContext.class);
        when(parkingPlaceRepository.findByNumber(1)).thenReturn(Optional.of(parkingPlace));
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(carRepository.findCarByNumber("u123ir")).thenReturn(Optional.of(car));
        when(personRepository.findByUsername("smith")).thenReturn(Optional.of(person));
        when(authentication.getName()).thenReturn("smith");
        ResponseEntity<String> response = parkingPlaceService.buyParkingPlace("2024-09-05 09:00", "2024-09-05 15:00", "u123ir", 1, new CardRequest());
        ResponseEntity<String> expected = ResponseEntity.status(HttpStatus.OK).body("Parking place is bought successfully");
        assertEquals(response, expected);
    }

    @Test
    public void TestBuyParkingPlaceNegativeNoParkingPlace() {
        SecurityContext securityContext = mock(SecurityContext.class);
        when(parkingPlaceRepository.findByNumber(1)).thenReturn(Optional.empty());
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(carRepository.findCarByNumber("u123ir")).thenReturn(Optional.of(car));
        when(personRepository.findByUsername("smith")).thenReturn(Optional.of(person));
        when(authentication.getName()).thenReturn("smith");
        assertThrows(NotFoundException.class, () -> parkingPlaceService.buyParkingPlace("2024-09-05 09:00", "2024-09-05 15:00", "u123ir", 1, new CardRequest()), "No such parking place");
    }

    @Test
    public void TestBuyParkingPlaceNegativeNoCar() {
        SecurityContext securityContext = mock(SecurityContext.class);
        when(parkingPlaceRepository.findByNumber(1)).thenReturn(Optional.of(parkingPlace));
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(carRepository.findCarByNumber("u123ir")).thenReturn(Optional.empty());
        when(personRepository.findByUsername("smith")).thenReturn(Optional.of(person));
        when(authentication.getName()).thenReturn("smith");
        assertThrows(NotFoundException.class, () -> parkingPlaceService.buyParkingPlace("2024-09-05 09:00", "2024-09-05 15:00", "u123ir", 1, new CardRequest()), "No such car");
    }

    @Test
    public void TestBuyParkingPlaceNegativePastTime() {
        SecurityContext securityContext = mock(SecurityContext.class);
        when(parkingPlaceRepository.findByNumber(1)).thenReturn(Optional.of(parkingPlace));
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(carRepository.findCarByNumber("u123ir")).thenReturn(Optional.of(car));
        when(personRepository.findByUsername("smith")).thenReturn(Optional.of(person));
        when(authentication.getName()).thenReturn("smith");
        assertThrows(CancellationPaymentException.class,() -> parkingPlaceService.buyParkingPlace("2021-09-05 09:00", "2024-09-05 15:00", "u123ir", 1, new CardRequest()), "You gave past time");
    }

    @Test
    public void TestBuyParkingPlaceNegativeNoMoney() {
        SecurityContext securityContext = mock(SecurityContext.class);
        when(apiConnection.createPayment(any(PaymentRequest.class))).thenThrow(new CancellationPaymentException("You haven't enough money"));
        when(parkingPlaceRepository.findByNumber(1)).thenReturn(Optional.of(parkingPlace));
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(carRepository.findCarByNumber("u123ir")).thenReturn(Optional.of(car));
        when(personRepository.findByUsername("smith")).thenReturn(Optional.of(person));
        when(authentication.getName()).thenReturn("smith");
        assertThrows(CancellationPaymentException.class,() -> parkingPlaceService.buyParkingPlace("2024-09-05 09:00", "2024-09-05 15:00", "u123ir", 1, new CardRequest("5555555555554600", 2399, "01")), "You haven't enough money");
    }

    @Test
    public void TestBuyParkingPlaceNegativeWrongTime() {
        SecurityContext securityContext = mock(SecurityContext.class);
        when(parkingPlaceRepository.findByNumber(1)).thenReturn(Optional.of(parkingPlace));
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(carRepository.findCarByNumber("u123ir")).thenReturn(Optional.of(car));
        when(personRepository.findByUsername("smith")).thenReturn(Optional.of(person));
        when(authentication.getName()).thenReturn("smith");
        assertThrows(CancellationPaymentException.class,() -> parkingPlaceService.buyParkingPlace("wrong format", "2024-09-05 09:00", "u123ir", 1, new CardRequest()), "Incorrect format of dates");
    }

    @Test
    public void TestBuyParkingPlaceNegativeBookedTime() {
        SecurityContext securityContext = mock(SecurityContext.class);
        when(parkingPlaceRepository.findByNumber(1)).thenReturn(Optional.of(parkingPlace));
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(carRepository.findCarByNumber("u123ir")).thenReturn(Optional.of(car));
        when(personRepository.findByUsername("smith")).thenReturn(Optional.of(person));
        when(authentication.getName()).thenReturn("smith");
        assertThrows(CancellationPaymentException.class, ()-> parkingPlaceService.buyParkingPlace("2024-09-05 09:00", "2024-09-05 15:00", "u123ir", 1, new CardRequest()), "This time is already booked");
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
        assertThrows(NotFoundException.class, () -> parkingPlaceService.deleteBookingRecord(registrationNumber), "No such booking record");

    }

    @Test
    public void TestGetParkingPlacesBookingRecordsPositive() {
        when(parkingPlaceRepository.findByNumber(1)).thenReturn(Optional.of(parkingPlace));
        ResponseEntity<Set<BookingRecord>> response = parkingPlaceService.getParkingPlacesBookingRecords(1);
        ResponseEntity<Set<BookingRecord>> expected = ResponseEntity.status(HttpStatus.OK).body(parkingPlace.getBookingRecords());
        assertEquals(response, expected);
    }

    @Test
    public void TestGetParkingPlacesBookingRecordsNegativeNoParkingPlace() {
        when(parkingPlaceRepository.findByNumber(1)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> parkingPlaceService.getParkingPlacesBookingRecords(1), "No such parking place");
    }
}
