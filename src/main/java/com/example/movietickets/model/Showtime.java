package com.example.movietickets.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "showtimes")
public class Showtime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY) // Exclude from API request
    private Long id;

    @Column(name = "movie_id", nullable = false)
    private Long movieId;

    private String theater;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    // Full constructor (excluding ID since it's auto-generated)
    public Showtime(Long movieId, String theater, LocalDateTime startTime, LocalDateTime endTime) {
        this.movieId = movieId;
        this.theater = theater;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
