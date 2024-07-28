package com.example.demo.utils;

import java.time.DateTimeException;
import java.time.Duration;
import java.time.LocalDateTime;

public class CountDatesDifference {
    public static Duration countDateDiffFromStrings(LocalDateTime startTime, LocalDateTime endTime) {

        if (startTime.isBefore(LocalDateTime.now()) || endTime.isBefore(LocalDateTime.now())) {
            throw new DateTimeException("Past time");
        }
        Duration dateDiff = Duration.between(startTime, endTime);
        if (dateDiff.isNegative()) {
            throw new DateTimeException("Wrong dates");
        }
        return dateDiff;
    }
}
