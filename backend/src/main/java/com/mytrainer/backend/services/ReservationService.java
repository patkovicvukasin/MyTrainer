package com.mytrainer.backend.services;

import com.mytrainer.backend.dto.ReservationRequest;
import com.mytrainer.backend.dto.ReservationResponse;
import com.mytrainer.backend.model.AppUser;
import com.mytrainer.backend.model.Reservation;
import com.mytrainer.backend.model.ReservationStatus;
import com.mytrainer.backend.model.Session;
import com.mytrainer.backend.repositories.AppUserRepository;
import com.mytrainer.backend.repositories.ReservationRepository;
import com.mytrainer.backend.repositories.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;

@Service
public class ReservationService {
    @Autowired
    private final SessionRepository sessionRepo;
    private final AppUserRepository userRepo;
    private final ReservationRepository reservationRepo;
    private final Duration cancelWindow;

    public ReservationService(SessionRepository sessionRepo,
                              AppUserRepository userRepo,
                              ReservationRepository reservationRepo,
                              @Value("${CANCELLATION_WINDOW}") String cancelWindowIso) {
        this.sessionRepo = sessionRepo;
        this.userRepo = userRepo;
        this.reservationRepo = reservationRepo;
        this.cancelWindow = Duration.parse(cancelWindowIso);
    }

    @Transactional
    public ReservationResponse reserve(ReservationRequest req) {
        Session session = sessionRepo.findById(req.getSessionId())
                .orElseThrow(() -> new IllegalArgumentException("Session not found"));

        AppUser user = userRepo.findByPhone(req.getPhone())
                .orElseGet(() -> {
                    AppUser u = new AppUser();
                    u.setName(req.getName());
                    u.setPhone(req.getPhone());
                    return userRepo.save(u);
                });

        Reservation res = new Reservation();
        res.setSession(session);
        res.setUser(user);
        res.setStatus(ReservationStatus.ACTIVE);
        res = reservationRepo.save(res);

        return new ReservationResponse(
                res.getId(),
                session.getId(),
                user.getName(),
                user.getPhone(),
                res.getCreatedAt(),
                res.getStatus().name()
        );
    }

    @Transactional
    public void cancel(Integer reservationId) {
        Reservation res = reservationRepo.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));

        OffsetDateTime startsAt = res.getSession().getStartTime();
        if (startsAt.minus(cancelWindow).isBefore(OffsetDateTime.now(ZoneOffset.UTC))) {
            throw new IllegalStateException("Too late to cancel");
        }

        res.setStatus(ReservationStatus.CANCELED);
        reservationRepo.save(res);
    }
}
