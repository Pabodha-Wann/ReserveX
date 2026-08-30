package com.reservex.backend.services;

import com.reservex.backend.dto.StallGenreRequest;
import com.reservex.backend.entity.Reservation;
import com.reservex.backend.entity.ReservationGenre;
import com.reservex.backend.entity.User;
import com.reservex.backend.repositories.ReservationGenreRepository;
import com.reservex.backend.repositories.ReservationRepository;
import com.reservex.backend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationGenreService {

    private final ReservationGenreRepository genreRepository;
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<String> getGenresByUser(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        List<Reservation> allReservations = reservationRepository.findByUserOrderByReservationDateDesc(user);
        
        return allReservations.stream()
                .flatMap(res -> genreRepository.findByReservation(res).stream())
                .map(ReservationGenre::getGenreName)
                .distinct()
                .collect(Collectors.toList());
    }

    @Transactional
    public void setGenresPerStall(Integer userId, List<StallGenreRequest> requests) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Get ALL reservations for this user
        List<Reservation> allReservations = reservationRepository.findByUserOrderByReservationDateDesc(user);

        // Extract all stall IDs from the request so we only clear those stalls
        List<Integer> stallIdsInRequest = requests != null ? requests.stream()
                .map(StallGenreRequest::getStallId)
                .toList() : List.of();

        // Clear old genres ONLY for the stalls being updated
        for (Reservation res : allReservations) {
            res.getReservationGenres().removeIf(genre -> stallIdsInRequest.contains(genre.getStallId()));
            reservationRepository.save(res);
        }

        // Save the new genres attached to stalls
        if (requests != null) {
            for (StallGenreRequest req : requests) {
                // Find which reservation owns this specific stall
                Reservation owningReservation = allReservations.stream()
                        .filter(res -> res.getStalls().stream().anyMatch(s -> s.getId().equals(req.getStallId())))
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("Unauthorized: You do not own stall ID " + req.getStallId()));

                if (req.getGenres() != null) {
                    for (String genreName : req.getGenres()) {
                        if (genreName != null && !genreName.isBlank()) {
                            ReservationGenre genre = new ReservationGenre(
                                    owningReservation,
                                    req.getStallId(),
                                    genreName.trim());
                            owningReservation.getReservationGenres().add(genre);
                        }
                    }
                }
            }
        }
        
        // Final flush
        for (Reservation res : allReservations) {
            reservationRepository.save(res);
        }
        reservationRepository.flush();
    }

}
