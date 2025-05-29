package com.mytrainer.backend.dto;

import jakarta.validation.constraints.*;

public class ReservationRequest {
    @NotNull(message = "Session ID is required")
    private Integer sessionId;

    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^\\+?[0-9]{6,20}$", message = "Phone must be valid format, 6-20 digits, optional leading +")
    private String phone;

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must be at most 100 characters")
    private String name;

    @NotNull(message = "Duration is required")
    @Min(value = 30, message = "Duration must be 30 or 60")
    @Max(value = 60, message = "Duration must be 30 or 60")
    private Integer duration;    // ← novo polje

    public Integer getSessionId() {
        return sessionId;
    }

    public void setSessionId(Integer sessionId) {
        this.sessionId = sessionId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }
}