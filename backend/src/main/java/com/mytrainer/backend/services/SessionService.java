package com.mytrainer.backend.services;

import com.mytrainer.backend.dto.ReservationInfo;
import com.mytrainer.backend.dto.SessionRequest;
import com.mytrainer.backend.dto.SessionResponse;
import com.mytrainer.backend.model.Session;
import com.mytrainer.backend.model.Trainer;
import com.mytrainer.backend.repositories.SessionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SessionService {
    private final SessionRepository sessionRepository;

    public SessionService(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    @Transactional(readOnly = true)
    public List<SessionResponse> getSessions(Trainer trainer, String view, LocalDate date) {
        YearMonth ym = YearMonth.from(date);
        // koristiti lokalnu zonu Europe/Belgrade
        ZoneId zone = ZoneId.of("Europe/Belgrade");

        // granice za ceo mesec
        OffsetDateTime monthStart = ym.atDay(1).atStartOfDay(zone).toOffsetDateTime();
        OffsetDateTime monthEnd   = ym.plusMonths(1).atDay(1).atStartOfDay(zone).toOffsetDateTime();

        // ako nema nijednog startTime u tom mesecu, generiši slotove
        if (sessionRepository.findStartTimesByTrainerAndPeriod(trainer, monthStart, monthEnd).isEmpty()) {
            createMonthlySlots(trainer, ym);
        }

        // dohvat samo dana (daily ili weekly)
        OffsetDateTime from = date.atStartOfDay(zone).toOffsetDateTime();
        OffsetDateTime to   = "daily".equalsIgnoreCase(view) ? from.plusDays(1) : from.plusWeeks(1);

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
                                    r.getStatus().name()))
                            .collect(Collectors.toList());
                    return new SessionResponse(s.getId(), s.getStartTime(), s.getDuration(), infos);
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
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Start time must be on the hour or half-hour");
        }

        if (duration != 30 && duration != 60) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Duration must be either 30 or 60 minutes");
        }

        OffsetDateTime end = start.plusMinutes(duration);
        List<Session> candidates = sessionRepository.findByTrainerAndStartTimeBetween(trainer, start.minusMinutes(60), end);
        boolean overlap = candidates.stream().anyMatch(existing -> {
            OffsetDateTime existingStart = existing.getStartTime();
            OffsetDateTime existingEnd = existingStart.plusMinutes(existing.getDuration());
            return existingStart.isBefore(end) && existingEnd.isAfter(start);
        });
        if (overlap) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Overlapping session exists");
        }
        Session s = new Session();
        s.setTrainer(trainer);
        s.setStartTime(req.getStartTime());
        s.setDuration(req.getDuration());
        s = sessionRepository.save(s);

        return new SessionResponse(s.getId(), s.getStartTime(), s.getDuration(), Collections.emptyList());
    }

    @Transactional
    public void deleteSession(Integer id) {
        sessionRepository.deleteById(id);
    }

    @Transactional
    public void createMonthlySlots(Trainer trainer, YearMonth month) {
        // koristiti lokalnu zonu Europe/Belgrade
        ZoneId zone = ZoneId.of("Europe/Belgrade");
        OffsetDateTime monthStart = month.atDay(1).atStartOfDay(zone).toOffsetDateTime();
        OffsetDateTime monthEnd   = month.plusMonths(1).atDay(1).atStartOfDay(zone).toOffsetDateTime();

        List<OffsetDateTime> existing = sessionRepository.findStartTimesByTrainerAndPeriod(trainer, monthStart, monthEnd);
        Set<OffsetDateTime> existingSet = new HashSet<>(existing);

        LocalTime first = LocalTime.of(9, 0);
        LocalTime lastSlotStart = LocalTime.of(17, 0).minusMinutes(30);

        for (int day = 1; day <= month.lengthOfMonth(); day++) {
            LocalDate date = month.atDay(day);
            if (date.getDayOfWeek() == DayOfWeek.SUNDAY) continue;

            LocalDateTime cursor = LocalDateTime.of(date, first);
            while (!cursor.toLocalTime().isAfter(lastSlotStart)) {
                OffsetDateTime slotTime = cursor.atZone(zone).toOffsetDateTime();
                if (!existingSet.contains(slotTime)) {
                    Session s = new Session();
                    s.setTrainer(trainer);
                    s.setStartTime(slotTime);
                    s.setDuration(30);
                    sessionRepository.save(s);
                }
                cursor = cursor.plusMinutes(30);
            }
        }
    }

}


