package com.mytrainer.backend.services;

import com.mytrainer.backend.model.Trainer;
import com.mytrainer.backend.repositories.TrainerRepository;
import com.mytrainer.backend.security.JwtUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Service
public class AuthService {
    private final TrainerRepository trainerRepo;
    private final JwtUtil jwtUtil;

    public AuthService(TrainerRepository trainerRepo, JwtUtil jwtUtil) {
        this.trainerRepo = trainerRepo;
        this.jwtUtil    = jwtUtil;
    }

    @Transactional
    public Map<String,Object> register(String name, String email) {
        String code;
        do {
            code = UUID.randomUUID()
                    .toString()
                    .replace("-", "")
                    .substring(0, 6);
        } while (trainerRepo.existsByAccessCode(code));

        Trainer t = new Trainer();
        t.setName(name);
        t.setEmail(email);
        t.setAccessCode(code);
        trainerRepo.save(t);

        return Map.of(
                "accessCode", code,
                "name",       name,
                "email",      email
        );
    }

    @Transactional(readOnly = true)
    public String login(String accessCode) {
        Trainer t = trainerRepo.findByAccessCode(accessCode)
                .orElseThrow(() -> new IllegalArgumentException("Invalid access code"));
        return jwtUtil.generateToken(accessCode);
    }
}
