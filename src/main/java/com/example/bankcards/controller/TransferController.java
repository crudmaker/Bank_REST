package com.example.bankcards.controller;

import com.example.bankcards.dto.TransferRequestDto;
import com.example.bankcards.entity.User;
import com.example.bankcards.service.TransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/transfers")
@RestController
public class TransferController {

    private final TransferService transferService;

    @PostMapping
    public ResponseEntity<Void> transferMoney(
            @RequestBody TransferRequestDto transferRequestDto,
            @AuthenticationPrincipal User user) {
        transferService.performTransfer(transferRequestDto, user);
        return ResponseEntity.ok().build();
    }
}
