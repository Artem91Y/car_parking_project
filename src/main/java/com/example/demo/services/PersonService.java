package com.example.demo.services;

import com.example.demo.dtos.PersonRequest;
import com.example.demo.models.Car;
import com.example.demo.models.Person;
import com.example.demo.repos.PersonRepository;
import com.example.demo.repos.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersonService {
    private final PersonRepository personRepository;

    private final UserRepository userRepository;

    public PersonService(PersonRepository personRepository, UserRepository userRepository) {
        this.personRepository = personRepository;
        this.userRepository = userRepository;
    }

    private ResponseEntity<String> savePerson(
            PersonRequest personRequest,
            String username,
            List<String> rulesBreaks,
            int money){
        if (personRequest.getFullName().isEmpty() || username.isEmpty()){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Person isn't full to be created");
        }
        if (personRepository.findByUsername(username).isPresent()){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("You can not create one more account");
        }
        Person person = new Person();
        person.setFullName(personRequest.getFullName());
        person.setUsername(username);
        person.setRulesBreaks(rulesBreaks);
        person.setMoney(money);
        person.setCars(personRequest.getCars());
        try{
            personRepository.save(person);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Person is created successfully");
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Person isn't created");
        }
    }

    private ResponseEntity<String> updatePerson(PersonRequest personRequest) {
        if (personRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName()).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No such person");
        }
        Person person = personRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName()).get();
        if (!(personRequest.getCars().isEmpty())) {
            person.setCars(personRequest.getCars());
        }
        if (!(personRequest.getFullName().isEmpty())) {
            person.setFullName(personRequest.getFullName());
        }
        try {
            personRepository.save(person);
            return ResponseEntity.status(HttpStatus.CREATED).body("Person is updated successfully");
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Person isn't updated");
        }
    }

    private ResponseEntity<Person> deletePerson(){
        try{
            Person person = personRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName()).get();
            personRepository.delete(person);
            return ResponseEntity.status(HttpStatus.OK).body(person);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private ResponseEntity<Person> getPerson(){
        try{
            Person person = personRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName()).get();
            return ResponseEntity.status(HttpStatus.OK).body(person);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
