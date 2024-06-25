package com.example.demo.controllers;

import com.example.demo.dtos.PersonRequest;
import com.example.demo.models.Person;
import com.example.demo.models.enums.RulesBreaks;
import com.example.demo.services.PersonService;
import org.apache.tomcat.util.digester.Rules;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PersonController {
    private final PersonService personService;

    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @GetMapping("/getInfoAboutYourAccount")
    public ResponseEntity<Person> getPerson(){
        return personService.getPerson();
    }

    @DeleteMapping("/deletePerson")
    public ResponseEntity<Person> deletePerson(){
        return personService.deletePerson();
    }

    @PostMapping("/savePerson")
    public ResponseEntity<String> savePerson(@RequestBody PersonRequest personRequest){
        return personService.savePerson(personRequest);
    }

    @PutMapping("/updatePerson")
    public ResponseEntity<String> updatePerson(@RequestBody PersonRequest personRequest){
        return personService.updatePerson(personRequest);
    }

    @PutMapping("/makeAccount")
    public ResponseEntity<String> makeAccount(@RequestParam String username, @RequestParam int money, @RequestBody List<RulesBreaks> rulesBreaks){
        return personService.makeAccount(username, money, rulesBreaks);
    }
}
