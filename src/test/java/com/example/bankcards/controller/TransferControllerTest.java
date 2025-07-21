package com.example.bankcards.controller;

import com.example.bankcards.BaseIntegrationTest;
import com.example.bankcards.dto.TransferRequestDto;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.Role;
import com.example.bankcards.service.TransferService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@DisplayName("Transfer Controller Tests")
class TransferControllerTest extends BaseIntegrationTest {

    @TestConfiguration
    static class TestSecurityConfig {

        @Bean
        @Primary
        public UserDetailsService userDetailsService() {
            User testUser = new User();
            testUser.setId(1L);
            testUser.setUsername("testuser");
            testUser.setPassword("password");
            testUser.setRole(Role.USER);
            testUser.setOwnerName("Test User");
            testUser.setLocked(false);

            return username -> {
                if (username.equals("testuser")) {
                    return testUser;
                }
                throw new UsernameNotFoundException("User not found: " + username);
            };
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransferService transferService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithUserDetails("testuser")
    @DisplayName("Should return OK for a valid transfer request from an authenticated user")
    void performTransfer_AsAuthenticatedUser_ShouldReturnOk() throws Exception {
        var requestDto = new TransferRequestDto(1L, 2L, new BigDecimal("100.00"));
        doNothing().when(transferService).performTransfer(any(TransferRequestDto.class), any(User.class));

        mockMvc.perform(post("/api/v1/transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk());

        verify(transferService).performTransfer(any(TransferRequestDto.class), any(User.class));
    }

    @Test
    @DisplayName("Should return Forbidden for a transfer request from an anonymous user")
    void performTransfer_AsAnonymous_ShouldReturnForbidden() throws Exception {
        var requestDto = new TransferRequestDto(1L, 2L, new BigDecimal("100.00"));

        mockMvc.perform(post("/api/v1/transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isForbidden());

        verify(transferService, never()).performTransfer(any(), any());
    }
}