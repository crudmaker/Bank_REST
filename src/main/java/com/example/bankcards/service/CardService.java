package com.example.bankcards.service;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.exception.CardOperationException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.util.CardMaskingUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;
    private final CardMaskingUtil cardMaskingUtil;

    public Page<CardDto> getUserCards(User user, Pageable pageable) {
        log.info("Fetching cards for user '{}' with page request: {}", user.getUsername(), pageable);
        Page<Card> cards = cardRepository.findByUser(user, pageable);
        return cards.map(this::mapToCardDto);
    }

    @Transactional
    public void requestCardBlock(Long cardId, User user) {
        log.info("User '{}' requesting to block card ID #{}", user.getUsername(), cardId);
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CardOperationException(String.format("Card with ID %d not found", cardId)));

        if (!card.getUser().getId().equals(user.getId())) {
            log.warn("Access denied for user '{}' to card ID #{}", user.getUsername(), cardId);
            throw new CardOperationException("Access denied. You are not the owner of this card.");
        }

        if (card.getStatus() == CardStatus.BLOCKED) {
            log.warn("Attempted to block an already blocked card ID #{}", cardId);
            throw new CardOperationException("Card is already blocked.");
        }

        card.setStatus(CardStatus.BLOCKED);
        cardRepository.save(card);
        log.info("Card ID #{} was successfully blocked by user '{}'", cardId, user.getUsername());
    }

    @Transactional
    @Scheduled(cron = "0 0 1 * * *")
    public void updateExpiredCardStatuses() {
        log.info("Running scheduled job to update expired card statuses...");
        LocalDate today = LocalDate.now();
        List<Card> expiredCards = cardRepository.findAllByStatusAndExpiryDateBefore(CardStatus.ACTIVE, today);

        if (expiredCards.isEmpty()) {
            log.info("No expired cards found.");
            return;
        }

        expiredCards.forEach(card -> card.setStatus(CardStatus.EXPIRED));

        cardRepository.saveAll(expiredCards);
        log.info("Successfully updated status for {} expired cards.", expiredCards.size());
    }


    public CardDto mapToCardDto(Card card) {
        return new CardDto(
                card.getId(),
                cardMaskingUtil.maskCardNumber(card.getCardNumber()),
                card.getUser().getOwnerName(),
                card.getExpiryDate(),
                card.getStatus(),
                card.getBalance()
        );
    }
}
