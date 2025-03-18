package com.example.movietickets.service;

import com.example.movietickets.model.Showtime;
import com.example.movietickets.repository.ShowtimeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ShowtimeService {
    private final ShowtimeRepository showtimeRepository;

    public ShowtimeService(ShowtimeRepository showtimeRepository) {
        this.showtimeRepository = showtimeRepository;
    }

    public List<Showtime> getAllShowtimes() {
        return showtimeRepository.findAll();
    }

    public Optional<Showtime> getShowtimeById(Long id) {
        return showtimeRepository.findById(id);
    }

    public Showtime addShowtime(Showtime showtime) {
        // Prevent overlapping showtimes for the same theater
        boolean overlapExists = showtimeRepository.existsByTheaterAndStartTimeBetween(
            showtime.getTheater(), showtime.getStartTime(), showtime.getEndTime()
        );

        if (overlapExists) {
            throw new RuntimeException("A showtime already exists in this theater at the given time.");
        }

        return showtimeRepository.save(showtime);
    }

    public Showtime updateShowtime(Long id, Showtime updatedShowtime) {
        return showtimeRepository.findById(id)
                .map(showtime -> {
                    showtime.setTheater(updatedShowtime.getTheater());
                    showtime.setStartTime(updatedShowtime.getStartTime());
                    showtime.setEndTime(updatedShowtime.getEndTime());
                    return showtimeRepository.save(showtime);
                }).orElseThrow(() -> new RuntimeException("Showtime not found"));
    }

    public void deleteShowtime(Long id) {
        showtimeRepository.deleteById(id);
    }
}
