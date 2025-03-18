package com.example.movietickets.service;

import com.example.movietickets.model.Showtime;
import com.example.movietickets.model.Ticket;
import com.example.movietickets.repository.TicketRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TicketService {
    private final TicketRepository ticketRepository;
    private final ShowtimeService showtimeService;
    
    @Value("${booking.max.seats.per.showtime}")
    private int maxSeatsPerShowtime;
    

    public TicketService(TicketRepository ticketRepository, ShowtimeService showtimeService) {
        this.ticketRepository = ticketRepository;
        this.showtimeService = showtimeService;
    }

    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    public Optional<Ticket> getTicketById(Long id) {
        return ticketRepository.findById(id);
    }

    public Ticket bookTicket(Ticket ticket) {
    	
		showtimeService.getShowtimeById(ticket.getShowtimeId())
				.orElseThrow(() -> new RuntimeException("Showtime not found"));
		
        // Prevent double seat booking
        if (ticketRepository.existsByShowtimeIdAndSeatNumber(ticket.getShowtimeId(), ticket.getSeatNumber())) {
            throw new RuntimeException("Seat is already booked for this showtime.");
        }

        // Prevent booking if max seats are reached
        long bookedSeats = ticketRepository.countByShowtimeId(ticket.getShowtimeId());
        if (bookedSeats >= maxSeatsPerShowtime) {
            throw new RuntimeException("Maximum seats reached for this showtime.");
        }

        return ticketRepository.save(ticket);
    }


    public void cancelTicket(Long id) {
        ticketRepository.deleteById(id);
    }
    
    

    // ✅ Adding the missing deleteTicket() method
    public void deleteTicket(Long id) {
        if (ticketRepository.existsById(id)) {
            ticketRepository.deleteById(id);
        } else {
            throw new RuntimeException("Ticket with ID " + id + " not found");
        }
    }
}
