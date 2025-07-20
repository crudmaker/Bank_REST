package com.example.bankcards.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CardMaskingUtil {

    private final int visibleDigits;
    private final char maskChar;

    public CardMaskingUtil(
                            @Value("${app.masking.card-number-visible-digits}") int visibleDigits,
                            @Value("${app.masking.card-number-mask-char}") char maskChar) {
        this.visibleDigits = visibleDigits;
        this.maskChar = maskChar;
    }

    public String maskCardNumber(String cardNumber) {
        if (cardNumber == null) {
            return "";
        }

        if (cardNumber.length() <= this.visibleDigits) {
            return cardNumber;
        }

        int totalLength = cardNumber.length();
        String lastDigits = cardNumber.substring(totalLength - this.visibleDigits);
        String mask = String.valueOf(this.maskChar).repeat(totalLength - this.visibleDigits);
        String maskedNumber = mask + lastDigits;

        StringBuilder formattedMask = new StringBuilder();
        for (int i = 0; i < maskedNumber.length(); i++) {
            if (i > 0 && i % 4 == 0) {
                formattedMask.append(' ');
            }
            formattedMask.append(maskedNumber.charAt(i));
        }

        return formattedMask.toString();
    }
}
