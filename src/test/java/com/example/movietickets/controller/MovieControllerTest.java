package com.example.movietickets.controller;

import com.example.movietickets.model.Movie;
import com.example.movietickets.repository.MovieRepository;
import com.example.movietickets.repository.ShowtimeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Rollback
class MovieControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MovieRepository movieRepository;
    
    @Autowired
    private ShowtimeRepository showtimeRepository; 

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    @Transactional
    void setup() {
    	
    	showtimeRepository.deleteAll(); 
        movieRepository.deleteAll();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminShouldAddMovie() throws Exception {
        Movie movie = new Movie(null, "Inception", "Sci-Fi", 148, "8.8", 2010);

        mockMvc.perform(post("/api/movies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(movie)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.title", is("Inception")));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void customerShouldNotAddMovie() throws Exception {
        Movie movie = new Movie(null, "Inception", "Sci-Fi", 148, "8.8", 2010);

        mockMvc.perform(post("/api/movies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(movie)))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    void anyUserCanGetAllMovies() throws Exception {
        movieRepository.save(new Movie(null, "Inception", "Sci-Fi", 148, "8.8", 2010));
        movieRepository.save(new Movie(null, "Matrix", "Action", 136, "8.7", 1999));

        mockMvc.perform(get("/api/movies"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[*].title", containsInAnyOrder("Inception", "Matrix")));
    }

    @Test
    @WithMockUser
    void userShouldGetMovieById() throws Exception {
        Movie saved = movieRepository.save(new Movie(null, "Inception", "Sci-Fi", 148, "8.8", 2010));

        mockMvc.perform(get("/api/movies/{id}", saved.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title", is("Inception")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminShouldUpdateMovie() throws Exception {
        Movie original = movieRepository.save(new Movie(null, "Inception", "Sci-Fi", 148, "8.8", 2010));
        Movie updated = new Movie(null, "Inception 2", "Sci-Fi", 150, "9.0", 2024);

        mockMvc.perform(put("/api/movies/{id}", original.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updated)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title", is("Inception 2")))
            .andExpect(jsonPath("$.duration", is(150)));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void customerShouldNotUpdateMovie() throws Exception {
        Movie original = movieRepository.save(new Movie(null, "Inception", "Sci-Fi", 148, "8.8", 2010));
        Movie updated = new Movie(null, "Inception 2", "Sci-Fi", 150, "9.0", 2024);

        mockMvc.perform(put("/api/movies/{id}", original.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updated)))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminShouldDeleteMovie() throws Exception {
        Movie movie = movieRepository.save(new Movie(null, "Inception", "Sci-Fi", 148, "8.8", 2010));

        mockMvc.perform(delete("/api/movies/{id}", movie.getId()))
            .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void customerShouldNotDeleteMovie() throws Exception {
        Movie movie = movieRepository.save(new Movie(null, "Inception", "Sci-Fi", 148, "8.8", 2010));

        mockMvc.perform(delete("/api/movies/{id}", movie.getId()))
            .andExpect(status().isForbidden());
    }
}
