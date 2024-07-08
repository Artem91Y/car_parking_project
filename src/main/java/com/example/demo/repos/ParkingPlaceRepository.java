package com.example.demo.repos;

import com.example.demo.models.ParkingPlace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ParkingPlaceRepository extends JpaRepository<ParkingPlace, Long> {
    Optional<ParkingPlace> findByNumber(int number);
}
