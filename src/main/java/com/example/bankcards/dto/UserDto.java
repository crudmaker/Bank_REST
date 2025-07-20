package com.example.bankcards.dto;

import com.example.bankcards.entity.enums.Role;

public record UserDto(
        Long id,
        String username,
        String ownerName,
        Role role
) {
}
