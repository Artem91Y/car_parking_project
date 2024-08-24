package com.example.demo.services;

import com.example.demo.dtos.CarRequest;
import com.example.demo.models.BookingRecord;
import com.example.demo.models.Car;
import com.example.demo.models.Person;
import com.example.demo.models.enums.RulesBreaks;
import com.example.demo.models.enums.TypeOfCar;
import com.example.demo.repos.CarRepository;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

    public CarServiceTest() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void TestSaveCarPositive() {
        when(carRepository.findCarByNumber("u123ir")).thenReturn(Optional.empty());
        ResponseEntity<String> response = carService.saveCar(new CarRequest("u123ir", TypeOfCar.USUAL_CAR));
        ResponseEntity<String> expected = ResponseEntity.status(HttpStatus.CREATED).body("Car is created successfully");
        assertEquals(response, expected);
    }

    @Test
    public void TestSaveCarNegativeDBFail() {
        when(carRepository.findCarByNumber("u123ir")).thenReturn(Optional.empty());
        ResponseEntity<String> response = carService.saveCar(new CarRequest("u123ir", TypeOfCar.USUAL_CAR));
        ResponseEntity<String> expected = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Car isn't created");
        assertEquals(response, expected);
    }

    @Test
    public void TestSaveCarNegative() {
        when(carRepository.findCarByNumber("u123ir")).thenReturn(Optional.of(new Car(1L, "u123ir", TypeOfCar.USUAL_CAR, null, null)));
        ResponseEntity<String> response = carService.saveCar(new CarRequest("u123ir", TypeOfCar.USUAL_CAR));
        ResponseEntity<String> expected = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("This car already exists");
        assertEquals(response, expected);
    }

    @Test
    public void TestSaveCarNegativeNotFullObject() {
        when(carRepository.findCarByNumber("u123ir")).thenReturn(Optional.empty());
        ResponseEntity<String> response = carService.saveCar(new CarRequest("u123ir", null));
        ResponseEntity<String> expected = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Car isn't full to be created");
        assertEquals(response, expected);
    }

    @Test
    public void TestUpdateCarPositive() {
        Person person = new Person(1L, "John", null, null, "smith");
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(carRepository.findCarByNumber("u123ir")).thenReturn(Optional.of(new Car(1L, "u123ir", TypeOfCar.USUAL_CAR, person, null)));
        when(personRepository.findByUsername("smith")).thenReturn(Optional.of(person));
        when(authentication.getName()).thenReturn("smith");
        ResponseEntity<String> response = carService.updateCar("u123ir", new CarRequest("u321ir", TypeOfCar.USUAL_CAR));
        ResponseEntity<String> expected = ResponseEntity.status(HttpStatus.CREATED).body("Car is updated successfully");
        assertEquals(response, expected);
    }

    @Test
    public void TestUpdateCarNegativeDBFail() {
        Person person = new Person(1L, "John", null, null, "smith");
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(carRepository.findCarByNumber("u123ir")).thenReturn(Optional.of(new Car(1L, "u123ir", TypeOfCar.USUAL_CAR, person, null)));
        when(personRepository.findByUsername("smith")).thenReturn(Optional.of(person));
        when(authentication.getName()).thenReturn("smith");
        ResponseEntity<String> response = carService.updateCar("u123ir", new CarRequest("u321ir", TypeOfCar.USUAL_CAR));
        ResponseEntity<String> expected = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Car isn't updated");
        assertEquals(response, expected);
    }

    @Test
    public void TestUpdateCarNegativeNoCar() {
        Person person = new Person(1L, "John", null, null, "smith");
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(carRepository.findCarByNumber("u123ir")).thenReturn(Optional.of(new Car(1L, "u123ir", TypeOfCar.USUAL_CAR, person, null)));
        when(personRepository.findByUsername("smith")).thenReturn(Optional.of(person));
        when(authentication.getName()).thenReturn("smith");
        ResponseEntity<String> response = carService.updateCar("wrong number", new CarRequest("u321ir", TypeOfCar.USUAL_CAR));
        ResponseEntity<String> expected = ResponseEntity.status(HttpStatus.NOT_FOUND).body("No such car");
        assertEquals(response, expected);
    }

    @Test
    public void TestUpdateCarNegativeWrongCar() {
        Person person = new Person(1L, "John", null, null, "smith");
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(carRepository.findCarByNumber("u123ir")).thenReturn(Optional.of(new Car(1L, "u123ir", TypeOfCar.USUAL_CAR, person, null)));
        when(personRepository.findByUsername("smith")).thenReturn(Optional.of(new Person(1L, "Adam", List.of(RulesBreaks.WRONG_PARKING), null, "smith")));
        when(authentication.getName()).thenReturn("smith");
        ResponseEntity<String> response = carService.updateCar("u123ir", new CarRequest("u321ir", TypeOfCar.USUAL_CAR));
        ResponseEntity<String> expected = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("It's not your car");
        assertEquals(response, expected);
    }

    @Test
    public void TestDeleteCarPositive() {
        Person person = new Person(1L, "John", null, null, "smith");
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn("smith");
        when(personRepository.findByUsername("smith")).thenReturn(Optional.of(person));
        when(carRepository.findCarByNumber("u123ir")).thenReturn(Optional.of(new Car(1L, "u123ir", TypeOfCar.USUAL_CAR, person, null)));
        ResponseEntity<Car> response = carService.deleteCar("u123ir");
        ResponseEntity<Car> expected = ResponseEntity.status(HttpStatus.OK).body(new Car(1L, "u123ir", TypeOfCar.USUAL_CAR, person, null));
        assertEquals(response, expected);
    }

    @Test
    public void TestDeleteCarNegativeNoCar() {
        Person person = new Person(1L, "John", null, null, "smith");
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn("smith");
        when(personRepository.findByUsername("smith")).thenReturn(Optional.of(person));
        when(carRepository.findCarByNumber("u123ir")).thenReturn(Optional.empty());
        ResponseEntity<Car> response = carService.deleteCar("u123ir");
        ResponseEntity<Car> expected = ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        assertEquals(response, expected);
    }

    @Test
    public void TestDeleteCarNegativeWrongPerson() {
        Person person = new Person(1L, "John", null, null, "smith");
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn("smith");
        when(personRepository.findByUsername("smith")).thenReturn(Optional.of(person));
        when(carRepository.findCarByNumber("u123ir")).thenReturn(Optional.of(new Car(1L, "u123ir", TypeOfCar.USUAL_CAR, new Person(1L, "Adam", null, null, "smith"), null)));
        ResponseEntity<Car> response = carService.deleteCar("u123ir");
        ResponseEntity<Car> expected = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        assertEquals(response, expected);
    }

    @Test
    public void TestGetCarPositive() {
        Person person = new Person(1L, "John", null, null, "smith");
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn("smith");
        when(personRepository.findByUsername("smith")).thenReturn(Optional.of(person));
        when(carRepository.findCarByNumber("u123ir")).thenReturn(Optional.of(new Car(1L, "u123ir", TypeOfCar.USUAL_CAR, person, null)));
        ResponseEntity<Car> response = carService.getCar("u123ir");
        ResponseEntity<Car> expected = ResponseEntity.status(HttpStatus.OK).body(new Car(1L, "u123ir", TypeOfCar.USUAL_CAR, person, null));
        assertEquals(response, expected);
    }

    @Test
    public void TestGetCarNegativeNoCar() {
        Person person = new Person(1L, "John", null, null, "smith");
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
        Person person = new Person(1L, "John", null, null, "smith");
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn("smith");
        when(personRepository.findByUsername("smith")).thenReturn(Optional.of(person));
        when(carRepository.findCarByNumber("u123ir")).thenReturn(Optional.of(new Car(1L, "u123ir", TypeOfCar.USUAL_CAR, new Person(1L, "Adam", null, null, "smith"), null)));
        ResponseEntity<Car> response = carService.getCar("u123ir");
        ResponseEntity<Car> expected = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        assertEquals(response, expected);
    }

    @Test
    public void TestGetCarsBookingRecordPositive() {
        Person person = new Person(1L, "John", null, null, "smith");
        UUID registrationNumber = UUID.randomUUID();
        BookingRecord bookingRecord = new BookingRecord(1L, null, null, LocalDateTime.now(), LocalDateTime.now().plusHours(8L), registrationNumber, 1600, UUID.randomUUID());
        Car car = new Car(1L, "u123ir", TypeOfCar.USUAL_CAR, person, Set.of(bookingRecord));
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
        Person person = new Person(1L, "John", null, null, "smith");
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
        Person person = new Person(1L, "John", null, null, "smith");
        UUID registrationNumber = UUID.randomUUID();
        BookingRecord bookingRecord = new BookingRecord(1L, null, null, LocalDateTime.now(), LocalDateTime.now().plusHours(8L), registrationNumber, 1600, UUID.randomUUID());
        Car car = new Car(1L, "u123ir", TypeOfCar.USUAL_CAR, new Person(2L, "Adam", null, null, "smith2"), Set.of(bookingRecord));
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
