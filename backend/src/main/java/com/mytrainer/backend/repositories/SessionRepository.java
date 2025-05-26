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

    @Modifying
    @Query("DELETE FROM Session s WHERE s.startTime < :cutoff")
    void deleteByStartTimeBefore(@Param("cutoff") OffsetDateTime cutoff);
}
