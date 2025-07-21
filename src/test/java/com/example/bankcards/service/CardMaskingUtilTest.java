package com.example.bankcards.service;

import com.example.bankcards.util.CardMaskingUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Card Masking Util Tests")
class CardMaskingUtilTest {

    private CardMaskingUtil cardMaskingUtil;

    @BeforeEach
    void setUp() {
        cardMaskingUtil = new CardMaskingUtil(4, '*');
    }

    @Test
    @DisplayName("Should correctly mask a standard 16-digit card number")
    void maskCardNumber_WithStandardNumber_ShouldMaskCorrectly() {
        String cardNumber = "1234567812345678";
        String expectedMaskedNumber = "**** **** **** 5678";

        String actualMaskedNumber = cardMaskingUtil.maskCardNumber(cardNumber);

        assertThat(actualMaskedNumber).isEqualTo(expectedMaskedNumber);
    }

    @Test
    @DisplayName("Should not mask a card number shorter than visible digits")
    void maskCardNumber_WithShortNumber_ShouldNotMask() {
        String cardNumber = "123";

        String actualMaskedNumber = cardMaskingUtil.maskCardNumber(cardNumber);

        assertThat(actualMaskedNumber).isEqualTo(cardNumber);
    }

    @Test
    @DisplayName("Should return an empty string for null input")
    void maskCardNumber_WithNullInput_ShouldReturnEmptyString() {
        String actualMaskedNumber = cardMaskingUtil.maskCardNumber(null);

        assertThat(actualMaskedNumber).isEmpty();
    }

    @ParameterizedTest
    @CsvSource({
            "123456789, **** *678 9",
            "12345, *234 5"
    })
    @DisplayName("Should correctly format masking for non-standard length numbers")
    void maskCardNumber_WithVariousLengths_ShouldFormatCorrectly(String input, String expected) {
        String actual = cardMaskingUtil.maskCardNumber(input);

        assertThat(actual).isEqualTo(expected);
    }
}
