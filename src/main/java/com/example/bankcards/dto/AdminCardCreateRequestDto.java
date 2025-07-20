package com.example.bankcards.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AdminCardCreateRequestDto(
        Long userId,
        String cardNumber,
        LocalDate expiryDate,
        BigDecimal initialBalance
) {
}
