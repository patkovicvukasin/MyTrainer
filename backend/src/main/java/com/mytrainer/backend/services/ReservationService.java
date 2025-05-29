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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReservationService {
    private final SessionRepository sessionRepo;
    private final AppUserRepository userRepo;
    private final ReservationRepository reservationRepo;
    private final Duration cancelWindow;
    private final Clock clock;

    public ReservationService(SessionRepository sessionRepo,
                              AppUserRepository userRepo,
                              ReservationRepository reservationRepo,
                              @Value("${CANCELLATION_WINDOW}") String cancelWindowIso,
                              Clock appClock) {
        this.sessionRepo = sessionRepo;
        this.userRepo = userRepo;
        this.reservationRepo = reservationRepo;
        this.cancelWindow = Duration.parse(cancelWindowIso);
        this.clock = appClock;
    }

    @Transactional
    public ReservationResponse reserve(ReservationRequest req) {
        Session session = sessionRepo.findById(req.getSessionId())
                .orElseThrow(() -> new IllegalArgumentException("Session not found"));

        AppUser user = userRepo.findByPhone(req.getPhone()).orElseGet(() -> {
            AppUser u = new AppUser();
            u.setName(req.getName());
            u.setPhone(req.getPhone());
            return userRepo.save(u);
        });

        Reservation res = new Reservation();
        res.setSession(session);
        res.setUser(user);
        res.setStatus(ReservationStatus.ACTIVE);
        res.setDuration(req.getDuration());
        res = reservationRepo.save(res);

        return new ReservationResponse(
                res.getId(),
                session.getId(),
                user.getName(),
                user.getPhone(),
                res.getSession().getStartTime(),
                res.getCreatedAt(),
                res.getStatus().name(),
                session.getDuration()
        );
    }

    @Transactional
    public void cancel(Integer reservationId) {
        Reservation res = reservationRepo.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));

        OffsetDateTime start = res.getSession().getStartTime();
        OffsetDateTime now = OffsetDateTime.now(clock);
        Duration untilStart = Duration.between(now, start);

        if (untilStart.compareTo(cancelWindow) < 0) {
            throw new IllegalStateException(
                    "Cannot cancel less than " + cancelWindow.toHours() + "h before session"
            );
        }

        res.setStatus(ReservationStatus.CANCELED);
        reservationRepo.save(res);
    }

    @Transactional
    public void forceCancel(Integer reservationId) {
        Reservation res = reservationRepo.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));
        res.setStatus(ReservationStatus.CANCELED);
        reservationRepo.save(res);
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> findByPhone(String phone) {
        // Prvo pokuša tačno poklapanje
        List<Reservation> reservations = reservationRepo.findByUserPhone(phone);

        // Ako ništa nije nađeno, pokuša sa/bez '+' prefiksa
        if (reservations.isEmpty()) {
            String alt = phone.startsWith("+") ? phone.substring(1) : "+" + phone;
            reservations = reservationRepo.findByUserPhone(alt);
        }

        return reservations.stream()
                .map(r -> new ReservationResponse(
                        r.getId(),
                        r.getSession().getId(),
                        r.getUser().getName(),
                        r.getUser().getPhone(),
                        r.getSession().getStartTime(),
                        r.getCreatedAt(),
                        r.getStatus().name(),
                        r.getSession().getDuration()
                ))
                .collect(Collectors.toList());
    }
}
