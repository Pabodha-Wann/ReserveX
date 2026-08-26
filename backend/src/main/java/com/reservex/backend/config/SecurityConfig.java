package com.reservex.backend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {


    private final CorsConfigurationSource corsConfigurationSource;
    private final JwtDecoder jwtDecoder;


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(cors -> cors.configurationSource(corsConfigurationSource))

                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests((authorize) -> authorize
                        // Public endpoints
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/stalls/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/genres").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/contact").permitAll()
                        // Admin endpoints - only EMPLOYEE role
                        .requestMatchers("/api/admin/**").hasRole("EXHIBITION_ORGANIZER")
                        .requestMatchers(HttpMethod.POST, "/api/stalls/**").hasRole("EXHIBITION_ORGANIZER")
                        .requestMatchers(HttpMethod.PUT, "/api/stalls/**").hasRole("EXHIBITION_ORGANIZER")
                        .requestMatchers(HttpMethod.DELETE, "/api/stalls/**").hasRole("EXHIBITION_ORGANIZER")
                        
                        // Reservation endpoints - authenticated users
                        .requestMatchers("/api/reservations/**").authenticated()
                        
                        // All other requests require authentication
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2->oauth2
                        .jwt(jwt -> jwt
                                .decoder(jwtDecoder)
                                .jwtAuthenticationConverter(new Auth0RolesConverter())
                        )

                );

        return http.build();
    }
}
