package com.example.bankcards.service;

import com.example.bankcards.dto.AdminCardCreateRequestDto;
import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.UserDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.entity.enums.Role;
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

        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new CardOperationException(String.format("User not found with id: %d", request.userId())));

        Card card = new Card();
        card.setUser(user);
        card.setCardNumber(request.cardNumber());
        card.setExpiryDate(request.expiryDate());
        card.setBalance(request.initialBalance());
        card.setStatus(CardStatus.ACTIVE);

        Card savedCard = cardRepository.save(card);
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

        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CardOperationException(String.format("Card not found with id: %d", cardId)));

        card.setStatus(newStatus);
        Card updatedCard = cardRepository.save(card);
        log.info("Successfully updated status for card ID #{}", updatedCard.getId());

        return cardService.mapToCardDto(updatedCard);
    }

    @Transactional
    public void deleteCard(Long cardId) {
        log.info("Admin deleting card ID #{}", cardId);
        if (!cardRepository.existsById(cardId)) {
            throw new CardOperationException(String.format("Card not found with id: %d", cardId));
        }
        cardRepository.deleteById(cardId);
        log.info("Successfully deleted card ID #{}", cardId);
    }

    public Page<UserDto> getAllUsers(Pageable pageable) {
        log.info("Admin fetching all users, page request: {}", pageable);
        return userRepository.findAll(pageable).map(this::mapToUserDto);
    }

    public UserDto getUserById(Long userId) {
        log.info("Admin fetching user by ID #{}", userId);
        return userRepository.findById(userId)
                .map(this::mapToUserDto)
                .orElseThrow(() -> new CardOperationException(String.format("User not found with id: %d", userId)));
    }

    @Transactional
    public UserDto updateUserRole(Long userId, Role newRole) {
        log.info("Admin updating role for user ID #{} to {}", userId, newRole);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CardOperationException(String.format("User not found with id: %d", userId)));

        user.setRole(newRole);
        userRepository.save(user);

        log.info("Successfully updated role for user ID #{}", userId);
        return mapToUserDto(user);
    }

    @Transactional
    public UserDto updateUserLockStatus(Long userId, boolean locked) {
        String status = locked ? "locking" : "unlocking";
        log.info("Admin {} user ID #{}", status, userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CardOperationException(String.format("User not found with id: %d", userId)));

        user.setLocked(locked);
        userRepository.save(user);

        log.info("Successfully updated lock status for user ID #{}", userId);
        return mapToUserDto(user);
    }

    private UserDto mapToUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getOwnerName(),
                user.getRole()
        );
    }
}
