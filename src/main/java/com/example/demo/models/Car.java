package com.example.demo.models;

import com.example.demo.models.enums.TypeOfCar;
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
    @ManyToOne
    @JoinColumn(name = "person_id")
    private Person person;

    @OneToMany(mappedBy = "car")
    @Column(name = "booking_records")
    private Set<BookingRecord> bookingRecords;
}
