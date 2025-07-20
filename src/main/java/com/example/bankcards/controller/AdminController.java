package com.example.bankcards.controller;

import com.example.bankcards.dto.AdminCardCreateRequestDto;
import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.UserDto;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.service.AdminService;
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

    @GetMapping("/users")
    public ResponseEntity<Page<UserDto>> getAllUsers(Pageable pageable) {
        return ResponseEntity.ok(adminService.getAllUsers(pageable));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.getUserById(id));
    }
}
