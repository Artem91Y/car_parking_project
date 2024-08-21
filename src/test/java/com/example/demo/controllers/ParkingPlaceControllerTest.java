package com.example.demo.controllers;

import com.example.demo.dtos.ParkingPlaceRequest;
import com.example.demo.models.BookingRecord;
import com.example.demo.models.ParkingPlace;
import com.example.demo.models.Person;
import com.example.demo.repos.ParkingPlaceRepository;
import com.example.demo.services.ParkingPlaceService;
import com.example.demo.utils.models.CardRequest;
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
import java.util.Set;
import java.util.UUID;

import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
public class ParkingPlaceControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ParkingPlaceService parkingPlaceService;

    @MockBean
    private ParkingPlaceRepository parkingPlaceRepository;

    @Test
    @WithMockUser(username = "admin", password = "password", authorities = {"ADMIN"})
    public void TestSaveParkingPlacePositive() throws Exception {
        ParkingPlaceRequest parkingPlace = new ParkingPlaceRequest(1, 200);
        ResponseEntity<String> response = ResponseEntity.status(HttpStatus.CREATED).body("Parking place is created successfully");
        when(parkingPlaceService.saveParkingPlace(parkingPlace)).thenReturn(response);
        mockMvc.perform(MockMvcRequestBuilders.post("/saveParkingPlace").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(parkingPlace)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().string("Parking place is created successfully"));
    }

    @Test
    @WithMockUser(username = "admin", password = "password", authorities = {"ADMIN"})
    public void TestSaveParkingPlaceNegativeNotFullRequest() throws Exception {
        ParkingPlaceRequest parkingPlace = new ParkingPlaceRequest(1, null);
        ResponseEntity<String> response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Parking place isn't full to be created");
        when(parkingPlaceService.saveParkingPlace(parkingPlace)).thenReturn(response);
        mockMvc.perform(MockMvcRequestBuilders.post("/saveParkingPlace").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(parkingPlace)))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().string("Parking place isn't full to be created"));
    }

    @Test
    @WithMockUser(username = "admin", password = "password", authorities = {"ADMIN"})
    public void TestSaveParkingPlaceNegativeDuplicateParkingPlace() throws Exception {
        ParkingPlaceRequest parkingPlace = new ParkingPlaceRequest(1, 200);
        ResponseEntity<String> response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Parking place isn't full to be created");
        when(parkingPlaceService.saveParkingPlace(parkingPlace)).thenReturn(response);
        mockMvc.perform(MockMvcRequestBuilders.post("/saveParkingPlace").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(parkingPlace)))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().string("Parking place isn't full to be created"));
    }

    @Test
    @WithMockUser(username = "admin", password = "password", authorities = {"ADMIN"})
    public void TestSaveParkingPlaceNegativeDBFail() throws Exception {
        ParkingPlaceRequest parkingPlace = new ParkingPlaceRequest(1, 200);
        ResponseEntity<String> response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Parking place isn't created");
        when(parkingPlaceService.saveParkingPlace(parkingPlace)).thenReturn(response);
        mockMvc.perform(MockMvcRequestBuilders.post("/saveParkingPlace").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(parkingPlace)))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().string("Parking place isn't created"));
    }

    @Test
    @WithMockUser(username = "admin", password = "password", authorities = {"ADMIN"})
    public void TestUpdateParkingPlacePositive() throws Exception {
        ParkingPlaceRequest parkingPlace = new ParkingPlaceRequest(1, 200);
        ResponseEntity<String> response = ResponseEntity.status(HttpStatus.CREATED).body("Parking place is updated");
        when(parkingPlaceService.updateParkingPlace(2, parkingPlace)).thenReturn(response);
        mockMvc.perform(MockMvcRequestBuilders.put("/updateParkingPlace/2").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(parkingPlace)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().string("Parking place is updated"));
    }

    @Test
    @WithMockUser(username = "admin", password = "password", authorities = {"ADMIN"})
    public void TestUpdateParkingPlaceNegativeNoParkingPlace() throws Exception {
        ParkingPlaceRequest parkingPlace = new ParkingPlaceRequest(1, 200);
        ResponseEntity<String> response = ResponseEntity.status(HttpStatus.NOT_FOUND).body("No such parking place");
        when(parkingPlaceService.updateParkingPlace(2, parkingPlace)).thenReturn(response);
        mockMvc.perform(MockMvcRequestBuilders.put("/updateParkingPlace/2").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(parkingPlace)))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("No such parking place"));
    }


    @Test
    @WithMockUser(username = "admin", password = "password", authorities = {"ADMIN"})
    public void TestUpdateParkingPlaceNegativeExistingParkingPlace() throws Exception {
        ParkingPlaceRequest parkingPlace = new ParkingPlaceRequest(1, 200);
        ResponseEntity<String> response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("This parking place is already exist");
        when(parkingPlaceService.updateParkingPlace(1, parkingPlace)).thenReturn(response);
        mockMvc.perform(MockMvcRequestBuilders.put("/updateParkingPlace/1").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(parkingPlace)))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().string("This parking place is already exist"));
    }

    @Test
    @WithMockUser(username = "admin", password = "password", authorities = {"ADMIN"})
    public void TestUpdateParkingPlaceNegativeDBFail() throws Exception {
        ParkingPlaceRequest parkingPlace = new ParkingPlaceRequest(1, 200);
        ResponseEntity<String> response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Parking place isn't updated");
        when(parkingPlaceService.updateParkingPlace(1, parkingPlace)).thenReturn(response);
        mockMvc.perform(MockMvcRequestBuilders.put("/updateParkingPlace/1").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(parkingPlace)))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().string("Parking place isn't updated"));
    }

    @Test
    @WithMockUser(username = "admin", password = "password", authorities = {"ADMIN"})
    public void TestDeleteParkingPlacePositive() throws Exception {
        ParkingPlace parkingPlace = new ParkingPlace(1L, 1, null, 200);
        ResponseEntity<String> response = ResponseEntity.status(HttpStatus.OK).body(parkingPlace.toString());
        when(parkingPlaceService.deleteParkingPlace(2)).thenReturn(response);
        mockMvc.perform(MockMvcRequestBuilders.delete("/deleteParkingPlace/2"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(parkingPlace.toString()));
    }

    @Test
    @WithMockUser(username = "admin", password = "password", authorities = {"ADMIN"})
    public void TestDeleteParkingPlaceNegativeNoParkingPlace() throws Exception {
        ResponseEntity<String> response = ResponseEntity.status(HttpStatus.NOT_FOUND).body("No such parking place");
        when(parkingPlaceService.deleteParkingPlace(2)).thenReturn(response);
        mockMvc.perform(MockMvcRequestBuilders.delete("/deleteParkingPlace/2"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("No such parking place"));
    }

    @Test
    @WithMockUser(username = "admin", password = "password", authorities = {"ADMIN"})
    public void TestDeleteParkingPlaceNegativeDBFail() throws Exception {
        ResponseEntity<String> response = ResponseEntity.status(HttpStatus.NOT_FOUND).body("No such parking place");
        when(parkingPlaceService.deleteParkingPlace(2)).thenReturn(response);
        mockMvc.perform(MockMvcRequestBuilders.delete("/deleteParkingPlace/2"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("No such parking place"));
    }

    @Test
    @WithMockUser(username = "admin", password = "password", authorities = {"ADMIN"})
    public void TestBuyParkingPlacePositive() throws Exception {
        ResponseEntity<String> response = ResponseEntity.status(HttpStatus.OK).body("Parking place is bought successfully");
        when(parkingPlaceService.buyParkingPlace("2024-09-05 09:00", "2024-09-05 15:00", "u123ir", 1, new CardRequest())).thenReturn(response);
        mockMvc.perform(((MockMvcRequestBuilders.put("/buyParkingPlace/1").param("startTime", "2024-09-05 09:00")).param("endTime", "2024-09-05 15:00")).param("carNumber", "u123ir").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(new CardRequest("5555555555554600", 2999, "01"))))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Parking place is bought successfully"));
    }

    @Test
    @WithMockUser(username = "admin", password = "password", authorities = {"ADMIN"})
    public void TestBuyParkingPlaceNegativeNoParkingPlace() throws Exception {
        ResponseEntity<String> response = ResponseEntity.status(HttpStatus.NOT_FOUND).body("No such parking place");
        when(parkingPlaceService.buyParkingPlace("2024-09-05 09:00", "2024-09-05 15:00", "u123ir", 1, new CardRequest())).thenReturn(response);
        mockMvc.perform(((MockMvcRequestBuilders.put("/buyParkingPlace/1").param("startTime", "2024-09-05 09:00")).param("endTime", "2024-09-05 15:00")).param("carNumber", "u123ir"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("No such parking place"));
    }

    @Test
    @WithMockUser(username = "admin", password = "password", authorities = {"ADMIN"})
    public void TestBuyParkingPlaceNegativeNoCar() throws Exception {
        ResponseEntity<String> response = ResponseEntity.status(HttpStatus.NOT_FOUND).body("No such car");
        when(parkingPlaceService.buyParkingPlace("2024-09-05 09:00", "2024-09-05 15:00", "u123ir", 1, new CardRequest())).thenReturn(response);
        mockMvc.perform(((MockMvcRequestBuilders.put("/buyParkingPlace/1").param("startTime", "2024-09-05 09:00")).param("endTime", "2024-09-05 15:00")).param("carNumber", "u123ir"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("No such car"));
    }

    @Test
    @WithMockUser(username = "admin", password = "password", authorities = {"ADMIN"})
    public void TestBuyParkingPlaceNegativePastTime() throws Exception {
        ResponseEntity<String> response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("You gave past time");
        when(parkingPlaceService.buyParkingPlace("2021-09-05 09:00", "2024-09-05 15:00", "u123ir", 1, new CardRequest())).thenReturn(response);
        mockMvc.perform(((MockMvcRequestBuilders.put("/buyParkingPlace/1").param("startTime", "2021-09-05 09:00")).param("endTime", "2024-09-05 15:00")).param("carNumber", "u123ir"))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().string("You gave past time"));
    }

    @Test
    @WithMockUser(username = "admin", password = "password", authorities = {"ADMIN"})
    public void TestBuyParkingPlaceNegativeNoMoney() throws Exception {
        Person person = new Person(1L, "John", null, null, "smith");
        ResponseEntity<String> response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("You haven't enough money");
        when(parkingPlaceService.buyParkingPlace("2024-09-05 09:00", "2024-09-05 15:00", "u123ir", 1, new CardRequest())).thenReturn(response);
        mockMvc.perform(((MockMvcRequestBuilders.put("/buyParkingPlace/1").param("startTime", "2024-09-05 09:00")).param("endTime", "2024-09-05 15:00")).param("carNumber", "u123ir"))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().string("You haven't enough money"));
    }

    @Test
    @WithMockUser(username = "admin", password = "password", authorities = {"ADMIN"})
    public void TestBuyParkingPlaceNegativeWrongTime() throws Exception {
        Person person = new Person(1L, "John", null, null, "smith");
        ResponseEntity<String> response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("You gave wrong time");
        when(parkingPlaceService.buyParkingPlace("2024-09-05 09:00", "2024-09-05 15:00", "u123ir", 1, new CardRequest())).thenReturn(response);
        mockMvc.perform(((MockMvcRequestBuilders.put("/buyParkingPlace/1").param("startTime", "2024-09-05 09:00")).param("endTime", "2024-09-05 15:00")).param("carNumber", "u123ir"))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().string("You gave wrong time"));
    }

    @Test
    @WithMockUser(username = "admin", password = "password", authorities = {"ADMIN"})
    public void TestBuyParkingPlaceNegativeBookedTime() throws Exception {
        Person person = new Person(1L, "John", null, null, "smith");
        ResponseEntity<String> response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("This time is already booked");
        when(parkingPlaceService.buyParkingPlace("2024-09-05 09:00", "2024-09-05 15:00", "u123ir", 1, new CardRequest())).thenReturn(response);
        mockMvc.perform(((MockMvcRequestBuilders.put("/buyParkingPlace/1").param("startTime", "2024-09-05 09:00")).param("endTime", "2024-09-05 15:00")).param("carNumber", "u123ir"))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().string("This time is already booked"));
    }

    @Test
    @WithMockUser(username = "admin", password = "password", authorities = {"ADMIN"})
    public void TestGetParkingPlacePositive() throws Exception {
        ParkingPlace parkingPlace = new ParkingPlace(1L, 1, null, 200);
        ResponseEntity<String> response = ResponseEntity.status(HttpStatus.OK).body(parkingPlace.toString());
        when(parkingPlaceService.getParkingPlace(2)).thenReturn(response);
        mockMvc.perform(MockMvcRequestBuilders.get("/getParkingPlace/2"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(parkingPlace.toString()));
    }

    @Test
    @WithMockUser(username = "admin", password = "password", authorities = {"ADMIN"})
    public void TestGetParkingPlaceNegativeNoParkingPlace() throws Exception {
        ResponseEntity<String> response = ResponseEntity.status(HttpStatus.NOT_FOUND).body("No such parking place");
        when(parkingPlaceService.getParkingPlace(2)).thenReturn(response);
        mockMvc.perform(MockMvcRequestBuilders.get("/getParkingPlace/2"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("No such parking place"));
    }

    @Test
    @WithMockUser(username = "admin", password = "password", authorities = {"ADMIN"})
    public void TestGetParkingPlaceNegativeDBFail() throws Exception {
        ResponseEntity<String> response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        when(parkingPlaceService.getParkingPlace(2)).thenReturn(response);
        mockMvc.perform(MockMvcRequestBuilders.get("/getParkingPlace/2"))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().string(""));
    }

    @Test
    @WithMockUser(username = "admin", password = "password", authorities = {"ADMIN"})
    public void TestDeleteBookingRecordPositive() throws Exception {
        UUID registrationNumber = UUID.randomUUID();
        BookingRecord bookingRecord = new BookingRecord(1L, null, null, null, null, registrationNumber, 0, UUID.randomUUID());
        ResponseEntity<BookingRecord> response = ResponseEntity.status(HttpStatus.OK).body(bookingRecord);
        when(parkingPlaceService.deleteBookingRecord(registrationNumber)).thenReturn(response);
        mockMvc.perform(MockMvcRequestBuilders.delete("/deleteBookingRecord").param("registrationNumber", registrationNumber.toString()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(objectMapper.writeValueAsString(bookingRecord)));
    }


    @Test
    @WithMockUser(username = "admin", password = "password", authorities = {"ADMIN"})
    public void TestDeleteBookingRecordNegativeNoBookingRecord() throws Exception {
        UUID registrationNumber = UUID.randomUUID();
        ResponseEntity<BookingRecord> response = ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        when(parkingPlaceService.deleteBookingRecord(registrationNumber)).thenReturn(response);
        mockMvc.perform(MockMvcRequestBuilders.delete("/deleteBookingRecord").param("registrationNumber", registrationNumber.toString()))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string(""));
    }


    @Test
    @WithMockUser(username = "admin", password = "password", authorities = {"ADMIN"})
    public void TestDeleteBookingRecordNegativeDBFail() throws Exception {
        UUID registrationNumber = UUID.randomUUID();
        ResponseEntity<BookingRecord> response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        when(parkingPlaceService.deleteBookingRecord(registrationNumber)).thenReturn(response);
        mockMvc.perform(MockMvcRequestBuilders.delete("/deleteBookingRecord").param("registrationNumber", registrationNumber.toString()))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().string(""));
    }

    @Test
    @WithMockUser(username = "admin", password = "password", authorities = {"ADMIN"})
    public void TestGetParkingPlaceBookingRecordPositive() throws Exception {
        UUID registrationNumber = UUID.randomUUID();
        BookingRecord bookingRecord = new BookingRecord(1L, null, null, null, null, registrationNumber, 0, UUID.randomUUID());
        ResponseEntity<Set<BookingRecord>> response = ResponseEntity.status(HttpStatus.OK).body(Set.of(bookingRecord));
        when(parkingPlaceService.getParkingPlacesBookingRecords(1)).thenReturn(response);
        mockMvc.perform(MockMvcRequestBuilders.get("/getParkingPlacesBookingRecords/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(objectMapper.writeValueAsString(Set.of(bookingRecord))));
    }


    @Test
    @WithMockUser(username = "admin", password = "password", authorities = {"ADMIN"})
    public void TestGetParkingPlaceBookingRecordNegativeNoBookingRecord() throws Exception {
        ResponseEntity<Set<BookingRecord>> response = ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        when(parkingPlaceService.getParkingPlacesBookingRecords(1)).thenReturn(response);
        mockMvc.perform(MockMvcRequestBuilders.get("/getParkingPlacesBookingRecords/1"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string(""));
    }


    @Test
    @WithMockUser(username = "admin", password = "password", authorities = {"ADMIN"})
    public void TestGetParkingPlaceBookingRecordNegativeDBFail() throws Exception {
        ResponseEntity<Set<BookingRecord>> response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        when(parkingPlaceService.getParkingPlacesBookingRecords(1)).thenReturn(response);
        mockMvc.perform(MockMvcRequestBuilders.get("/getParkingPlacesBookingRecords/1"))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().string(""));
    }
}
