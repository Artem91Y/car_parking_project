package com.example.demo.controllers;

import com.example.demo.dtos.CarRequest;
import com.example.demo.models.Car;
import com.example.demo.models.enums.TypeOfCar;
import com.example.demo.repos.CarRepository;
import com.example.demo.services.CarService;
import com.example.demo.services.ParkingPlaceService;
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

import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
public class CarControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CarService carService;

    @MockBean
    private CarRepository carRepository;

    @MockBean
    private ParkingPlaceService parkingPlaceService;

    @Test
    @WithMockUser(username = "admin", password = "password", authorities = {"ADMIN"})
    public void TestSaveCarPositive() throws Exception {
        CarRequest carRequest = new CarRequest("u123ir", TypeOfCar.USUAL_CAR);
        ResponseEntity<String> response = ResponseEntity.status(HttpStatus.CREATED).body("Car is created successfully");
        when(carService.saveCar(carRequest)).thenReturn(response);
        mockMvc.perform(MockMvcRequestBuilders.post("/saveCar").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(carRequest)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().string("Car is created successfully"));
    }

    @Test
    @WithMockUser(username = "admin", password = "password", authorities = {"ADMIN"})
    public void TestSaveCarNegativeExistingCar() throws Exception {
        CarRequest carRequest = new CarRequest("u123ir", TypeOfCar.USUAL_CAR);
        ResponseEntity<String> response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("This car already exists");
        when(carService.saveCar(carRequest)).thenReturn(response);
        mockMvc.perform(MockMvcRequestBuilders.post("/saveCar").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(carRequest)))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().string("This car already exists"));
    }

    @Test
    @WithMockUser(username = "admin", password = "password", authorities = {"ADMIN"})
    public void TestSaveCarNegativeNotFullCar() throws Exception {
        CarRequest carRequest = new CarRequest("u123ir", TypeOfCar.USUAL_CAR);
        ResponseEntity<String> response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Car isn't full to be created");
        when(carService.saveCar(carRequest)).thenReturn(response);
        mockMvc.perform(MockMvcRequestBuilders.post("/saveCar").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(carRequest)))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().string("Car isn't full to be created"));
    }

    @Test
    @WithMockUser(username = "admin", password = "password", authorities = {"ADMIN"})
    public void TestUpdateCarPositive() throws Exception {
        CarRequest carRequest = new CarRequest("u123ir", TypeOfCar.USUAL_CAR);
        ResponseEntity<String> response = ResponseEntity.status(HttpStatus.CREATED).body("Car is updated successfully");
        when(carService.updateCar("u123ir", carRequest)).thenReturn(response);
        mockMvc.perform(MockMvcRequestBuilders.put("/updateCar").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(carRequest)).param("number", "u123ir"))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().string("Car is updated successfully"));
    }


    @Test
    @WithMockUser(username = "admin", password = "password", authorities = {"ADMIN"})
    public void TestUpdateCarNegativeWrongCar() throws Exception {
        CarRequest carRequest = new CarRequest("u123ir", TypeOfCar.USUAL_CAR);
        ResponseEntity<String> response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("It's not your car");
        when(carService.updateCar("u123ir", carRequest)).thenReturn(response);
        mockMvc.perform(MockMvcRequestBuilders.put("/updateCar").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(carRequest)).param("number", "u123ir"))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().string("It's not your car"));
    }

    @Test
    @WithMockUser(username = "admin", password = "password", authorities = {"ADMIN"})
    public void TestUpdateCarNegativeNoCar() throws Exception {
        CarRequest carRequest = new CarRequest("u123ir", TypeOfCar.USUAL_CAR);
        ResponseEntity<String> response = ResponseEntity.status(HttpStatus.NOT_FOUND).body("No such car");
        when(carService.updateCar("u123ir", carRequest)).thenReturn(response);
        mockMvc.perform(MockMvcRequestBuilders.put("/updateCar").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(carRequest)).param("number", "u123ir"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("No such car"));
    }

    @Test
    @WithMockUser(username = "admin", password = "password", authorities = {"ADMIN"})
    public void TestUpdateCarNegativeDBFail() throws Exception {
        CarRequest carRequest = new CarRequest("u123ir", TypeOfCar.USUAL_CAR);
        ResponseEntity<String> response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Car isn't updated");
        when(carService.updateCar("u123ir", carRequest)).thenReturn(response);
        mockMvc.perform(MockMvcRequestBuilders.put("/updateCar").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(carRequest)).param("number", "u123ir"))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().string("Car isn't updated"));
    }

    @Test
    @WithMockUser(username = "smith", password = "123", authorities = {"USER"})
    public void TestDeleteCarPositive() throws Exception {
        Car car = new Car(1L, "u123ir", TypeOfCar.USUAL_CAR, null, null);
        ResponseEntity<Car> response = ResponseEntity.status(HttpStatus.OK).body(car);
        when(carService.deleteCar("u123ir")).thenReturn(response);
        mockMvc.perform(MockMvcRequestBuilders.delete("/deleteCar").param("number", "u123ir"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(objectMapper.writeValueAsString(car)));
    }

    @Test
    @WithMockUser(username = "smith", password = "123", authorities = {"USER"})
    public void TestDeleteCarNegativeNoCar() throws Exception {
        ResponseEntity<Car> response = ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        when(carService.deleteCar("u123ir")).thenReturn(response);
        mockMvc.perform(MockMvcRequestBuilders.delete("/deleteCar").param("number", "u123ir"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string(""));
    }

    @Test
    @WithMockUser(username = "smith", password = "123", authorities = {"USER"})
    public void TestDeletePersonNegativeWrongPerson() throws Exception {
        ResponseEntity<Car> response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        when(carService.deleteCar("u123ir")).thenReturn(response);
        mockMvc.perform(MockMvcRequestBuilders.delete("/deleteCar").param("number", "u123ir"))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().string(""));
    }

    @Test
    @WithMockUser(username = "smith", password = "123", authorities = {"USER"})
    public void TestGetCarPositive() throws Exception {
        Car car = new Car(1L, "u123ir", TypeOfCar.USUAL_CAR, null, null);
        ResponseEntity<Car> response = ResponseEntity.status(HttpStatus.OK).body(car);
        when(carService.getCar("u123ir")).thenReturn(response);
        mockMvc.perform(MockMvcRequestBuilders.get("/getCar").param("number", "u123ir"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(objectMapper.writeValueAsString(car)));
    }

    @Test
    @WithMockUser(username = "smith", password = "123", authorities = {"USER"})
    public void TestGetCarNegativeNoCar() throws Exception {
        ResponseEntity<Car> response = ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        when(carService.getCar("u123ir")).thenReturn(response);
        mockMvc.perform(MockMvcRequestBuilders.get("/getCar").param("number", "u123ir"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string(""));
    }

    @Test
    @WithMockUser(username = "smith", password = "123", authorities = {"USER"})
    public void TestGetPersonNegativeWrongPerson() throws Exception {
        ResponseEntity<Car> response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        when(carService.getCar("u123ir")).thenReturn(response);
        mockMvc.perform(MockMvcRequestBuilders.get("/getCar").param("number", "u123ir"))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().string(""));
    }


}
