package com.mytrainer.backend.repositories;

import com.mytrainer.backend.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Integer> {
    List<Reservation> findByUserPhone(String phone);

    @Modifying
    @Query("DELETE FROM Reservation r WHERE r.session.startTime < :cutoff")
    void deleteBySessionStartTimeBefore(@Param("cutoff") OffsetDateTime cutoff);
}
