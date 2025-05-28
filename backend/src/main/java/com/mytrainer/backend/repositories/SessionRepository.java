package com.mytrainer.backend.repositories;

import com.mytrainer.backend.model.Session;
import com.mytrainer.backend.model.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;

public interface SessionRepository extends JpaRepository<Session, Integer> {
    List<Session> findByTrainerAndStartTimeBetween(
            Trainer trainer,
            OffsetDateTime from,
            OffsetDateTime to
    );

    boolean existsByTrainerAndStartTime(Trainer trainer, OffsetDateTime startTime);

    @Modifying
    @Query("DELETE FROM Session s WHERE s.startTime < :cutoff")
    void deleteByStartTimeBefore(@Param("cutoff") OffsetDateTime cutoff);

    @Query("""
              select s.startTime
              from Session s
              where s.trainer = :trainer
                and s.startTime >= :monthStart
                and s.startTime < :monthEnd
            """)
    List<OffsetDateTime> findStartTimesByTrainerAndPeriod(
            @Param("trainer") Trainer trainer,
            @Param("monthStart") OffsetDateTime monthStart,
            @Param("monthEnd") OffsetDateTime monthEnd);

}
