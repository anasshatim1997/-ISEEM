package com.iseem_backend.application.utils;

import jakarta.persistence.Convert;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class TimeSlot {
    @Convert(converter = DayOfWeekConverter.class)
    private DayOfWeek day;
    private LocalTime startTime;
    private LocalTime endTime;
}
