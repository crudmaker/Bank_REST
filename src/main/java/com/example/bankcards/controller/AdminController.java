package com.example.bankcards.controller;

import com.example.bankcards.dto.AdminCardCreateRequestDto;
import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.UserDto;
import com.example.bankcards.dto.UserLockStatusUpdateRequestDto;
import com.example.bankcards.dto.UserRoleUpdateRequestDto;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Admin Controller", description = "Endpoints for managing users and cards. Access limited to ADMIN role.")
@RestController
public class AdminController {

    private final AdminService adminService;

    @Operation(summary = "Create a new card", description = "Creates a new card for a specified user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Card created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data (e.g., invalid card number or user not found)"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PostMapping("/cards")
    public ResponseEntity<CardDto> createCard(@Valid @RequestBody AdminCardCreateRequestDto request) {
        var createdCard = adminService.createCard(request);
        return new ResponseEntity<>(createdCard, HttpStatus.CREATED);
    }

    @Operation(summary = "Get all cards", description = "Returns a paginated list of all cards in the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/cards")
    public ResponseEntity<Page<CardDto>> getAllCards(Pageable pageable) {
        return ResponseEntity.ok(adminService.getAllCards(pageable));
    }

    @Operation(summary = "Update card status", description = "Updates the status of a specific card (e.g., ACTIVE, BLOCKED).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status updated successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Card not found")
    })
    @PatchMapping("/cards/{id}/status")
    public ResponseEntity<CardDto> updateCardStatus(@PathVariable Long id, @RequestBody CardStatus newStatus) {
        return ResponseEntity.ok(adminService.updateCardStatus(id, newStatus));
    }

    @Operation(summary = "Delete a card", description = "Deletes a specific card by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Card deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Card not found")
    })
    @DeleteMapping("/cards/{id}")
    public ResponseEntity<Void> deleteCard(@PathVariable Long id) {
        adminService.deleteCard(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get all users", description = "Returns a paginated list of all registered users.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list"),
            @ApiResponse(responseCode = "403", description = "Access denied")})
    @GetMapping("/users")
    public ResponseEntity<Page<UserDto>> getAllUsers(Pageable pageable) {
        return ResponseEntity.ok(adminService.getAllUsers(pageable));
    }

    @Operation(summary = "Get user by ID", description = "Returns details for a specific user by their ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/users/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.getUserById(id));
    }

    @Operation(summary = "Update user role", description = "Updates the role of a specific user (e.g., from USER to ADMIN).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Role updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid role provided"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PatchMapping("/users/{id}/role")
    public ResponseEntity<UserDto> updateUserRole(@PathVariable Long id, @Valid @RequestBody UserRoleUpdateRequestDto request) {
        return ResponseEntity.ok(adminService.updateUserRole(id, request.newRole()));
    }

    @Operation(summary = "Update user lock status", description = "Locks or unlocks a user's account.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lock status updated successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PatchMapping("/users/{id}/lock-status")
    public ResponseEntity<UserDto> updateUserLockStatus(@PathVariable Long id, @RequestBody UserLockStatusUpdateRequestDto request) {
        return ResponseEntity.ok(adminService.updateUserLockStatus(id, request.locked()));
    }
}
