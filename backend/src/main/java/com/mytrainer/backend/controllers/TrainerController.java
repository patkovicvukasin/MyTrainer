package com.mytrainer.backend.controllers;

import com.mytrainer.backend.dto.SessionResponse;
import com.mytrainer.backend.model.Trainer;
import com.mytrainer.backend.repositories.TrainerRepository;
import com.mytrainer.backend.services.ReservationService;
import com.mytrainer.backend.services.SessionService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/trainers")
public class TrainerController {
    private final TrainerRepository repo;
    private final SessionService sessionService;
    private final ReservationService reservationService;

    public TrainerController(TrainerRepository repo, SessionService sessionService, ReservationService reservationService) {
        this.repo = repo;
        this.sessionService = sessionService;
        this.reservationService = reservationService;
    }

    public record TrainerInfo(Integer id, String name) {
    }

    @GetMapping
    public List<TrainerInfo> list() {
        return repo.findAll().stream().map(t -> new TrainerInfo(t.getId(), t.getName())).toList();
    }

    @GetMapping("/me")
    public TrainerInfo me() {
        Trainer trainer = (Trainer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return new TrainerInfo(trainer.getId(), trainer.getName());
    }

    @GetMapping("/{id}/sessions")
    public ResponseEntity<List<SessionResponse>> publicSessions(@PathVariable Integer id, @RequestParam String view, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        Trainer trainer = repo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Trainer not found"));
        List<SessionResponse> sessions = sessionService.getSessions(trainer, view, date);
        return ResponseEntity.ok(sessions);
    }

    @DeleteMapping("/me/reservations/{id}")
    public ResponseEntity<Void> forceCancel(@PathVariable Integer id) {
        reservationService.forceCancel(id);
        return ResponseEntity.noContent().build();
    }

}
