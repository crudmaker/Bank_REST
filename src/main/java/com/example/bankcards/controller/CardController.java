package com.example.bankcards.controller;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.entity.User;
import com.example.bankcards.service.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v1/cards")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Card Controller", description = "Endpoints for a user to manage their own cards.")
@RestController
public class CardController {

    private final CardService cardService;

    @Operation(summary = "Request to block a card", description = "Allows an authenticated user to block one of their own cards.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Card blocked successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request (e.g., card is already blocked)"),
            @ApiResponse(responseCode = "403", description = "Access Denied (not the card owner)"),
            @ApiResponse(responseCode = "404", description = "Card not found")
    })
    @PostMapping("/{id}/block")
    public ResponseEntity<Void> blockCard(@PathVariable Long id, @AuthenticationPrincipal User user) {
        cardService.requestCardBlock(id, user);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get my cards", description = "Returns a paginated list of cards belonging to the authenticated user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of cards"),
            @ApiResponse(responseCode = "403", description = "Access Denied")
    })
    @GetMapping
    public ResponseEntity<Page<CardDto>> getMyCards(
            @AuthenticationPrincipal User user,
            Pageable pageable) {
        return ResponseEntity.ok(cardService.getUserCards(user, pageable));
    }
}
