package com.example.movietickets.controller;

import com.example.movietickets.model.Role;
import com.example.movietickets.model.User;
import com.example.movietickets.repository.UserRepository;
import com.example.movietickets.security.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Rollback
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private JwtUtil jwtUtil;
    @MockBean
    private BCryptPasswordEncoder passwordEncoder;

    private User testUser;
    private final String encodedPassword = new BCryptPasswordEncoder().encode("password");
    
    @BeforeEach
    void setUp() {
        testUser = new User(1L, "Test User", "user@example.com", encodedPassword, Role.ADMIN);
    }

    @Test
    void testLoginSuccess() throws Exception {
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password", testUser.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken("user@example.com")).thenReturn("mockJwtToken");

        mockMvc.perform(post("/api/auth/login")
                        .param("email", "user@example.com")
                        .param("password", "password")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string("mockJwtToken"));
    }

    @Test
    void testLoginInvalidCredentials() throws Exception {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/auth/login")
                        .param("email", "invalid@example.com")
                        .param("password", "wrongPassword"))
                .andExpect(status().isOk())
                .andExpect(content().string("Invalid credentials"));
    }
}
