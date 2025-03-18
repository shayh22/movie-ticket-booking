package com.example.movietickets.service;

import com.example.movietickets.model.Ticket;
import com.example.movietickets.repository.TicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @InjectMocks
    private TicketService ticketService;

    private Ticket testTicket;

    @BeforeEach
    void setUp() {
        testTicket = new Ticket(1L, null, null, "A5", 12.99);
    }

    @Test
    void shouldReturnAllTickets() {
        when(ticketRepository.findAll()).thenReturn(List.of(testTicket));

        List<Ticket> tickets = ticketService.getAllTickets();

        assertFalse(tickets.isEmpty());
        assertEquals(1, tickets.size());
    }
}
