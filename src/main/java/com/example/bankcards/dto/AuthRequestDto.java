package com.example.bankcards.dto;

public record AuthRequestDto(
        String username,
        String password
) {
}
