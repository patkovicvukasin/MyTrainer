package com.mytrainer.backend.dto;

import java.time.OffsetDateTime;
import java.util.List;

public class SessionResponse {
    private final Integer id;
    private final OffsetDateTime startTime;
    private final int duration;
    private final List<ReservationInfo> reservations;

    public SessionResponse(Integer id, OffsetDateTime startTime, int duration, List<ReservationInfo> reservations) {
        this.id = id;
        this.startTime = startTime;
        this.duration = duration;
        this.reservations = reservations;
    }

    public Integer getId() {
        return id;
    }

    public OffsetDateTime getStartTime() {
        return startTime;
    }

    public int getDuration() {
        return duration;
    }

    public List<ReservationInfo> getReservations() {
        return reservations;
    }
}
