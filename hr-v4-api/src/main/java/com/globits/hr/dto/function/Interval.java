package com.globits.hr.dto.function;

import java.time.Duration;
import java.time.LocalDateTime;

public class Interval {
    private final LocalDateTime start;
    private final LocalDateTime end;

    public Interval(LocalDateTime start, LocalDateTime end) {
        if (end.isBefore(start)) {
            throw new IllegalArgumentException("End must not be before start");
        }
        this.start = start;
        this.end = end;
    }

    public Interval getIntersection(Interval other) {
        LocalDateTime latestStart = start.isAfter(other.start) ? start : other.start;
        LocalDateTime earliestEnd = end.isBefore(other.end) ? end : other.end;
        if (latestStart.isBefore(earliestEnd)) {
            return new Interval(latestStart, earliestEnd);
        }
        return null;
    }

    public long getDurationInMinutes() {
        return Duration.between(start, end).toMinutes();
    }

    public LocalDateTime getStart() {
        return start;
    }

    public LocalDateTime getEnd() {
        return end;
    }
}
