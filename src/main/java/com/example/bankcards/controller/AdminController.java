package com.example.bankcards.controller;

import com.example.bankcards.dto.AdminCardCreateRequestDto;
import com.example.bankcards.dto.CardDto;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
@RestController
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/cards")
    public ResponseEntity<CardDto> createCard(@RequestBody AdminCardCreateRequestDto request) {
        var createdCard = adminService.createCard(request);
        return new ResponseEntity<>(createdCard, HttpStatus.CREATED);
    }

    @GetMapping("/cards")
    public ResponseEntity<Page<CardDto>> getAllCards(Pageable pageable) {
        return ResponseEntity.ok(adminService.getAllCards(pageable));
    }

    @PatchMapping("/cards/{id}/status")
    public ResponseEntity<CardDto> updateCardStatus(@PathVariable Long id, @RequestBody CardStatus newStatus) {
        return ResponseEntity.ok(adminService.updateCardStatus(id, newStatus));
    }

    @DeleteMapping("/cards/{id}")
    public ResponseEntity<Void> deleteCard(@PathVariable Long id) {
        adminService.deleteCard(id);
        return ResponseEntity.noContent().build();
    }
}
