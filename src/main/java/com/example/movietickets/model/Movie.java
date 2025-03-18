package com.example.movietickets.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "movies")
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY) // Exclude from API request
    private Long id;

    private String title;
    private String genre;
    private int duration; // in minutes
    private String rating;
    private int releaseYear;

    // Full constructor excluding ID (for creating new movies)
    public Movie(String title, String genre, int duration, String rating, int releaseYear) {
        this.title = title;
        this.genre = genre;
        this.duration = duration;
        this.rating = rating;
        this.releaseYear = releaseYear;
    }
}
