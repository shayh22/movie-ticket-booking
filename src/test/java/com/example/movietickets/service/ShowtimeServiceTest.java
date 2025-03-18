package com.example.movietickets.service;

import com.example.movietickets.model.Movie;
import com.example.movietickets.model.Showtime;
import com.example.movietickets.repository.ShowtimeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShowtimeServiceTest {

    @Mock
    private ShowtimeRepository showtimeRepository;

    @InjectMocks
    private ShowtimeService showtimeService;

    private Showtime testShowtime;

    @BeforeEach
    void setUp() {
        Movie movie = new Movie(1L, "Interstellar", "Sci-Fi", 169, "PG-13", 2014);
        testShowtime = new Showtime(1L, movie.getId(), "Theater 1", LocalDateTime.now(), LocalDateTime.now().plusHours(2));
    }

    @Test
    void shouldReturnAllShowtimes() {
        when(showtimeRepository.findAll()).thenReturn(List.of(testShowtime));

        List<Showtime> showtimes = showtimeService.getAllShowtimes();

        assertFalse(showtimes.isEmpty());
        assertEquals(1, showtimes.size());
    }

    @Test
    void shouldDeleteShowtimeIfExists() {
        doNothing().when(showtimeRepository).deleteById(1L);

        assertDoesNotThrow(() -> showtimeService.deleteShowtime(1L));
        verify(showtimeRepository, times(1)).deleteById(1L);
    }
    
    @Test
    void shouldPreventOverlappingShowtimes() {
        when(showtimeRepository.existsByTheaterAndStartTimeBetween(any(), any(), any())).thenReturn(true);

        Exception exception = assertThrows(RuntimeException.class, () -> showtimeService.addShowtime(testShowtime));
        assertEquals("A showtime already exists in this theater at the given time.", exception.getMessage());
    }
}
