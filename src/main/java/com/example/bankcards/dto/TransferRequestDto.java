package com.example.bankcards.dto;

import java.math.BigDecimal;

public record TransferRequestDto(
        Long fromCardId,
        Long toCardId,
        BigDecimal amount
) {
}
