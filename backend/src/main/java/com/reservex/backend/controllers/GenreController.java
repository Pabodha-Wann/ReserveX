// Add/remove genres for reservations


package com.reservex.backend.controllers;

import com.reservex.backend.dto.StallGenreRequest;
import com.reservex.backend.entity.ReservationGenre;
import com.reservex.backend.services.ReservationGenreService;
import com.reservex.backend.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/genres")
@RequiredArgsConstructor
public class GenreController {

    private final ReservationGenreService genreService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<String>> getMyGenres(@AuthenticationPrincipal Jwt jwt) {
        Integer userId = userService.getUserIdByEmail(jwt.getClaimAsString("email"));
        return ResponseEntity.ok(genreService.getGenresByUser(userId));
    }

//    @PostMapping
//    public ResponseEntity<?> addGenre(
//            @AuthenticationPrincipal UserPrincipal principal,
//            @RequestBody Map<String, String> body) {
//        String genreName = body.get("genreName");
//        if (genreName == null || genreName.isBlank()) {
//            return ResponseEntity.badRequest().body(Map.of("message", "genreName is required"));
//        }
//        ReservationGenre genre = genreService.addGenre(principal.getId(), genreName);
//        return ResponseEntity.ok(Map.of("id", genre.getId(), "genreName", genre.getGenreName()));
//    }

    @PutMapping
    public ResponseEntity<?> setGenres(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody List<StallGenreRequest> genreNames) {
        Integer userId = userService.getUserIdByEmail(jwt.getClaimAsString("email"));
        genreService.setGenresPerStall(userId, genreNames);
        return ResponseEntity.ok(Map.of("message", "Genres updated"));
    }
}
