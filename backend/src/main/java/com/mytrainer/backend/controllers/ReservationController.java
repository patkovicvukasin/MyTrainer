package com.mytrainer.backend.controllers;

import com.mytrainer.backend.dto.ReservationRequest;
import com.mytrainer.backend.dto.ReservationResponse;
import com.mytrainer.backend.services.ReservationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {
    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    public ResponseEntity<ReservationResponse> reserve(@Valid @RequestBody ReservationRequest req) {
        ReservationResponse resp = reservationService.reserve(req);
        return ResponseEntity.ok(resp);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancel(@PathVariable Integer id) {
        try {
            reservationService.cancel(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/me/reservations/{id}")
    public ResponseEntity<Void> forceCancel(@PathVariable Integer id) {
        reservationService.forceCancel(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public List<ReservationResponse> findByPhone(@RequestParam String phone) {
        return reservationService.findByPhone(phone);
    }

}
