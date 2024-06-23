package com.example.demo.services;

import com.example.demo.dtos.PersonRequest;
import com.example.demo.models.Car;
import com.example.demo.models.Person;
import com.example.demo.models.enums.TypeOfCar;
import com.example.demo.repos.PersonRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Test
    public void TestSavePersonPositive(){
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        Authentication authentication1 = mock(Authentication.class);
        when(authentication1.getName()).thenReturn("smith");
        when(personRepository.findByUsername(anyString())).thenReturn(null);
        ResponseEntity<String> response = personService.savePerson(new PersonRequest("John", List.of(new Car(1L, "u123ir", TypeOfCar.USUAL_CAR, null, null))));
        ResponseEntity<String> expected = ResponseEntity.status(HttpStatus.CREATED)
                .body("Person is created successfully");
        assertEquals(response, expected);
    }

    @Test
    public void TestSavePersonNegative(){
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn("smith");
        when(personRepository.findByUsername(anyString())).thenReturn(Optional.of(new Person(1L, "John", 10000, null, null, "smith")));
        ResponseEntity<String> response = personService.savePerson(new PersonRequest("John", List.of(new Car(1L, "u123ir", TypeOfCar.USUAL_CAR, null, null))));
        ResponseEntity<String> expected = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("You can not create one more account");
        assertEquals(response, expected);
    }


}
