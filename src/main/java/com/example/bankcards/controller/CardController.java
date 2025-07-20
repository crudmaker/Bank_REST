package com.example.bankcards.controller;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.entity.User;
import com.example.bankcards.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/v1/cards")
@RestController
public class CardController {

    private final CardService cardService;

    @GetMapping
    public ResponseEntity<Page<CardDto>> getMyCards(
            @AuthenticationPrincipal User user,
            Pageable pageable) {
        return ResponseEntity.ok(cardService.getUserCards(user, pageable));
    }

    @PostMapping("/{id}/block")
    public ResponseEntity<Void> blockCard(@PathVariable Long id, @AuthenticationPrincipal User user) {
        cardService.requestCardBlock(id, user);
        return ResponseEntity.ok().build();
    }
}
