package com.example.movietickets.repository;

import com.example.movietickets.model.Showtime;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShowtimeRepository extends JpaRepository<Showtime, Long> {
	 boolean existsByTheaterAndStartTimeBetween(String theater, LocalDateTime startTime, LocalDateTime endTime);
}
