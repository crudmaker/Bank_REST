package com.example.bankcards.service;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.exception.CardOperationException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.util.CardMaskingUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Card Service Unit Tests")
class CardServiceTest {

    @Mock
    private CardRepository cardRepository;
    @Mock
    private CardMaskingUtil cardMaskingUtil;

    @InjectMocks
    private CardService cardService;

    private User cardOwner;
    private Card card;

    @BeforeEach
    void setUp() {
        cardOwner = new User();
        cardOwner.setId(1L);

        card = new Card();
        card.setId(100L);
        card.setUser(cardOwner);
        card.setStatus(CardStatus.ACTIVE);
    }

    @Test
    @DisplayName("Should block card successfully for the owner")
    void requestCardBlock_WhenUserIsOwner_ShouldBlockCard() {
        when(cardRepository.findById(card.getId())).thenReturn(Optional.of(card));

        cardService.requestCardBlock(card.getId(), cardOwner);

        ArgumentCaptor<Card> cardCaptor = ArgumentCaptor.forClass(Card.class);
        verify(cardRepository).save(cardCaptor.capture());
        Card savedCard = cardCaptor.getValue();

        assertThat(savedCard.getStatus()).isEqualTo(CardStatus.BLOCKED);
    }

    @Test
    @DisplayName("Should throw exception when trying to block a card not owned by the user")
    void requestCardBlock_WhenUserIsNotOwner_ShouldThrowException() {
        User anotherUser = new User();
        anotherUser.setId(2L);
        when(cardRepository.findById(card.getId())).thenReturn(Optional.of(card));

        assertThrows(CardOperationException.class, () -> cardService.requestCardBlock(card.getId(), anotherUser));
    }
}