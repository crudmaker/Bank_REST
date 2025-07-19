package com.example.bankcards.service;

import com.example.bankcards.dto.AuthRequestDto;
import com.example.bankcards.dto.AuthResponseDto;
import com.example.bankcards.dto.RegisterRequestDto;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.Role;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponseDto register(RegisterRequestDto request) {
        log.info("Registering new user with username: {}", request.username());
        var user = new User();
        user.setUsername(request.username());
        user.setOwnerName(request.ownerName());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(Role.USER);
        userRepository.save(user);

        var jwtToken = jwtService.generateToken(user);
        log.info("User {} registered successfully", user.getUsername());
        return new AuthResponseDto(jwtToken);
    }

    public AuthResponseDto authenticate(AuthRequestDto request) {
        log.info("Attempting to authenticate user: {}", request.username());
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.username(),
                            request.password()
                    )
            );
            var user = userRepository.findByUsername(request.username()).orElseThrow();
            var jwtToken = jwtService.generateToken(user);
            log.info("User {} authenticated successfully", user.getUsername());
            return new AuthResponseDto(jwtToken);
        } catch (AuthenticationException e) {
            log.warn("Failed authentication attempt for user: {}", request.username());
            throw e;
        }
    }
}
