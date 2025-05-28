package com.mytrainer.backend.controllers;

import com.mytrainer.backend.dto.SessionRequest;
import com.mytrainer.backend.dto.SessionResponse;
import com.mytrainer.backend.model.Trainer;
import com.mytrainer.backend.services.SessionService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/trainers/me/sessions")
public class SessionController {
    private final SessionService sessionService;

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @GetMapping
    public ResponseEntity<List<SessionResponse>> getSessions(@RequestParam String view, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        Trainer trainer = (Trainer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<SessionResponse> list = sessionService.getSessions(trainer, view, date);
        return ResponseEntity.ok(list);
    }

    @PostMapping
    public ResponseEntity<SessionResponse> createSession(@Valid @RequestBody SessionRequest req) {
        Trainer trainer = (Trainer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        SessionResponse resp = sessionService.createSession(trainer, req);
        return ResponseEntity.ok(resp);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSession(@PathVariable Integer id) {
        sessionService.deleteSession(id);
        return ResponseEntity.noContent().build();
    }
}
