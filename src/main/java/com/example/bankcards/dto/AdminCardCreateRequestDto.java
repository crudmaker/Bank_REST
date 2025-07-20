package com.example.bankcards.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import org.hibernate.validator.constraints.CreditCardNumber;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AdminCardCreateRequestDto(

        @NotNull
        Long userId,

        @NotBlank(message = "Card number must not be blank")
        @Pattern(regexp = "\\d{13,19}", message = "Card number must contain 13 to 19 digits")
        @CreditCardNumber(message = "Invalid credit card number")
        String cardNumber,

        @NotNull
        @Future(message = "Expiry date must be in the future")
        LocalDate expiryDate,

        @NotNull
        @PositiveOrZero(message = "Initial balance must be zero or positive")
        BigDecimal initialBalance
) {
}
