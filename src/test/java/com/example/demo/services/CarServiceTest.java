package com.example.demo.services;

import com.example.demo.dtos.CarRequest;
import com.example.demo.dtos.ErrorException;
import com.example.demo.dtos.NotFoundException;
import com.example.demo.models.BookingRecord;
import com.example.demo.models.Car;
import com.example.demo.models.Person;
import com.example.demo.models.enums.RulesBreaks;
import com.example.demo.models.enums.TypeOfCar;
import com.example.demo.repos.CarRepository;
import com.example.demo.repos.PersonRepository;
import org.aspectj.weaver.ast.Not;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CarServiceTest {

    @InjectMocks
    private CarService carService;

    @Mock
    private CarRepository carRepository;

    @Mock
    private PersonRepository personRepository;

    @Mock
    private Authentication authentication;

    private Person person;

    private Car car;

    public CarRequest carRequest;

    @BeforeEach
    public void setUp() {
        person = new Person(1L, "John", null, null, "smith");
        car = new Car(1L, "u123ir", TypeOfCar.USUAL_CAR, person, null);
        carRequest = new CarRequest("u123ir", TypeOfCar.USUAL_CAR);
    }

    public CarServiceTest() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void TestSaveCarPositive() {
        when(carRepository.findCarByNumber("u123ir")).thenReturn(Optional.empty());
        ResponseEntity<String> response = carService.saveCar(carRequest);
        ResponseEntity<String> expected = ResponseEntity.status(HttpStatus.CREATED).body("Car is created successfully");
        assertEquals(response, expected);
    }

    @Test
    public void TestSaveCarNegativeDBFail() {
        when(carRepository.findCarByNumber("u123ir")).thenThrow(new DataAccessException("DB error") {});
        assertThrows(DataAccessException.class, () -> carService.saveCar(carRequest));
    }

    @Test
    public void TestSaveCarNegativeExistingCar() {
        when(carRepository.findCarByNumber("u123ir")).thenReturn(Optional.of(car));
        assertThrows(ErrorException.class, () -> carService.saveCar(carRequest), "This car already exists");
    }

    @Test
    public void TestSaveCarNegativeNoCar() {
        when(carRepository.findCarByNumber("u123ir")).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> carService.saveCar(carRequest), "No such car");

    }

    @Test
    public void TestUpdateCarPositive() {
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(carRepository.findCarByNumber("u123ir")).thenReturn(Optional.of(car));
        when(personRepository.findByUsername("smith")).thenReturn(Optional.of(person));
        when(authentication.getName()).thenReturn("smith");
        ResponseEntity<String> response = carService.updateCar("u123ir", carRequest);
        ResponseEntity<String> expected = ResponseEntity.status(HttpStatus.CREATED).body("Car is updated successfully");
        assertEquals(response, expected);
    }

    @Test
    public void TestUpdateCarNegativeDBFail() {
        when(carRepository.findCarByNumber("u123ir")).thenThrow(new DataAccessException("DB error") {
        });
        assertThrows(DataAccessException.class, () -> carService.updateCar("u123ir", carRequest));
    }

    @Test
    public void TestUpdateCarNegativeNoCar() {
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(carRepository.findCarByNumber("u123ir")).thenReturn(Optional.of(car));
        when(personRepository.findByUsername("smith")).thenReturn(Optional.of(person));
        when(authentication.getName()).thenReturn("smith");
        assertThrows(NotFoundException.class, () -> carService.updateCar("wrong number", carRequest), "No such car");

    }

    @Test
    public void TestUpdateCarNegativeWrongCar() {
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(carRepository.findCarByNumber("u123ir")).thenReturn(Optional.of(car));
        when(personRepository.findByUsername("smith")).thenReturn(Optional.of(person));
        when(authentication.getName()).thenReturn("smith");
        assertThrows(ErrorException.class, () -> carService.updateCar("u123ir", new CarRequest("u321ir", TypeOfCar.USUAL_CAR)), "It's not your car");

    }

    @Test
    public void TestDeleteCarPositive() {
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn("smith");
        when(personRepository.findByUsername("smith")).thenReturn(Optional.of(person));
        when(carRepository.findCarByNumber("u123ir")).thenReturn(Optional.of(car));
        ResponseEntity<Car> response = carService.deleteCar("u123ir");
        ResponseEntity<Car> expected = ResponseEntity.status(HttpStatus.OK).body(car);
        assertEquals(response, expected);
    }

    @Test
    public void TestDeleteCarNegativeNoCar() {
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn("smith");
        when(personRepository.findByUsername("smith")).thenReturn(Optional.of(person));
        when(carRepository.findCarByNumber("u123ir")).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> carService.deleteCar("u123ir"), "No such car");

    }

    @Test
    public void TestDeleteCarNegativeWrongPerson() {
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn("smith");
        when(personRepository.findByUsername("smith")).thenReturn(Optional.of(person));
        car.setPerson(person);
        when(carRepository.findCarByNumber("u123ir")).thenReturn(Optional.of(car));
        ResponseEntity<Car> response = carService.deleteCar("u123ir");
        ResponseEntity<Car> expected = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        assertEquals(expected, response);
    }

    @Test
    public void TestGetCarPositive() {
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn("smith");
        when(personRepository.findByUsername("smith")).thenReturn(Optional.of(person));
        when(carRepository.findCarByNumber("u123ir")).thenReturn(Optional.of(car));
        ResponseEntity<Car> response = carService.getCar("u123ir");
        ResponseEntity<Car> expected = ResponseEntity.status(HttpStatus.OK).body(car);
        assertEquals(response, expected);
    }

    @Test
    public void TestGetCarNegativeNoCar() {
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn("smith");
        when(personRepository.findByUsername("smith")).thenReturn(Optional.of(person));
        when(carRepository.findCarByNumber("u123ir")).thenReturn(Optional.empty());
        ResponseEntity<Car> response = carService.getCar("u123ir");
        ResponseEntity<Car> expected = ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        assertEquals(response, expected);
    }

    @Test
    public void TestGetCarNegativeWrongPerson() {
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn("smith");
        when(personRepository.findByUsername("smith")).thenReturn(Optional.of(person));
        car.setPerson(person);
        when(carRepository.findCarByNumber("u123ir")).thenReturn(Optional.of(car));
        ResponseEntity<Car> response = carService.getCar("u123ir");
        ResponseEntity<Car> expected = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        assertEquals(response, expected);
    }

    @Test
    public void TestGetCarsBookingRecordPositive() {
        UUID registrationNumber = UUID.randomUUID();
        BookingRecord bookingRecord = new BookingRecord(1L, null, null, LocalDateTime.now(), LocalDateTime.now().plusHours(8L), registrationNumber, 1600, UUID.randomUUID());
        car.setBookingRecords(Set.of(bookingRecord));
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn("smith");
        when(personRepository.findByUsername("smith")).thenReturn(Optional.of(person));
        when(carRepository.findCarByNumber("u123ir")).thenReturn(Optional.of(car));
        ResponseEntity<Set<BookingRecord>> response = carService.getCarsBookingRecords("u123ir");
        ResponseEntity<Set<BookingRecord>> expected = ResponseEntity.status(HttpStatus.OK).body(Set.of(bookingRecord));
        assertEquals(response, expected);
    }

    @Test
    public void TestGetCarsBookingRecordNegativeNoCar() {
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn("smith");
        when(personRepository.findByUsername("smith")).thenReturn(Optional.of(person));
        when(carRepository.findCarByNumber("u123ir")).thenReturn(Optional.empty());
        ResponseEntity<Set<BookingRecord>> response = carService.getCarsBookingRecords("u123ir");
        ResponseEntity<Set<BookingRecord>> expected = ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        assertEquals(response, expected);
    }

    @Test
    public void TestGetCarsBookingRecordNegativeWrongPerson() {
        UUID registrationNumber = UUID.randomUUID();
        BookingRecord bookingRecord = new BookingRecord(1L, null, null, LocalDateTime.now(), LocalDateTime.now().plusHours(8L), registrationNumber, 1600, UUID.randomUUID());
        car.setBookingRecords(Set.of(bookingRecord));
        car.setPerson(person);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn("smith");
        when(personRepository.findByUsername("smith")).thenReturn(Optional.of(person));
        when(carRepository.findCarByNumber("u123ir")).thenReturn(Optional.of(car));
        ResponseEntity<Set<BookingRecord>> response = carService.getCarsBookingRecords("u123ir");
        ResponseEntity<Set<BookingRecord>> expected = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        assertEquals(response, expected);
    }
}
