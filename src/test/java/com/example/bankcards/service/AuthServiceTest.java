package com.example.bankcards.service;

import com.example.bankcards.dto.AuthRequestDto;
import com.example.bankcards.dto.AuthResponseDto;
import com.example.bankcards.dto.RegisterRequestDto;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.Role;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.security.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Auth Service Unit Tests")
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("Should register a new user successfully")
    void register_ShouldSaveUserAndReturnToken() {
        var request = new RegisterRequestDto("newUser", "password123", "New User");
        String dummyToken = "dummy-jwt-token";
        String encodedPassword = "encoded-password";

        when(passwordEncoder.encode(request.password())).thenReturn(encodedPassword);
        when(jwtService.generateToken(any(User.class))).thenReturn(dummyToken);

        AuthResponseDto response = authService.register(request);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        assertThat(savedUser.getUsername()).isEqualTo(request.username());
        assertThat(savedUser.getPassword()).isEqualTo(encodedPassword);
        assertThat(savedUser.getRole()).isEqualTo(Role.USER);
        assertThat(response.token()).isEqualTo(dummyToken);
    }

    @Test
    @DisplayName("Should authenticate a valid user and return a token")
    void authenticate_WhenCredentialsAreValid_ShouldReturnToken() {
        var request = new AuthRequestDto("user", "password");
        var user = new User();
        user.setUsername("user");
        String dummyToken = "dummy-jwt-token";

        when(userRepository.findByUsername(request.username())).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn(dummyToken);

        AuthResponseDto response = authService.authenticate(request);

        verify(authenticationManager).authenticate(any());
        assertThat(response.token()).isEqualTo(dummyToken);
    }

    @Test
    @DisplayName("Should throw exception for invalid credentials")
    void authenticate_WhenCredentialsAreInvalid_ShouldThrowException() {
        var request = new AuthRequestDto("user", "wrong-password");
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(BadCredentialsException.class, () -> authService.authenticate(request));
    }
}