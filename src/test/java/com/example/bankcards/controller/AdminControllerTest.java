package com.example.bankcards.controller;

import com.example.bankcards.BaseIntegrationTest;
import com.example.bankcards.dto.AdminCardCreateRequestDto;
import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.UserDto;
import com.example.bankcards.dto.UserRoleUpdateRequestDto;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.entity.enums.Role;
import com.example.bankcards.service.AdminService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Admin Controller Tests")
class AdminControllerTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminService adminService;

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    @DisplayName("User Management API")
    class UserManagementTests {

        @Test
        @WithMockUser(roles = "ADMIN")
        void getUserById_AsAdmin_ShouldReturnOk() throws Exception {
            long userId = 1L;
            UserDto userDto = new UserDto(userId, "testuser", "Test User", Role.USER);
            when(adminService.getUserById(userId)).thenReturn(userDto);

            mockMvc.perform(get("/api/v1/admin/users/{id}", userId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(userId))
                    .andExpect(jsonPath("$.username").value("testuser"));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void updateUserRole_AsAdmin_ShouldReturnOk() throws Exception {
            long userId = 1L;
            var request = new UserRoleUpdateRequestDto(Role.ADMIN);
            var response = new UserDto(userId, "testuser", "Test User", Role.ADMIN);
            when(adminService.updateUserRole(eq(userId), any(Role.class))).thenReturn(response);

            mockMvc.perform(patch("/api/v1/admin/users/{id}/role", userId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.role").value("ADMIN"));
        }
    }

    @Nested
    @DisplayName("Card Management API")
    class CardManagementTests {

        @Test
        @WithMockUser(roles = "ADMIN")
        void createCard_AsAdmin_ShouldReturnCreated() throws Exception {
            var request = new AdminCardCreateRequestDto(
                    1L,
                    "1111222233334444",
                    LocalDate.now().plusYears(1),
                    BigDecimal.ZERO);
            var response = new CardDto(
                    100L,
                    "**** **** **** 4444",
                    "Test User",
                    request.expiryDate(),
                    CardStatus.ACTIVE,
                    BigDecimal.ZERO);
            when(adminService.createCard(any(AdminCardCreateRequestDto.class))).thenReturn(response);

            mockMvc.perform(post("/api/v1/admin/cards")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(100L));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void deleteCard_AsAdmin_ShouldReturnNoContent() throws Exception {
            long cardId = 100L;
            doNothing().when(adminService).deleteCard(cardId);

            mockMvc.perform(delete("/api/v1/admin/cards/{id}", cardId))
                    .andExpect(status().isNoContent());
        }
    }

    @Nested
    @DisplayName("Security Tests")
    class SecurityTests {

        @ParameterizedTest
        @ValueSource(strings = {
                "/api/v1/admin/users",
                "/api/v1/admin/users/1",
                "/api/v1/admin/cards"
        })
        @WithMockUser(roles = "USER")
        void adminGetEndpoints_AsUser_ShouldReturnForbidden(String url) throws Exception {
            mockMvc.perform(get(url))
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(roles = "USER")
        void adminPostEndpoints_AsUser_ShouldReturnForbidden() throws Exception {
            var request = new AdminCardCreateRequestDto(
                    1L,
                    "4242424242424242", // <-- ВАЛИДНЫЙ номер по алгоритму Луна
                    LocalDate.now().plusYears(1),
                    BigDecimal.TEN
            );

            mockMvc.perform(post("/api/v1/admin/cards")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isForbidden());        }
    }
}
