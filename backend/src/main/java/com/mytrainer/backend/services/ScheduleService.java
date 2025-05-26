package com.mytrainer.backend.services;

import com.mytrainer.backend.repositories.TrainerRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;

@Service
public class ScheduleService {
    private SessionService sessionService;
    private TrainerRepository trainerRepo;

    public ScheduleService(SessionService sessionService, TrainerRepository trainerRepo) {
        this.sessionService = sessionService;
        this.trainerRepo = trainerRepo;
    }

    @Scheduled(cron = "0 0 0 * * SAT", zone = "Europe/Belgrade")
    public void weeklyGenerate() {
        LocalDate today = LocalDate.now(ZoneId.of("Europe/Belgrade"));
        LocalDate nextMonday = today.with(TemporalAdjusters.next(DayOfWeek.MONDAY));
        LocalDate nextSat = nextMonday.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY));
        trainerRepo.findAll().forEach(t ->
                sessionService.generateSchedule(t, nextMonday, nextSat)
        );
    }
}
