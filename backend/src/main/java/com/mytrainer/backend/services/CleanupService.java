package com.mytrainer.backend.services;

import com.mytrainer.backend.repositories.ReservationRepository;
import com.mytrainer.backend.repositories.SessionRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Service
public class CleanupService {
    private final SessionRepository sessionRepo;
    private final ReservationRepository reservationRepo;

    public CleanupService(SessionRepository sessionRepo,
                          ReservationRepository reservationRepo) {
        this.sessionRepo     = sessionRepo;
        this.reservationRepo = reservationRepo;
    }

    @Scheduled(cron = "0 0 0 * * *", zone = "UTC")
    @Transactional
    public void purgePastData() {
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        reservationRepo.deleteBySessionStartTimeBefore(now);
        sessionRepo.deleteByStartTimeBefore(now);
    }
}
