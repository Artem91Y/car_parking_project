package com.example.demo.services;

import com.example.demo.dtos.CarRequest;
import com.example.demo.models.BookingRecord;
import com.example.demo.models.Car;
import com.example.demo.repos.CarRepository;
import com.example.demo.repos.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.example.demo.utils.CheckTheOwnerOfTheCar;

import java.util.Optional;
import java.util.Set;

@Service
public class CarService {

    @Autowired
    private final CarRepository carRepository;
    @Autowired
    private final PersonRepository personRepository;

    public CarService(CarRepository carRepository, PersonRepository personRepository) {
        this.carRepository = carRepository;
        this.personRepository = personRepository;
    }

    public ResponseEntity<String> saveCar(CarRequest carRequest) {
        if (carRequest.getType() == null || carRequest.getNumber().isEmpty()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Car isn't full to be created");
        }
        if (carRepository.findCarByNumber(carRequest.getNumber()).isPresent()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("This car already exists");
        }
        Car car = new Car();
        car.setType(carRequest.getType());
        car.setNumber(carRequest.getNumber());
        try {
            carRepository.save(car);
            return ResponseEntity.status(HttpStatus.CREATED).body("Car is created successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Car isn't created");
        }
    }

    public ResponseEntity<String> updateCar(String number, CarRequest carRequest) {
        if (carRepository.findCarByNumber(number).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No such car");
        }
        if (!CheckTheOwnerOfTheCar.CheckTheOwnerOfTheCarByContext(carRepository.findCarByNumber(number).get(), personRepository)) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("It's not your car");
        }
        Car car = carRepository.findCarByNumber(number).get();
        if (!(carRequest.getNumber().isEmpty())) {
            car.setNumber(carRequest.getNumber());
        }
        if (!(carRequest.getType() == null)) {
            car.setType(carRequest.getType());
        }
        try {
            carRepository.save(car);
            return ResponseEntity.status(HttpStatus.CREATED).body("Car is updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Car isn't updated");
        }
    }

    public ResponseEntity<Car> deleteCar(String number) {
        Optional<Car> carOptional = carRepository.findCarByNumber(number);
        if (carOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        if (!CheckTheOwnerOfTheCar.CheckTheOwnerOfTheCarByContext(carOptional.get(), personRepository)){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        try {
            carRepository.delete(carOptional.get());
            return ResponseEntity.status(HttpStatus.OK).body(carOptional.get());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public ResponseEntity<Car> getCar(String number) {
        try {
            Optional<Car> carOptional = carRepository.findCarByNumber(number);
            if (carOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            if (!CheckTheOwnerOfTheCar.CheckTheOwnerOfTheCarByContext(carOptional.get(), personRepository)) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
            return ResponseEntity.status(HttpStatus.OK).body(carOptional.get());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public ResponseEntity<Set<BookingRecord>> getCarsBookingRecords(String number) {
        try {
            Optional<Car> carOptional = carRepository.findCarByNumber(number);
            if (carOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            if (!CheckTheOwnerOfTheCar.CheckTheOwnerOfTheCarByContext(carOptional.get(), personRepository)) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
            return ResponseEntity.status(HttpStatus.OK).body(carOptional.get().getBookingRecords());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


}