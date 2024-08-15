package com.example.demo.models;

import com.example.demo.models.enums.TypeOfCar;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "car")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@EqualsAndHashCode(exclude = {"bookingRecords"})
@ToString(exclude = {"bookingRecords"})
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String number;

    private TypeOfCar type;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "person_id")
    private Person person;

    @JsonBackReference
    @OneToMany(mappedBy = "car")
    @Column(name = "booking_records")

    private Set<BookingRecord> bookingRecords;
}
