package com.mytrainer.backend.repositories;

import com.mytrainer.backend.model.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TrainerRepository extends JpaRepository<Trainer, Integer> {
    Optional<Trainer> findByAccessCode(String accessCode);
    boolean existsByAccessCode(String accessCode);
}
