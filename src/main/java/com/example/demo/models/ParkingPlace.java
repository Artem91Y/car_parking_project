package com.example.demo.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "parking_place")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@EqualsAndHashCode
@ToString
public class ParkingPlace {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private int number;

    @OneToMany(mappedBy = "parkingPlace")
    @Column(name = "booking_records")
    private Set<BookingRecord> bookingRecords;

    private int pricePerHour;

}
