package com.example.demo.controllers;

import com.example.demo.dtos.PersonRequest;
import com.example.demo.models.Car;
import com.example.demo.models.Person;
import com.example.demo.models.enums.RulesBreaks;
import com.example.demo.repos.PersonRepository;
import com.example.demo.services.ParkingPlaceService;
import com.example.demo.services.PersonService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
public class PersonControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PersonService personService;

    @MockBean
    private PersonRepository personRepository;

    @MockBean
    private ParkingPlaceService parkingPlaceService;

    @Test
    @WithMockUser(username = "admin", password = "password", authorities = {"ADMIN"})
    public void TestSavePersonPositive() throws Exception {
        PersonRequest personRequest = new PersonRequest("Nicolas", List.of("u123ir"));
        ResponseEntity<String> response = ResponseEntity.status(HttpStatus.CREATED).body("Person is created successfully");
        when(personService.savePerson(personRequest)).thenReturn(response);
        mockMvc.perform(MockMvcRequestBuilders.post("/savePerson").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(personRequest)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().string("Person is created successfully"));
    }

    @Test
    @WithMockUser(username = "admin", password = "password", authorities = {"ADMIN"})
    public void TestSavePersonNegativeSecondAccount() throws Exception {
        PersonRequest personRequest = new PersonRequest("Nicolas", List.of("u123ir"));
        ResponseEntity<String> response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("You can not create one more account");
        when(personService.savePerson(personRequest)).thenReturn(response);
        mockMvc.perform(MockMvcRequestBuilders.post("/savePerson").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(personRequest)))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().string("You can not create one more account"));
    }

    @Test
    @WithMockUser(username = "admin", password = "password", authorities = {"ADMIN"})
    public void TestSavePersonNegativeFailDB() throws Exception {
        PersonRequest personRequest = new PersonRequest("Nicolas", List.of("u123ir"));
        ResponseEntity<String> response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Person isn't created");
        when(personService.savePerson(personRequest)).thenReturn(response);
        mockMvc.perform(MockMvcRequestBuilders.post("/savePerson").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(personRequest)))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().string("Person isn't created"));
    }

    @Test
    @WithMockUser(username = "admin", password = "password", authorities = {"ADMIN"})
    public void TestSavePersonNegativeNoCar() throws Exception {
        PersonRequest personRequest = new PersonRequest("Nicolas", List.of("u123ir"));
        ResponseEntity<String> response = ResponseEntity.status(HttpStatus.NOT_FOUND).body("No such car");
        when(personService.savePerson(personRequest)).thenReturn(response);
        mockMvc.perform(MockMvcRequestBuilders.post("/savePerson").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(personRequest)))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("No such car"));
    }

    @Test
    @WithMockUser(username = "admin", password = "password", authorities = {"ADMIN"})
    public void TestSavePersonNegativeNotFullObject() throws Exception {
        PersonRequest personRequest = new PersonRequest(null, null);
        ResponseEntity<String> response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Person isn't full to be saved");
        when(personService.savePerson(personRequest)).thenReturn(response);
        mockMvc.perform(MockMvcRequestBuilders.post("/savePerson").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(personRequest)))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().string("Person isn't full to be saved"));
    }

    @Test
    @WithMockUser(username = "admin", password = "password", authorities = {"ADMIN"})
    public void TestUpdatePersonPositive() throws Exception {
        PersonRequest personRequest = new PersonRequest("Nicolas", List.of("u123ir"));
        ResponseEntity<String> response = ResponseEntity.status(HttpStatus.CREATED).body("Person is updated successfully");
        when(personService.updatePerson(any(PersonRequest.class))).thenReturn(response);
        mockMvc.perform(MockMvcRequestBuilders.put("/updatePerson").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(personRequest)).param("fullName", "Nicolas"))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().string("Person is updated successfully"));
    }

    @Test
    @WithMockUser(username = "admin", password = "password", authorities = {"ADMIN"})
    public void TestUpdatePersonNegativeDBFail() throws Exception {
        PersonRequest personRequest = new PersonRequest("Nicolas", List.of("u123ir"));
        ResponseEntity<String> response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Person isn't updated");
        when(personService.updatePerson(any(PersonRequest.class))).thenReturn(response);
        mockMvc.perform(MockMvcRequestBuilders.put("/updatePerson").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(personRequest)).param("fullName", "Nicolas"))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().string("Person isn't updated"));
    }

    @Test
    @WithMockUser(username = "admin", password = "password", authorities = {"ADMIN"})
    public void TestUpdatePersonNegativeNotFoundObject() throws Exception {
        PersonRequest personRequest = new PersonRequest("Nicolas", List.of("u123ir"));
        ResponseEntity<String> response = ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("No such person");
        when(personService.updatePerson(any(PersonRequest.class))).thenReturn(response);
        mockMvc.perform(MockMvcRequestBuilders.put("/updatePerson").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(personRequest)).param("fullName", "Nicolas"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("No such person"));
    }


    @Test
    @WithMockUser(username = "smith", password = "123", authorities = {"USER"})
    public void TestDeletePersonPositive() throws Exception {
        Person person = new Person(1L, "Nicolas", List.of(RulesBreaks.WRONG_PARKING), List.of(new Car()), "smith");
        ResponseEntity<Person> response = ResponseEntity.status(HttpStatus.OK).body(person);
        when(personService.deletePerson()).thenReturn(response);
        mockMvc.perform(MockMvcRequestBuilders.delete("/deletePerson"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(objectMapper.writeValueAsString(person)));
    }

    @Test
    @WithMockUser(username = "smith", password = "password", authorities = {"ADMIN"})
    public void TestDeletePersonNegativeWrongName() throws Exception {
        ResponseEntity<Person> response = ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        when(personService.deletePerson()).thenReturn(response);
        mockMvc.perform(MockMvcRequestBuilders.delete("/deletePerson"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string(""));
    }

    @Test
    @WithMockUser(username = "smith", password = "123", authorities = {"USER"})
    public void TestGetPersonPositive() throws Exception {
        Person person = new Person(1L, "Nicolas", List.of(RulesBreaks.WRONG_PARKING), List.of(new Car()), "smith");
        ResponseEntity<Person> response = ResponseEntity.status(HttpStatus.OK).body(person);
        when(personService.getPerson()).thenReturn(response);
        mockMvc.perform(MockMvcRequestBuilders.get("/getInfoAboutYourAccount"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(objectMapper.writeValueAsString(person)));
    }

    @Test
    @WithMockUser(username = "smith", password = "password", authorities = {"ADMIN"})
    public void TestGetPersonNegativeWrongName() throws Exception {
        ResponseEntity<Person> response = ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        when(personService.getPerson()).thenReturn(response);
        mockMvc.perform(MockMvcRequestBuilders.get("/getInfoAboutYourAccount"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string(""));
    }

    @Test
    @WithMockUser(username = "admin", password = "password", authorities = {"ADMIN"})
    public void TestMakeAccountPositive() throws Exception {
        ResponseEntity<String> response = ResponseEntity.status(HttpStatus.CREATED).body("Account is created successfully");
        when(personService.addRulesBreaks(anyString(), anyList())).thenReturn(response);
        mockMvc.perform(MockMvcRequestBuilders.put("/makeAccount").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(List.of(RulesBreaks.WRONG_PARKING)))
                        .param("username", "admin").param("money", "1000"))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().string("Account is created successfully"));
    }


    @Test
    @WithMockUser(username = "admin", password = "password", authorities = {"ADMIN"})
    public void TestMakeAccountNegativeNoPerson() throws Exception {
        ResponseEntity<String> response = ResponseEntity.status(HttpStatus.NOT_FOUND).body("No such person");
        when(personService.addRulesBreaks(anyString(), anyList())).thenReturn(response);
        mockMvc.perform(MockMvcRequestBuilders.put("/makeAccount").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(List.of(RulesBreaks.WRONG_PARKING)))
                        .param("username", "admin").param("money", "1000"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("No such person"));
    }

    @Test
    @WithMockUser(username = "admin", password = "password", authorities = {"ADMIN"})
    public void TestMakeAccountNegativeDBFail() throws Exception {
        ResponseEntity<String> response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Account isn't created");
        when(personService.addRulesBreaks(anyString(), anyList())).thenReturn(response);
        mockMvc.perform(MockMvcRequestBuilders.put("/makeAccount").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(List.of(RulesBreaks.WRONG_PARKING)))
                        .param("username", "admin").param("money", "1000"))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().string("Account isn't created"));
    }
}
