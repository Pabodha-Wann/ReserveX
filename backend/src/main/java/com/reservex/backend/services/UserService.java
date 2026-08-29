package com.reservex.backend.services;

import com.reservex.backend.dto.UpdateProfileRequest;
import com.reservex.backend.dto.UserProfileDto;
import com.reservex.backend.entity.User;
import com.reservex.backend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public UserProfileDto getOrCreateFromToken(Jwt jwt) {
        String sub = jwt.getSubject();

        User user = userRepository.findByAuth0Sub(sub)
                .orElseGet(() -> {
                    // Read the email from the custom claim you added in Auth0 Actions
                    String email = jwt.getClaimAsString("https://api.reservex.com/email");

                    if (email == null) {
                        throw new IllegalArgumentException(
                                "Email is missing from the authentication token. Please verify your Auth0 Action.");
                    }

                    User newUser = User.builder()
                            .auth0Sub(sub)
                            .email(email)
                            .username(email) // Using email as username
                            .role(resolveRole(jwt))
                            .build();
                    return userRepository.save(newUser);
                });

        return UserProfileDto.fromEntity(user);
    }

    @Transactional
    public UserProfileDto updateProfile(String auth0Sub, UpdateProfileRequest request) {
        User user = userRepository.findByAuth0Sub(auth0Sub)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (request.getBusinessName() != null) {
            user.setBusinessName(request.getBusinessName());
        }
        user.setLastUpdatedAt(Instant.now());

        User saved = userRepository.save(user);
        return UserProfileDto.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public Integer getUserIdByAuth0Sub(String sub) {
        return userRepository.findByAuth0Sub(sub)
                .map(User::getId)
                .orElseThrow(() -> new IllegalArgumentException("User not found or not synced yet"));
    }

    private User.Role resolveRole(Jwt jwt) {
        // extracts the custom roles claim from the Auth0 token
        List<String> roles = jwt.getClaimAsStringList("https://api.reservex.com/roles");

        // if Auth0 says it is organizer assign as EMPLOYEE
        if (roles != null && roles.contains("Exhibition Organizer")) {
            return User.Role.EMPLOYEE;
        }
        // if not VENDOR
        return User.Role.VENDOR;
    }
}