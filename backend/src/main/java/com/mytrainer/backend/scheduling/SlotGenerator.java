package com.mytrainer.backend.scheduling;

import com.mytrainer.backend.model.Trainer;
import com.mytrainer.backend.repositories.TrainerRepository;
import com.mytrainer.backend.services.SessionService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.YearMonth;
import java.time.ZoneId;

@Component
public class SlotGenerator {

    private final SessionService sessionService;
    private final TrainerRepository trainerRepo;

    public SlotGenerator(SessionService sessionService, TrainerRepository trainerRepo) {
        this.sessionService = sessionService;
        this.trainerRepo = trainerRepo;
    }

    // Uklonjen @PostConstruct – više ne generišemo pri svakom pokretanju.

    /**
     * Svakog 15. u mesecu u ponoć (Europe/Belgrade)
     * generiše slotove za naredni mesec.
     */
    @Scheduled(cron = "0 0 0 15 * *", zone = "Europe/Belgrade")
    public void generateNextMonthSlots() {
        ZoneId zone = ZoneId.of("Europe/Belgrade");
        YearMonth next = YearMonth.now(zone).plusMonths(1);
        for (Trainer t : trainerRepo.findAll()) {
            sessionService.createMonthlySlots(t, next);
        }
    }
}
