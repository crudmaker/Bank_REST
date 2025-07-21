package com.example.bankcards.service;

import com.example.bankcards.dto.AdminCardCreateRequestDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.Role;
import com.example.bankcards.exception.CardOperationException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Admin Service Unit Tests")
class AdminServiceTest {

    @Mock
    private CardRepository cardRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CardService cardService;

    @InjectMocks
    private AdminService adminService;

    @Test
    @DisplayName("Should create card successfully when user exists")
    void createCard_WhenUserExists_ShouldCreateCard() {
        long userId = 1L;
        var request = new AdminCardCreateRequestDto(userId, "1234567812345678", LocalDate.now().plusYears(2), BigDecimal.ZERO);
        var user = new User();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(cardRepository.save(any(Card.class))).thenAnswer(invocation -> invocation.getArgument(0));

        adminService.createCard(request);

        verify(cardRepository, times(1)).save(any(Card.class));
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    @DisplayName("Should throw exception when creating card for non-existent user")
    void createCard_WhenUserDoesNotExist_ShouldThrowException() {
        long userId = 99L;
        var request = new AdminCardCreateRequestDto(userId, "1234567812345678", LocalDate.now().plusYears(2), BigDecimal.ZERO);

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(CardOperationException.class, () -> adminService.createCard(request));
        verify(cardRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should update user role successfully")
    void updateUserRole_ShouldChangeUserRole() {
        long userId = 1L;
        Role newRole = Role.ADMIN;
        User user = new User();
        user.setId(userId);
        user.setRole(Role.USER);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        adminService.updateUserRole(userId, newRole);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        assertThat(savedUser.getRole()).isEqualTo(Role.ADMIN);
    }
}
