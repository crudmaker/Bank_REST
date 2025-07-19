package com.example.bankcards.service;

import com.example.bankcards.dto.TransferRequestDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.exception.CardOperationException;
import com.example.bankcards.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@RequiredArgsConstructor
@Service
public class TransferService {

    private final CardRepository cardRepository;

    @Transactional
    public void performTransfer(TransferRequestDto request, User user) {
        log.info(
                "Attempting to transfer {} from card #{} to card #{} for user '{}'",
                request.amount(),
                request.fromCardId(),
                request.toCardId(),
                user.getUsername()
        );

        Card fromCard = findAndValidateCard(request.fromCardId(), user);
        Card toCard = findAndValidateCard(request.toCardId(), user);

        if (fromCard.getStatus() != CardStatus.ACTIVE) {
            log.warn("Transfer failed: Source card #{} is not active. Status: {}", fromCard.getId(), fromCard.getStatus());
            throw new CardOperationException("The source card is not active.");
        }

        if (fromCard.getBalance().compareTo(request.amount()) < 0) {
            log.warn("Transfer failed: Insufficient funds on card #{}. Balance: {}, Requested: {}",
                    fromCard.getId(), fromCard.getBalance(), request.amount());
            throw new CardOperationException("Insufficient funds on the source card.");
        }

        if (request.amount().compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("Transfer failed: Amount must be positive. Requested: {}", request.amount());
            throw new CardOperationException("Transfer amount must be positive.");
        }

        fromCard.setBalance(fromCard.getBalance().subtract(request.amount()));
        toCard.setBalance(toCard.getBalance().add(request.amount()));

        cardRepository.save(fromCard);
        cardRepository.save(toCard);

        log.info("Transfer from card #{} to #{} for amount {} completed successfully for user '{}'",
                fromCard.getId(), toCard.getId(), request.amount(), user.getUsername());
    }

    private Card findAndValidateCard(Long cardId, User user) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CardOperationException(String.format("Card with id %d not found.", cardId)));
        if (!card.getUser().getId().equals(user.getId())) {
            log.warn("User '{}' attempted to access card #{} owned by user '{}'",
                    user.getUsername(), card.getId(), card.getUser().getUsername());
            throw new CardOperationException(String.format("Access denied to card %d", cardId));
        }
        return card;
    }
}