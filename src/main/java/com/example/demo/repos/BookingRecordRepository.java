package com.example.demo.repos;

import com.example.demo.models.BookingRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface BookingRecordRepository extends JpaRepository<BookingRecord, Long> {
    Optional<BookingRecord> findByRegistrationNumber(UUID registrationNumber);

    Optional<BookingRecord> deleteByRegistrationNumber(UUID registrationNumber);

}
