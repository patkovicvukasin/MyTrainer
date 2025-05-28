package com.mytrainer.backend.dto;

import java.time.OffsetDateTime;

public class ReservationResponse {
    private Integer id;
    private Integer sessionId;
    private String userName;
    private String userPhone;
    private OffsetDateTime startTime;
    private OffsetDateTime createdAt;
    private String status;

    public ReservationResponse(Integer id, Integer sessionId, String userName, String userPhone, OffsetDateTime startTime, OffsetDateTime createdAt, String status) {
        this.id = id;
        this.sessionId = sessionId;
        this.userName = userName;
        this.userPhone = userPhone;
        this.startTime = startTime;
        this.createdAt = createdAt;
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSessionId() {
        return sessionId;
    }

    public OffsetDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(OffsetDateTime startTime) {
        this.startTime = startTime;
    }

    public void setSessionId(Integer sessionId) {
        this.sessionId = sessionId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
