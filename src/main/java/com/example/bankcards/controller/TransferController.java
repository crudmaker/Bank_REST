package com.example.bankcards.controller;

import com.example.bankcards.dto.TransferRequestDto;
import com.example.bankcards.entity.User;
import com.example.bankcards.service.TransferService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v1/transfers")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Transfer Controller", description = "Endpoint for performing transfers between a user's own cards.")
@RestController
public class TransferController {

    private final TransferService transferService;

    @Operation(summary = "Perform a transfer", description = "Transfers a specified amount from one of the user's cards to another.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transfer successful"),
            @ApiResponse(responseCode = "400", description = "Invalid transfer request (e.g., insufficient funds, expired card)"),
            @ApiResponse(responseCode = "403", description = "Access Denied (not the card owner)"),
            @ApiResponse(responseCode = "404", description = "One or both cards not found")
    })
    @PostMapping
    public ResponseEntity<Void> transferMoney(
            @RequestBody TransferRequestDto transferRequestDto,
            @AuthenticationPrincipal User user) {
        transferService.performTransfer(transferRequestDto, user);
        return ResponseEntity.ok().build();
    }
}
