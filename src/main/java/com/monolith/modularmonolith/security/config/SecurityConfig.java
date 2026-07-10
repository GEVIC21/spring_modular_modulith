package com.monolith.modularmonolith.security.config;

import com.monolith.modularmonolith.security.jwt.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // ⬇️ CORS activé AVANT tout le reste
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        // Auth public
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers("/api/v1/public/**").permitAll()

                        // Uploads d'images (avatars)
                        .requestMatchers(HttpMethod.GET, "/uploads/**").permitAll()

                        // Admin uniquement
                        .requestMatchers("/api/v1/admin/**").hasAnyRole("SUPERADMIN", "ADMIN")

                        // Enseignant peut voir élèves mais pas tout admin
                        .requestMatchers(HttpMethod.GET, "/api/v1/admin/students/**").hasAnyRole("SUPERADMIN", "ADMIN", "ENSEIGNANT")

                        // Profil personnel (tous les utilisateurs authentifiés)
                        .requestMatchers("/api/v1/users/profile/**").authenticated()
                        .requestMatchers("/api/v1/users/password/**").authenticated()

                        // Tout le reste nécessite authentification
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Configuration CORS pour le frontend Angular (localhost:4200).
     * En production, remplace l'origine par ton domaine déployé.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Origine(s) autorisée(s)
        // ⚠️ Avec allowCredentials=true, on ne peut PAS utiliser "*" pour allowedOrigins
        configuration.setAllowedOrigins(List.of("http://localhost:4200"));

        // Si tu as besoin de plusieurs origines dynamiques (dev + staging + prod),
        // remplace par :
        // configuration.setAllowedOriginPatterns(List.of("http://localhost:*", "https://*.ecole.fr"));

        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(List.of(
                "Authorization",
                "Content-Type",
                "Accept",
                "Origin",
                "X-Requested-With"
        ));
        configuration.setExposedHeaders(List.of("Authorization", "Content-Disposition"));
        configuration.setAllowCredentials(true); // Indispensable pour envoyer le JWT en header ou cookies
        configuration.setMaxAge(3600L); // Cache le preflight pendant 1h

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}