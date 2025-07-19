package com.example.bankcards.dto;

public record RegisterRequestDto(
        String username,
        String password,
        String ownerName
) {
}
