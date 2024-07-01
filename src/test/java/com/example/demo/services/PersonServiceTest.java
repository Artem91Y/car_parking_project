package com.example.demo.services;

import com.example.demo.dtos.PersonRequest;
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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class PersonServiceTest {
    @InjectMocks
    private PersonService personService;

    @Mock
    private PersonRepository personRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private CarRepository carRepository;

    public PersonServiceTest() {
        MockitoAnnotations.initMocks(this);
    }
    @Test
    public void TestSavePersonPositive(){
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(carRepository.findCarByNumber("u123ir")).thenReturn(Optional.of(new Car(1L, "u123ir", TypeOfCar.USUAL_CAR, null, null)));
        when(personRepository.findByUsername("smith")).thenReturn(Optional.empty());
        when(authentication.getName()).thenReturn("smith");
        ResponseEntity<String> response = personService.savePerson(new PersonRequest("John", List.of("u123ir")));
        ResponseEntity<String> expected = ResponseEntity.status(HttpStatus.CREATED)
                .body("Person is created successfully");
        assertEquals(response, expected);
    }

    @Test
    public void TestSavePersonNegativeNotFullPerson(){
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(carRepository.findCarByNumber("u123ir")).thenReturn(Optional.of(new Car(1L, "u123ir", TypeOfCar.USUAL_CAR, null, null)));
        when(personRepository.findByUsername("smith")).thenReturn(Optional.empty());
        when(authentication.getName()).thenReturn("smith");
        ResponseEntity<String> response = personService.savePerson(new PersonRequest("", List.of("u123ir")));
        ResponseEntity<String> expected = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Person isn't full to be created");
        assertEquals(response, expected);
    }

    @Test
    public void TestSavePersonNegativeExistingUser(){
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(carRepository.findCarByNumber("u123ir")).thenReturn(Optional.of(new Car(1L, "u123ir", TypeOfCar.USUAL_CAR, null, null)));
        when(authentication.getName()).thenReturn("smith");
        when(personRepository.findByUsername(anyString())).thenReturn(Optional.of(new Person(1L, "John", 0, null, List.of(new Car(1L, "u123ir", TypeOfCar.USUAL_CAR, null, null)), "smith")));
        ResponseEntity<String> response = personService.savePerson(new PersonRequest("John",List.of("u123ir")));
        ResponseEntity<String> expected = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("You can not create one more account");
        assertEquals(response, expected);
    }


    @Test
    public void TestUpdatePersonPositive(){
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(carRepository.findCarByNumber("u123ir")).thenReturn(Optional.of(new Car(1L, "u123ir", TypeOfCar.USUAL_CAR, null, null)));
        when(authentication.getName()).thenReturn("smith");
        when(personRepository.findByUsername(anyString())).thenReturn(Optional.of(new Person(1L, "John", 0, null, List.of(new Car(1L, "u123ir", TypeOfCar.USUAL_CAR, null, null)), "smith")));
        ResponseEntity<String> response = personService.updatePerson(new PersonRequest("Nick", List.of("u123ir")));
        ResponseEntity<String> expected = ResponseEntity.status(HttpStatus.CREATED)
                .body("Person is updated successfully");
        assertEquals(response, expected);
    }

    @Test
    public void TestUpdatePersonNegativeNoUpdatedPerson(){
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(carRepository.findCarByNumber("u123ir")).thenReturn(Optional.of(new Car(1L, "u123ir", TypeOfCar.USUAL_CAR, null, null)));
        when(authentication.getName()).thenReturn("smith");
        when(personRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        ResponseEntity<String> response = personService.updatePerson(new PersonRequest("Nick", List.of("u123ir")));
        ResponseEntity<String> expected = ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("No such person");
        assertEquals(response, expected);
    }

    @Test
    public void TestUpdatePersonNegativeNoCar(){
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(carRepository.findCarByNumber("u123ir")).thenReturn(Optional.empty());
        when(authentication.getName()).thenReturn("smith");
        when(personRepository.findByUsername(anyString())).thenReturn(Optional.of(new Person(1L, "John", 0, null, List.of(new Car(1L, "u123ir", TypeOfCar.USUAL_CAR, null, null)), "smith")));
        ResponseEntity<String> response = personService.updatePerson(new PersonRequest("Nick", List.of("u123ir")));
        ResponseEntity<String> expected = ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("No such car");
        assertEquals(response, expected);
    }

    @Test
    public void TestDeletePersonPositive(){
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(personRepository.findByUsername("smith")).thenReturn(Optional.of(new Person(1L, "John", 0, null, List.of(new Car(1L, "u123ir", TypeOfCar.USUAL_CAR, null, null)), "smith")));
        when(authentication.getName()).thenReturn("smith");
        ResponseEntity<Person> response = personService.deletePerson();
        ResponseEntity<Person> expected = ResponseEntity.status(HttpStatus.OK)
                .body(new Person(1L, "John", 0, null, List.of(new Car(1L, "u123ir", TypeOfCar.USUAL_CAR, null, null)), "smith"));
        assertEquals(response, expected);
    }


    @Test
    public void TestDeletePersonNegativeFail(){
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(personRepository.findByUsername("smith")).thenReturn(Optional.empty());
        when(authentication.getName()).thenReturn("smith");
        ResponseEntity<Person> response = personService.deletePerson();
        ResponseEntity<Person> expected = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .build();
        assertEquals(response, expected);
    }

    @Test
    public void TestGetPersonPositive(){
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(personRepository.findByUsername("smith")).thenReturn(Optional.of(new Person(1L, "John", 0, null, List.of(new Car(1L, "u123ir", TypeOfCar.USUAL_CAR, null, null)), "smith")));
        when(authentication.getName()).thenReturn("smith");
        ResponseEntity<Person> response = personService.getPerson();
        ResponseEntity<Person> expected = ResponseEntity.status(HttpStatus.OK)
                .body(new Person(1L, "John", 0, null, List.of(new Car(1L, "u123ir", TypeOfCar.USUAL_CAR, null, null)), "smith"));
        assertEquals(response, expected);
    }

    @Test
    public void TestGetPersonNegativeFail(){
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(personRepository.findByUsername("smith")).thenReturn(Optional.empty());
        when(authentication.getName()).thenReturn("smith");
        ResponseEntity<Person> response = personService.getPerson();
        ResponseEntity<Person> expected = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .build();
        assertEquals(response, expected);
    }

    @Test
    public void TestMakeAccountPositive(){
        when(personRepository.findByUsername(anyString())).thenReturn(Optional.of(new Person(1L, "John", 0, null, List.of(new Car(1L, "u123ir", TypeOfCar.USUAL_CAR, null, null)), "smith")));
        ResponseEntity<String> response = personService.makeAccount("smith", 1000, List.of(RulesBreaks.WRONG_PARKING));
        ResponseEntity<String> expected = ResponseEntity.status(HttpStatus.CREATED).body("Account is created successfully");
        assertEquals(expected, response);
    }

    @Test
    public void TestMakeAccountNegativeNoPerson(){
        when(personRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        ResponseEntity<String> response = personService.makeAccount("smith", 1000, List.of(RulesBreaks.WRONG_PARKING));
        ResponseEntity<String> expected = ResponseEntity.status(HttpStatus.NOT_FOUND).body("No such person");
        assertEquals(expected, response);
    }
}
