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
                                .headers(headers -> headers
                                                .frameOptions(frameOptions -> frameOptions.deny())
                                                .contentSecurityPolicy(csp -> csp.policyDirectives("default-src 'self'; frame-ancestors 'none';"))
                                                .httpStrictTransportSecurity(hsts -> hsts.includeSubDomains(true).maxAgeInSeconds(31536000))
                                )
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .authorizeHttpRequests((authorize) -> authorize
                                                // Public endpoints
                                                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                                                .requestMatchers("/api/auth/**").permitAll()
                                                .requestMatchers(HttpMethod.GET, "/api/stalls/**").permitAll()
                                                .requestMatchers(HttpMethod.GET, "/api/genres").permitAll()
                                                .requestMatchers(HttpMethod.POST, "/api/contact").permitAll()
                                                // Admin endpoints - only EMPLOYEE role
                                                .requestMatchers("/api/admin/**").hasRole("EMPLOYEE")
                                                .requestMatchers(HttpMethod.POST, "/api/stalls/**")
                                                .hasRole("EMPLOYEE")
                                                .requestMatchers(HttpMethod.PUT, "/api/stalls/**")
                                                .hasRole("EMPLOYEE")
                                                .requestMatchers(HttpMethod.DELETE, "/api/stalls/**")
                                                .hasRole("EMPLOYEE")

                                                // Reservation endpoints - authenticated users
                                                .requestMatchers("/api/reservations/**").authenticated()

                                                // All other requests require authentication
                                                .anyRequest().authenticated())
                                .oauth2ResourceServer(oauth2 -> oauth2
                                                .jwt(jwt -> jwt
                                                                .decoder(jwtDecoder)
                                                                .jwtAuthenticationConverter(new Auth0RolesConverter()))

                                );

                return http.build();
        }
}
