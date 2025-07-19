package com.example.bankcards.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;

@Converter
public class CardNumberConverter implements AttributeConverter<String, String> {

    private static final String ALGORITHM = "AES/ECB/PKCS5Padding";

    // Ключ шифрования. В реальном проекте он должен быть надежно защищен
    // и загружаться из безопасного хранилища (Vault) или переменных окружения.
    private final Key key;
    private final Cipher cipher;

    public CardNumberConverter() {
        // ВАЖНО: Этот ключ приведен только для примера.
        // Он должен быть длиной 16, 24 или 32 байта для AES.
        // Никогда не храните ключи в коде в продакшене!
        String secret = "ThisIsASecretKeyForAES12345678";
        this.key = new SecretKeySpec(secret.getBytes(), "AES");
        try {
            this.cipher = Cipher.getInstance(ALGORITHM);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public String convertToDatabaseColumn(String cardNumber) {
        // Шифруем номер карты перед сохранением в БД
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
        // Расшифровываем номер карты при извлечении из БД
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
