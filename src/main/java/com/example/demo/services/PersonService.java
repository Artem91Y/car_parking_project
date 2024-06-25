package com.example.demo.services;

import com.example.demo.dtos.PersonRequest;
import com.example.demo.models.Car;
import com.example.demo.models.Person;
import com.example.demo.models.enums.RulesBreaks;
import com.example.demo.repos.CarRepository;
import com.example.demo.repos.PersonRepository;
import org.apache.tomcat.util.digester.Rules;
import org.hibernate.query.sqm.produce.function.StandardFunctionReturnTypeResolvers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PersonService {
    @Autowired
    private final PersonRepository personRepository;

    @Autowired
    private final CarRepository carRepository;

    public PersonService(PersonRepository personRepository, CarRepository carRepository) {
        this.personRepository = personRepository;
        this.carRepository = carRepository;
    }

    public ResponseEntity<String> savePerson(PersonRequest personRequest) {
        if (personRequest.getFullName().isEmpty() || SecurityContextHolder.getContext().getAuthentication().getName().isEmpty() || personRequest.getNumbers().isEmpty()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Person isn't full to be created");
        }
        if (personRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName()).isPresent()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("You can not create one more account");
        }
        Person person = new Person();
        person.setFullName(personRequest.getFullName());
        person.setUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        List<Car> cars = new ArrayList<>();
        for (String number: personRequest.getNumbers()) {
            if (carRepository.findCarByNumber(number).isPresent()) {
                cars.add(carRepository.findCarByNumber(number).get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No such car");
            }
        }
        person.setCars(cars);
        try {
            personRepository.save(person);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Person is created successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Person isn't created");
        }
    }

    public ResponseEntity<String> updatePerson(PersonRequest personRequest) {
        if (personRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName()).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No such person");
        }
        Person person = personRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName()).get();
        if (!(personRequest.getNumbers().isEmpty())) {
            List<Car> cars = new ArrayList<>();
            for (String number: personRequest.getNumbers()) {
                if (carRepository.findCarByNumber(number).isPresent()) {
                    cars.add(carRepository.findCarByNumber(number).get());
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No such car");
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
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Person isn't updated");
        }
    }

    public ResponseEntity<Person> deletePerson() {
        try {
            Person person = personRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName()).get();
            personRepository.delete(person);
            return ResponseEntity.status(HttpStatus.OK).body(person);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public ResponseEntity<Person> getPerson() {
        try {
            Person person = personRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName()).get();
            return ResponseEntity.status(HttpStatus.OK).body(person);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public ResponseEntity<String> makeAccount(String username, int money, List<RulesBreaks> rulesBreaks) {
        if (personRepository.findByUsername(username).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No such person");
        }
        Person person = personRepository.findByUsername(username).get();
        person.setMoney(money);
        if (!rulesBreaks.isEmpty()){
            person.setRulesBreaks(rulesBreaks);
        }
        try {
            personRepository.save(person);
            return ResponseEntity.status(HttpStatus.CREATED).body("Account is created successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Account isn't created");
        }
    }
}
