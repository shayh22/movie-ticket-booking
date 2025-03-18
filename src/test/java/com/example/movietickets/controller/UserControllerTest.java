package com.example.movietickets.controller;

import com.example.movietickets.model.Role;
import com.example.movietickets.model.User;
import com.example.movietickets.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    @Rollback
    void shouldRegisterUserWithEncryptedPassword() throws Exception {
        User newUser = new User("John Doe", "john@example.com", "password123", Role.CUSTOMER);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(newUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.email", is("john@example.com")))
                .andExpect(jsonPath("$.password", not("password123")))
                .andExpect(jsonPath("$.role", is("CUSTOMER")));

        User savedUser = userRepository.findByEmail("john@example.com").orElse(null);

        assertNotNull(savedUser, "User should exist in the database");
        assertTrue(passwordEncoder.matches("password123", savedUser.getPassword()), "Password must be encrypted and match original");
    }

    @Test
    @Rollback
    void shouldRetrieveAllUsers() throws Exception {
        User user = new User("Alice", "alice@example.com", passwordEncoder.encode("alicepass"), Role.CUSTOMER);
        userRepository.save(user);

        mockMvc.perform(get("/api/users").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].email", is("alice@example.com")));
    }

    @Test
    @Rollback
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    void shouldRetrieveUserById() throws Exception {
        User user = new User("Bob", "bob@example.com", passwordEncoder.encode("bobpass"), Role.ADMIN);
        user = userRepository.save(user);

        mockMvc.perform(get("/api/users/{id}", user.getId()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is("bob@example.com")))
                .andExpect(jsonPath("$.role", is("ADMIN")));
    }


    @Test
    @Rollback
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    void shouldDeleteUser() throws Exception {
        User user = new User("David", "david@example.com", passwordEncoder.encode("davidpass"), Role.CUSTOMER);
        user = userRepository.save(user);

        mockMvc.perform(delete("/api/users/{id}", user.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        assertFalse(userRepository.existsById(user.getId()), "User should be deleted from the database");
    }
}
