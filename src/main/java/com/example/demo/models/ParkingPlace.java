package com.example.demo.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "parking_place")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@EqualsAndHashCode(exclude = {"bookingRecords"})
@ToString
public class ParkingPlace {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private Integer number;

    @JsonBackReference
    @OneToMany(mappedBy = "parkingPlace", fetch = FetchType.EAGER)
    @Column(name = "booking_records")
    private Set<BookingRecord> bookingRecords;

    private Integer pricePerHour;

}
