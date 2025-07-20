package com.example.bankcards.service;

import com.example.bankcards.dto.AdminCardCreateRequestDto;
import com.example.bankcards.dto.CardDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.exception.CardOperationException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Service
public class AdminService {

    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final CardService cardService;

    @Transactional
    public CardDto createCard(AdminCardCreateRequestDto request) {
        log.info("Admin creating card for user ID #{}", request.userId());

        var user = userRepository.findById(request.userId())
                .orElseThrow(() -> new CardOperationException("User not found with id: " + request.userId()));

        var card = new Card();
        card.setUser(user);
        card.setCardNumber(request.cardNumber());
        card.setExpiryDate(request.expiryDate());
        card.setBalance(request.initialBalance());
        card.setStatus(CardStatus.ACTIVE); // Новые карты сразу активны

        var savedCard = cardRepository.save(card);
        log.info("Successfully created card ID #{} for user ID #{}", savedCard.getId(), user.getId());

        return cardService.mapToCardDto(savedCard);
    }

    public Page<CardDto> getAllCards(Pageable pageable) {
        log.info("Admin fetching all cards, page request: {}", pageable);
        return cardRepository.findAll(pageable).map(cardService::mapToCardDto);
    }

    @Transactional
    public CardDto updateCardStatus(Long cardId, CardStatus newStatus) {
        log.info("Admin updating status for card ID #{} to {}", cardId, newStatus);

        var card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CardOperationException("Card not found with id: " + cardId));

        card.setStatus(newStatus);
        var updatedCard = cardRepository.save(card);
        log.info("Successfully updated status for card ID #{}", updatedCard.getId());

        return cardService.mapToCardDto(updatedCard);
    }

    @Transactional
    public void deleteCard(Long cardId) {
        log.info("Admin deleting card ID #{}", cardId);
        if (!cardRepository.existsById(cardId)) {
            throw new CardOperationException("Card not found with id: " + cardId);
        }
        cardRepository.deleteById(cardId);
        log.info("Successfully deleted card ID #{}", cardId);
    }
}
