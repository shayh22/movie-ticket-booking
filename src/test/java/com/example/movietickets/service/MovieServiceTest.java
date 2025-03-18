package com.example.movietickets.service;

import com.example.movietickets.model.Movie;
import com.example.movietickets.repository.MovieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovieServiceTest {

    @Mock
    private MovieRepository movieRepository;

    @InjectMocks
    private MovieService movieService;

    private Movie testMovie;

    @BeforeEach
    void setUp() {
        testMovie = new Movie("Inception", "Sci-Fi", 148, "PG-13", 2010);
    }

    @Test
    void shouldReturnAllMovies() {
        when(movieRepository.findAll()).thenReturn(List.of(testMovie));

        List<Movie> movies = movieService.getAllMovies();

        assertFalse(movies.isEmpty());
        assertEquals(1, movies.size());
    }

    @Test
    void shouldReturnMovieById() {
        when(movieRepository.findById(1L)).thenReturn(Optional.of(testMovie));

        Optional<Movie> movie = movieService.getMovieById(1L);

        assertTrue(movie.isPresent());
        assertEquals("Inception", movie.get().getTitle());
    }

    @Test
    void shouldDeleteMovieIfExists() {
        doNothing().when(movieRepository).deleteById(1L);

        assertDoesNotThrow(() -> movieService.deleteMovie(1L));
        verify(movieRepository, times(1)).deleteById(1L);
    }
}
