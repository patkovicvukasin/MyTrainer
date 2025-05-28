package com.mytrainer.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class ReservationRequest {
    @NotNull(message = "Session ID is required")
    private Integer sessionId;

    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^\\+?[0-9]{6,20}$", message = "Phone must be valid format, 6-20 digits, optional leading +")
    private String phone;

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must be at most 100 characters")
    private String name;

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
}