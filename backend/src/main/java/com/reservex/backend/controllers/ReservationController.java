// Reserve stall(s) endpoint
// “My reservations” endpoint


package com.reservex.backend.controllers;

import com.reservex.backend.dto.ReservationDto;
import com.reservex.backend.services.ReservationGenreService;
import com.reservex.backend.services.ReservationService;
import com.reservex.backend.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;
    private final ReservationGenreService genreService;
    private final UserService userService;
    
    @PostMapping
    public ResponseEntity<?> createReservation(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody Map<String, Object> body) {
        Integer userId = userService.getUserIdByEmail(jwt.getClaimAsString("email"));
        try {
            if (body.get("stallIds") != null || body.get("stall_ids") != null) {
                @SuppressWarnings("unchecked")
                List<?> rawList = (List<?>) (body.get("stall_ids") != null ? body.get("stall_ids") : body.get("stallIds"));
                List<Integer> stallIds = rawList.stream()
                        .map(id -> id instanceof Number n ? n.intValue() : Integer.parseInt(id.toString()))
                        .toList();
                List<ReservationDto> dtos = reservationService.createReservations(userId, stallIds);
                return ResponseEntity.status(HttpStatus.CREATED).body(dtos);
            }
            Object stallIdObj = body.get("stallId");
            if (stallIdObj == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "stallId or stallIds is required"));
            }
            Integer stallId = stallIdObj instanceof Number n ? n.intValue() : Integer.parseInt(stallIdObj.toString());
            ReservationDto dto = reservationService.createReservation(userId, stallId);
            return ResponseEntity.status(HttpStatus.CREATED).body(dto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/my")
    public ResponseEntity<List<ReservationDto>> getMyReservations(@AuthenticationPrincipal Jwt jwt) {
        Integer userId = userService.getUserIdByEmail(jwt.getClaimAsString("email"));
        return ResponseEntity.ok(reservationService.getMyReservations(userId));
    }

}
