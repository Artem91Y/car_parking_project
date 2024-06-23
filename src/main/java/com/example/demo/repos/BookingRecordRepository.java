package com.example.demo.repos;

import com.example.demo.models.BookingRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.awt.print.Book;
import java.util.Optional;
import java.util.UUID;

public interface BookingRecordRepository extends JpaRepository<BookingRecord, Long> {
    Optional<BookingRecord> findByRegistrationNumber(UUID registrationNumber);
    Optional<BookingRecord> deleteByRegistrationNumber(UUID registrationNumber);

//    Failed to create query for method public abstract java.util.Optional com.example.demo.repos.BookingRecordRepository.findByRegistrationNumber(java.util.UUID); Cannot compare left expression of type 'java.lang.Integer' with right expression of type 'java.util.UUID'

}
