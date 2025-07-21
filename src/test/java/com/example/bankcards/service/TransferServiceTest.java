package com.example.bankcards.service;

import com.example.bankcards.dto.TransferRequestDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.exception.CardOperationException;
import com.example.bankcards.repository.CardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Transfer Service Unit Tests")
class TransferServiceTest {

    @Mock
    private CardRepository cardRepository;

    @InjectMocks
    private TransferService transferService;

    private User user;
    private Card fromCard;
    private Card toCard;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        fromCard = new Card();
        fromCard.setId(10L);
        fromCard.setUser(user);
        fromCard.setStatus(CardStatus.ACTIVE);
        fromCard.setBalance(new BigDecimal("1000.00"));
        fromCard.setExpiryDate(LocalDate.now().plusYears(1));

        toCard = new Card();
        toCard.setId(20L);
        toCard.setUser(user);
        toCard.setStatus(CardStatus.ACTIVE);
        toCard.setBalance(new BigDecimal("500.00"));
        toCard.setExpiryDate(LocalDate.now().plusYears(1));
    }

    @Test
    @DisplayName("Should perform transfer successfully for valid request")
    void performTransfer_WhenRequestIsValid_ShouldSucceed() {
        var request = new TransferRequestDto(fromCard.getId(), toCard.getId(), new BigDecimal("100.00"));
        when(cardRepository.findById(fromCard.getId())).thenReturn(Optional.of(fromCard));
        when(cardRepository.findById(toCard.getId())).thenReturn(Optional.of(toCard));

        assertDoesNotThrow(() -> transferService.performTransfer(request, user));

        assertThat(fromCard.getBalance()).isEqualByComparingTo("900.00");
        assertThat(toCard.getBalance()).isEqualByComparingTo("600.00");
        verify(cardRepository, times(2)).save(any(Card.class));
    }

    @Test
    @DisplayName("Should throw exception when source card has insufficient funds")
    void performTransfer_WhenInsufficientFunds_ShouldThrowException() {
        var request = new TransferRequestDto(fromCard.getId(), toCard.getId(), new BigDecimal("2000.00"));
        when(cardRepository.findById(fromCard.getId())).thenReturn(Optional.of(fromCard));
        when(cardRepository.findById(toCard.getId())).thenReturn(Optional.of(toCard));

        var exception = assertThrows(CardOperationException.class, () -> transferService.performTransfer(request, user));
        assertThat(exception.getMessage()).isEqualTo("Insufficient funds on the source card.");
        verify(cardRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when source card is expired")
    void performTransfer_WhenCardIsExpired_ShouldThrowException() {
        fromCard.setExpiryDate(LocalDate.now().minusDays(1));
        var request = new TransferRequestDto(fromCard.getId(), toCard.getId(), new BigDecimal("100.00"));
        when(cardRepository.findById(fromCard.getId())).thenReturn(Optional.of(fromCard));
        when(cardRepository.findById(toCard.getId())).thenReturn(Optional.of(toCard));

        var exception = assertThrows(CardOperationException.class, () -> transferService.performTransfer(request, user));
        assertThat(exception.getMessage()).isEqualTo("The source card has expired.");
        verify(cardRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when source card is not active")
    void performTransfer_WhenCardIsNotActive_ShouldThrowException() {
        fromCard.setStatus(CardStatus.BLOCKED);
        var request = new TransferRequestDto(fromCard.getId(), toCard.getId(), new BigDecimal("100.00"));
        when(cardRepository.findById(fromCard.getId())).thenReturn(Optional.of(fromCard));
        when(cardRepository.findById(toCard.getId())).thenReturn(Optional.of(toCard));

        var exception = assertThrows(CardOperationException.class, () -> transferService.performTransfer(request, user));
        assertThat(exception.getMessage()).isEqualTo("The source card is not active.");
        verify(cardRepository, never()).save(any());
    }
}