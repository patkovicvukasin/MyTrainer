package com.mytrainer.backend.dto;

import java.time.LocalDate;

public record GenerateRequest(
        LocalDate startDate,
        LocalDate endDate
) {}
