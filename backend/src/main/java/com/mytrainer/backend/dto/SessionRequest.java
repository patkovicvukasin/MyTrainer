package com.mytrainer.backend.dto;

import java.time.OffsetDateTime;

public class SessionRequest {
    private OffsetDateTime startTime;
    private int duration;

    public SessionRequest(){}
    public SessionRequest(OffsetDateTime startTime, int duration){
        this.startTime = startTime;
        this.duration = duration;
    }

    public OffsetDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(OffsetDateTime startTime) {
        this.startTime = startTime;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
