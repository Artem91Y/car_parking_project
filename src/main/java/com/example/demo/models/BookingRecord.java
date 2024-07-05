package com.example.demo.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "booking_record")
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"parkingPlace", "car"})
@Getter
@Setter
@ToString(exclude = {"parkingPlace"})
public class BookingRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Car car;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "parking_place")
    private ParkingPlace parkingPlace;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "registration_number")
    private UUID registrationNumber;

    private int price;

}
