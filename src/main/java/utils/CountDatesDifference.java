package utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.DateTimeException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CountDatesDifference {
    public static Duration countDateDiffFromStrings(LocalDateTime startTime, LocalDateTime endTime){
        try {
            if (startTime.isBefore(LocalDateTime.now()) || endTime.isBefore(LocalDateTime.now())) {
                throw new DateTimeException("Past time");
            }
            Duration dateDiff = Duration.between(startTime, endTime);
            if (dateDiff.isNegative()) {
                throw new DateTimeException("Wrong dates");
            }
            return dateDiff;
        } catch (Exception e){
            throw new DateTimeException("Error while parsing dates");
        }
    }
}
