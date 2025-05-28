package com.mytrainer.backend.scheduling;

import com.mytrainer.backend.model.Trainer;
import com.mytrainer.backend.repositories.TrainerRepository;
import com.mytrainer.backend.services.SessionService;
import jakarta.annotation.PostConstruct;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.YearMonth;

@Component
public class SlotGenerator {

    private final SessionService sessionService;
    private final TrainerRepository trainerRepo;

    public SlotGenerator(SessionService sessionService, TrainerRepository trainerRepo) {
        this.sessionService = sessionService;
        this.trainerRepo = trainerRepo;
    }

    @PostConstruct
    public void initSlots() {
        YearMonth thisMonth = YearMonth.now();
        YearMonth nextMonth = thisMonth.plusMonths(1);
        for (Trainer t : trainerRepo.findAll()) {
            sessionService.createMonthlySlots(t, thisMonth);
            sessionService.createMonthlySlots(t, nextMonth);
        }
    }

    @Scheduled(cron = "0 0 0 1 * *", zone = "UTC")
    public void generateNextMonthSlots() {
        YearMonth target = YearMonth.now().plusMonths(1);
        for (Trainer t : trainerRepo.findAll()) {
            sessionService.createMonthlySlots(t, target);
        }
    }
}
