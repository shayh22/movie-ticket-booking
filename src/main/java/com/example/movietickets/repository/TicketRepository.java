package com.example.movietickets.repository;

import com.example.movietickets.model.Showtime;
import com.example.movietickets.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
	long countByShowtimeId(Long showtimeId);
	boolean existsByShowtimeIdAndSeatNumber(Long showtimeId, String seatNumber);
}
