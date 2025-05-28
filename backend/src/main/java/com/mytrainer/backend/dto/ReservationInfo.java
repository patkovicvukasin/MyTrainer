// src/main/java/com/mytrainer/backend/dto/ReservationInfo.java
package com.mytrainer.backend.dto;

import java.time.OffsetDateTime;

public class ReservationInfo {
    private final Integer reservationId;
    private final String userName;
    private final String userPhone;
    private final OffsetDateTime createdAt;
    private final String status;

    public ReservationInfo(Integer reservationId, String userName, String userPhone, OffsetDateTime createdAt, String status) {
        this.reservationId = reservationId;
        this.userName = userName;
        this.userPhone = userPhone;
        this.createdAt = createdAt;
        this.status = status;
    }

    public Integer getReservationId() {
        return reservationId;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public String getStatus() {
        return status;
    }
}
