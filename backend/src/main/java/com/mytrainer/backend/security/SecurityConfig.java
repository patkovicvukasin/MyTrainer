package com.mytrainer.backend.security;

import com.mytrainer.backend.repositories.TrainerRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import java.io.IOException;
import java.util.List;

@Configuration
public class SecurityConfig {
    private final JwtUtil jwtUtil;
    private final TrainerRepository trainerRepo;

    public SecurityConfig(JwtUtil jwtUtil, TrainerRepository trainerRepo) {
        this.jwtUtil = jwtUtil;
        this.trainerRepo = trainerRepo;
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/api/auth/register").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .requestMatchers("/api/trainers/me/**").authenticated()
                        .anyRequest().permitAll()
                )
                .addFilterBefore(
                        (request, response, chain) -> {
                            HttpServletRequest req = (HttpServletRequest) request;
                            HttpServletResponse res = (HttpServletResponse) response;

                            String header = req.getHeader("Authorization");
                            if (header != null && header.startsWith("Bearer ")) {
                                String token = header.substring(7);
                                if (jwtUtil.validateToken(token)) {
                                    String code = jwtUtil.extractAccessCode(token);
                                    trainerRepo.findByAccessCode(code).ifPresent(trainer -> {
                                        var auth = new UsernamePasswordAuthenticationToken(
                                                trainer, null, List.of(new SimpleGrantedAuthority("ROLE_TRAINER")));
                                        SecurityContextHolder.getContext().setAuthentication(auth);
                                    });
                                }
                            }
                            chain.doFilter(request, response);
                        },
                        BasicAuthenticationFilter.class
                );
        return http.build();
    }


    private void authenticateByToken(HttpServletRequest req,
                                     HttpServletResponse res,
                                     FilterChain chain) throws IOException, ServletException {
        String header = req.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            if (jwtUtil.validateToken(token)) {
                String code = jwtUtil.extractAccessCode(token);
                trainerRepo.findByAccessCode(code).ifPresent(trainer -> {
                    var auth = new UsernamePasswordAuthenticationToken(
                            trainer, null, List.of(new SimpleGrantedAuthority("ROLE_TRAINER")));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                });
            }
        }
        chain.doFilter(req, res);
    }
}
