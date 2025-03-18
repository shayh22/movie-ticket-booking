package com.example.movietickets.controller;

import com.example.movietickets.model.*;
import com.example.movietickets.repository.*;
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
import jakarta.transaction.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class TicketControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ShowtimeRepository showtimeRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private User testUser;
    private Showtime testShowtime;

    @BeforeEach
    void setUp() {
        testUser = new User("Test User", "testuser@example.com", passwordEncoder.encode("password"), Role.CUSTOMER);
        userRepository.save(testUser);

        Movie movie = new Movie("Inception", "Sci-Fi", 148, "8.8", 2010);
        movieRepository.save(movie);

        testShowtime = new Showtime(movie.getId(), "Theater 1", LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1).plusHours(3));
        showtimeRepository.save(testShowtime);
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    @Rollback
    void customerShouldBookTicket() throws Exception {
        Ticket ticket = new Ticket(testUser.getId(), testShowtime.getId(), "A1", 50.0);

        mockMvc.perform(post("/api/tickets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ticket)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.seatNumber", is("A1")))
                .andExpect(jsonPath("$.price", is(50.0)));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @Rollback
    void adminShouldBookTicket() throws Exception {
        Ticket ticket = new Ticket(testUser.getId(), testShowtime.getId(), "A2", 55.0);

        mockMvc.perform(post("/api/tickets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ticket)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.seatNumber", is("A2")))
                .andExpect(jsonPath("$.price", is(55.0)));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    @Rollback
    void customerShouldGetAllTickets() throws Exception {
        Ticket ticket = new Ticket(testUser.getId(), testShowtime.getId(), "B1", 45.0);
        ticketRepository.save(ticket);

        mockMvc.perform(get("/api/tickets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].seatNumber", is("B1")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @Rollback
    void adminShouldGetAllTickets() throws Exception {
        Ticket ticket = new Ticket(testUser.getId(), testShowtime.getId(), "B2", 60.0);
        ticketRepository.save(ticket);

        mockMvc.perform(get("/api/tickets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].seatNumber", is("B2")));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    @Rollback
    void customerShouldCancelTicket() throws Exception {
        Ticket ticket = new Ticket(testUser.getId(), testShowtime.getId(), "C1", 50.0);
        ticket = ticketRepository.save(ticket);

        mockMvc.perform(delete("/api/tickets/{id}", ticket.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @Rollback
    void adminShouldCancelTicket() throws Exception {
        Ticket ticket = new Ticket(testUser.getId(), testShowtime.getId(), "C2", 55.0);
        ticket = ticketRepository.save(ticket);

        mockMvc.perform(delete("/api/tickets/{id}", ticket.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    @Rollback
    void shouldGetTicketById() throws Exception {
        Ticket ticket = new Ticket(testUser.getId(), testShowtime.getId(), "D1", 40.0);
        ticket = ticketRepository.save(ticket);

        mockMvc.perform(get("/api/tickets/{id}", ticket.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.seatNumber", is("D1")))
                .andExpect(jsonPath("$.price", is(40.0)));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @Rollback
    void adminShouldGetTicketById() throws Exception {
        Ticket ticket = new Ticket(testUser.getId(), testShowtime.getId(), "D2", 65.0);
        ticket = ticketRepository.save(ticket);

        mockMvc.perform(get("/api/tickets/{id}", ticket.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.seatNumber", is("D2")));
    }
    
    @Test
    @WithMockUser(roles = "USER")
    @Rollback
    void userShouldNotGetTicketById() throws Exception {
        Ticket ticket = new Ticket(testUser.getId(), testShowtime.getId(), "D2", 65.0);
        ticket = ticketRepository.save(ticket);

        mockMvc.perform(get("/api/tickets/{id}", ticket.getId()))
                .andExpect(status().isForbidden());
    }
}