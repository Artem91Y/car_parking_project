package com.example.demo.models;

import com.example.demo.models.enums.RulesBreaks;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "person")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@EqualsAndHashCode
@ToString
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", unique = true)
    private String fullName;

    @Column(name = "rules_breaks")
    private List<RulesBreaks> rulesBreaks;

    @JsonManagedReference
    @OneToMany(mappedBy = "person")
    private List<Car> cars;

    @Column(unique = true)
    private String username;
}
