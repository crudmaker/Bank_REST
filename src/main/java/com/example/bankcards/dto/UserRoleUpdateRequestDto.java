package com.example.bankcards.dto;

import com.example.bankcards.entity.enums.Role;
import jakarta.validation.constraints.NotNull;

public record UserRoleUpdateRequestDto(@NotNull Role newRole) {
}

