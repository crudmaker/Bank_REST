package com.example.bankcards.dto;

import com.example.bankcards.entity.enums.CardStatus;
import java.math.BigDecimal;
import java.time.LocalDate;

public record CardDto(
        Long id,
        String maskedCardNumber,
        String ownerName,
        LocalDate expiryDate,
        CardStatus status,
        BigDecimal balance
) {
}
