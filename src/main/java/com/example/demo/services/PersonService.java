package com.example.demo.services;

import com.example.demo.dtos.ErrorException;
import com.example.demo.dtos.NotFoundException;
import com.example.demo.dtos.PersonRequest;
import com.example.demo.models.Car;
import com.example.demo.models.Person;
import com.example.demo.models.enums.RulesBreaks;
import com.example.demo.repos.CarRepository;
import com.example.demo.repos.PersonRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PersonService {
    private final PersonRepository personRepository;
    private final CarRepository carRepository;

    public PersonService(PersonRepository personRepository, CarRepository carRepository) {
        this.personRepository = personRepository;
        this.carRepository = carRepository;
    }

    public ResponseEntity<String> savePerson(PersonRequest personRequest) {
        if (personRequest.getFullName().isEmpty() || SecurityContextHolder.getContext().getAuthentication().getName().isEmpty() || personRequest.getNumbers().isEmpty()) {
            throw new ErrorException("Person isn't full to be created");
        }
        if (personRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName()).isPresent()) {
            throw new ErrorException("You can not create one more account");
        }
        Person person = new Person();
        person.setFullName(personRequest.getFullName());
        person.setUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        List<Car> cars = new ArrayList<>();
        for (String number : personRequest.getNumbers()) {
            if (carRepository.findCarByNumber(number).isPresent()) {
                Car car = carRepository.findCarByNumber(number).get();
                car.setPerson(person);
                cars.add(car);
            } else {
                throw new NotFoundException("No such car");
            }
        }
        person.setCars(cars);
        try {
            personRepository.save(person);
            carRepository.saveAll(cars);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Person is created successfully");
        } catch (Exception e) {
            throw new ErrorException("Person isn't created");
        }
    }

    public ResponseEntity<String> updatePerson(PersonRequest personRequest) {
        if (personRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName()).isEmpty()) {
            throw new NotFoundException("No such person");
        }
        Person person = personRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName()).get();
        if (!(personRequest.getNumbers().isEmpty())) {
            List<Car> cars = new ArrayList<>();
            for (String number : personRequest.getNumbers()) {
                if (carRepository.findCarByNumber(number).isPresent()) {
                    cars.add(carRepository.findCarByNumber(number).get());
                } else {
                    throw new NotFoundException("No such car");
                }
            }
            person.setCars(cars);
        }
        if (!(personRequest.getFullName().isEmpty())) {
            person.setFullName(personRequest.getFullName());
        }
        try {
            personRepository.save(person);
            return ResponseEntity.status(HttpStatus.CREATED).body("Person is updated successfully");
        } catch (Exception e) {
            throw new ErrorException("Person isn't updated");
        }
    }

    public ResponseEntity<Person> deletePerson() {
        try {
            Person person = personRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName()).get();
            personRepository.delete(person);
            return ResponseEntity.status(HttpStatus.OK).body(person);
        } catch (Exception e) {
            throw new ErrorException("Person isn't deleted");
        }
    }

    public ResponseEntity<Person> getPerson() {
        try {
            Person person = personRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName()).get();
            return ResponseEntity.status(HttpStatus.OK).body(person);
        } catch (Exception e) {
            throw new ErrorException("Person isn't got");
        }
    }

    public ResponseEntity<String> addRulesBreaks(String username, List<RulesBreaks> rulesBreaks) {
        if (personRepository.findByUsername(username).isEmpty()) {
            throw new NotFoundException("No such person");
        }
        Person person = personRepository.findByUsername(username).get();
        if (!rulesBreaks.isEmpty()) {
            person.setRulesBreaks(rulesBreaks);
        }
        try {
            personRepository.save(person);
            return ResponseEntity.status(HttpStatus.CREATED).body("Rules breaks are added successfully");
        } catch (Exception e) {
            throw new ErrorException("Rules breaks aren't added");
        }
    }
}
