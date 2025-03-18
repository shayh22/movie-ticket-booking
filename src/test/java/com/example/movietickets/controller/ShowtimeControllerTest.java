package com.example.movietickets.controller;

import com.example.movietickets.model.Movie;
import com.example.movietickets.model.Showtime;
import com.example.movietickets.service.ShowtimeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Rollback
class ShowtimeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ShowtimeService showtimeService;

    @Test
    void testGetAllShowtimes() throws Exception {
        Movie movie = new Movie("Test Movie", "Drama", 120, "PG-13", 2024);
        Showtime showtime = new Showtime(1L, movie.getId(), "Test Theater", LocalDateTime.now(), LocalDateTime.now().plusHours(2));

        when(showtimeService.getAllShowtimes()).thenReturn(Collections.singletonList(showtime));

        mockMvc.perform(get("/api/showtimes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].theater").value("Test Theater"));
    }

    @Test
    void testAddShowtime() throws Exception {
        Movie movie = new Movie("Test Movie", "Drama", 120, "PG-13", 2024);
        Showtime showtime = new Showtime(null, movie.getId(), "Test Theater", LocalDateTime.now(), LocalDateTime.now().plusHours(2));

        when(showtimeService.addShowtime(any(Showtime.class))).thenReturn(showtime);

        mockMvc.perform(post("/api/showtimes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(showtime)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.theater").value("Test Theater"));
    }

    @Test
    void testDeleteShowtime() throws Exception {
        doNothing().when(showtimeService).deleteShowtime(1L);

        mockMvc.perform(delete("/api/showtimes/{id}", 1L))
                .andExpect(status().isNoContent());
    }
}
