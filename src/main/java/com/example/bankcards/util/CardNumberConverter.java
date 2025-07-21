package com.example.bankcards.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;

@Component
@Converter(autoApply = true)
public class CardNumberConverter implements AttributeConverter<String, String> {

    private static final String ALGORITHM = "AES/ECB/PKCS5Padding";
    private final Key key;
    private final Cipher cipher;

    public CardNumberConverter(@Value("${app.encryption.secret}") String secret) {
        if (secret == null || (secret.length() != 16 && secret.length() != 24 && secret.length() != 32)) {
            throw new IllegalArgumentException("Invalid AES key length. Must be 16, 24, or 32 bytes.");
        }
        this.key = new SecretKeySpec(secret.getBytes(), "AES");
        try {
            this.cipher = Cipher.getInstance(ALGORITHM);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public String convertToDatabaseColumn(String cardNumber) {
        if (cardNumber == null) {
            return null;
        }
        try {
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return Base64.getEncoder().encodeToString(cipher.doFinal(cardNumber.getBytes()));
        } catch (Exception e) {
            throw new RuntimeException("Error encrypting card number", e);
        }
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        try {
            cipher.init(Cipher.DECRYPT_MODE, key);
            return new String(cipher.doFinal(Base64.getDecoder().decode(dbData)));
        } catch (Exception e) {
            throw new RuntimeException("Error decrypting card number", e);
        }
    }
}
