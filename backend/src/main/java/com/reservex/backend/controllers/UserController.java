package com.reservex.backend.controllers;
import com.reservex.backend.dto.UpdateProfileRequest;
import com.reservex.backend.dto.UserProfileDto;
import com.reservex.backend.services.UserService;
import org.springframework.security.oauth2.jwt.Jwt;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<?> me(@AuthenticationPrincipal Jwt jwt) {
        try {
            UserProfileDto profile = userService.getOrCreateFromToken(jwt);
            return ResponseEntity.ok(profile);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorMessage(e.getMessage()));
        }
    }

    @PutMapping("/me")
    public ResponseEntity<?> updateProfile(@AuthenticationPrincipal Jwt jwt,
                                           @Valid @RequestBody UpdateProfileRequest request) {
        UserProfileDto updated = userService.updateProfile(jwt.getSubject(), request);
        return ResponseEntity.ok(updated);
    }

    public record ErrorMessage(String message) {}
}

