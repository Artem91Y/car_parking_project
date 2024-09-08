package com.example.demo.services;

import com.example.demo.dtos.CarRequest;
import com.example.demo.dtos.ErrorException;
import com.example.demo.dtos.NotFoundException;
import com.example.demo.models.BookingRecord;
import com.example.demo.models.Car;
import com.example.demo.repos.CarRepository;
import com.example.demo.repos.PersonRepository;
import com.example.demo.utils.CheckTheOwnerOfTheCar;
import org.hibernate.annotations.Cascade;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class CarService {

    private final CarRepository carRepository;
    private final PersonRepository personRepository;

    public CarService(CarRepository carRepository, PersonRepository personRepository) {
        this.carRepository = carRepository;
        this.personRepository = personRepository;
    }

    public ResponseEntity<String> saveCar(CarRequest carRequest) {
        if (carRequest.getType() == null || carRequest.getNumber().isEmpty()) {
            throw new ErrorException("Car isn't full to be created");
        }
        if (carRepository.findCarByNumber(carRequest.getNumber()).isPresent()) {
            throw new ErrorException("This car already exists");
        }
        Car car = new Car();
        car.setType(carRequest.getType());
        car.setNumber(carRequest.getNumber());
        try {
            carRepository.save(car);
            return ResponseEntity.status(HttpStatus.CREATED).body("Car is created successfully");
        } catch (Exception e) {
            throw new ErrorException("Car isn't created");
        }
    }

    public ResponseEntity<String> updateCar(String number, CarRequest carRequest) {
        Optional<Car> carOptional = carRepository.findCarByNumber(number);
        if (carOptional.isEmpty()) {
            throw new NotFoundException("No such car");
        }
        Car car = carOptional.get();
        if (!CheckTheOwnerOfTheCar.CheckTheOwnerOfTheCarByContext(car, personRepository)) {
            throw new ErrorException("It's not your car");
        }
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
            throw new ErrorException("Car isn't updated");
        }
    }

    public ResponseEntity<Car> deleteCar(String number) {
        Optional<Car> carOptional = carRepository.findCarByNumber(number);
        if (carOptional.isEmpty()) {
            throw new NotFoundException("No such car");
        }
        if (!CheckTheOwnerOfTheCar.CheckTheOwnerOfTheCarByContext(carOptional.get(), personRepository)) {
            throw new ErrorException("It's not your car");
        }
        try {
            carRepository.delete(carOptional.get());
            return ResponseEntity.status(HttpStatus.OK).body(carOptional.get());
        } catch (Exception e) {
            throw new ErrorException("Car isn't deleted");
        }
    }

    public ResponseEntity<Car> getCar(String number){
        long startTime = System.nanoTime();
        Optional<Car> carOptional = findByNumber(number);
        long endTime = System.nanoTime();
        System.out.println((endTime - startTime) / 1000000);
        if (carOptional.isEmpty()) {
            System.out.println("here");
            throw new NotFoundException("No such car");
        }
        if (!CheckTheOwnerOfTheCar.CheckTheOwnerOfTheCarByContext(carOptional.get(), personRepository)) {
            throw new ErrorException("It's not your car");
        }
        return ResponseEntity.status(HttpStatus.OK).body(carOptional.get());
    }
    @Cacheable(value = "cars", key = "#number")
    public Optional<Car> findByNumber(String number){
        try {
            Thread.sleep(3000);
        }catch (Exception e){

        }
        return carRepository.findCarByNumber(number);
    }

    public ResponseEntity<Set<BookingRecord>> getCarsBookingRecords(String number) {
        try {
            Optional<Car> carOptional = carRepository.findCarByNumber(number);
            if (carOptional.isEmpty()) {
                throw new NotFoundException("No such car");

            }
            if (!CheckTheOwnerOfTheCar.CheckTheOwnerOfTheCarByContext(carOptional.get(), personRepository)) {
                throw new ErrorException("It's not your car");
            }
            return ResponseEntity.status(HttpStatus.OK).body(carOptional.get().getBookingRecords());
        } catch (Exception e) {
            throw new ErrorException("Car's booking record aren't got");

        }
    }


}