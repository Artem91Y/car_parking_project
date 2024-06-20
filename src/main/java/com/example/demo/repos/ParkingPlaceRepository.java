package com.example.demo.repos;

import com.example.demo.models.ParkingPlace;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParkingPlaceRepository  extends JpaRepository<ParkingPlace, Long> {
}
