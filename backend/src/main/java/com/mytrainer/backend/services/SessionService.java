package com.mytrainer.backend.services;

import com.mytrainer.backend.dto.SessionRequest;
import com.mytrainer.backend.dto.SessionResponse;
import com.mytrainer.backend.dto.ReservationInfo;
import com.mytrainer.backend.model.Session;
import com.mytrainer.backend.model.Trainer;
import com.mytrainer.backend.repositories.SessionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.*;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SessionService {
    private final SessionRepository sessionRepository;

    public SessionService(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    @Transactional(readOnly = true)
    public List<SessionResponse> getSessions(Trainer trainer, String view, LocalDate date) {
        OffsetDateTime from = date.atStartOfDay().atOffset(ZoneOffset.UTC);
        OffsetDateTime to   = "daily".equalsIgnoreCase(view)
                ? from.plusDays(1)
                : from.plusWeeks(1);

        return sessionRepository
                .findByTrainerAndStartTimeBetween(trainer, from, to)
                .stream()
                .map(s -> {
                    List<ReservationInfo> infos = s.getReservations().stream()
                            .map(r -> new ReservationInfo(
                                    r.getId(),
                                    r.getUser().getName(),
                                    r.getUser().getPhone(),
                                    r.getCreatedAt(),
                                    r.getStatus().name()
                            ))
                            .collect(Collectors.toList());
                    return new SessionResponse(
                            s.getId(),
                            s.getStartTime(),
                            s.getDuration(),
                            infos
                    );
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public SessionResponse createSession(Trainer trainer, SessionRequest req) {
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        if (req.getStartTime().isBefore(now)) {
            throw new IllegalArgumentException("Cannot schedule a session in the past");
        }

        OffsetDateTime start = req.getStartTime();
        int minute = start.getMinute();
        int duration = req.getDuration();

        if (minute != 0 && minute != 30) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Start time must be on the hour or half-hour"
            );
        }

        if (duration != 30 && duration != 60) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Duration must be either 30 or 60 minutes"
            );
        }

        OffsetDateTime end = start.plusMinutes(duration);
        List<Session> candidates = sessionRepository
                .findByTrainerAndStartTimeBetween(
                        trainer,
                        start.minusMinutes(60),
                        end
                );
        boolean overlap = candidates.stream().anyMatch(existing -> {
            OffsetDateTime existingStart = existing.getStartTime();
            OffsetDateTime existingEnd   = existingStart.plusMinutes(existing.getDuration());
            return existingStart.isBefore(end) && existingEnd.isAfter(start);
        });
        if (overlap) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Overlapping session exists"
            );
        }
        Session s = new Session();
        s.setTrainer(trainer);
        s.setStartTime(req.getStartTime());
        s.setDuration(req.getDuration());
        s = sessionRepository.save(s);

        return new SessionResponse(
                s.getId(),
                s.getStartTime(),
                s.getDuration(),
                Collections.emptyList()
        );
    }

    @Transactional
    public void deleteSession(Integer id) {
        sessionRepository.deleteById(id);
    }

    @Transactional
    public void generateSchedule(Trainer trainer, LocalDate startDate, LocalDate endDate) {
        for (LocalDate date = startDate;
             !date.isAfter(endDate);
             date = date.plusDays(1)) {

            LocalTime workStart, workEnd;
            DayOfWeek dow = date.getDayOfWeek();
            if (dow == DayOfWeek.SATURDAY) {
                workStart = LocalTime.of(10, 0);
                workEnd   = LocalTime.of(14, 0);
            } else if (dow == DayOfWeek.SUNDAY) {
                continue;
            } else {
                workStart = LocalTime.of(9, 0);
                workEnd   = LocalTime.of(17, 0);
            }

            LocalDateTime cursor = LocalDateTime.of(date, workStart);
            LocalDateTime lastSlot = LocalDateTime.of(date, workEnd).minusMinutes(30);
            while (!cursor.isAfter(lastSlot)) {
                OffsetDateTime slotTime = cursor.atOffset(ZoneOffset.UTC);
                try {
                    createSession(trainer, new SessionRequest(slotTime, 30));
                } catch (ResponseStatusException ex) {
                }
                cursor = cursor.plusMinutes(30);
            }
        }
    }

}
