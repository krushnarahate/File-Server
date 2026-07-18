package com.krushna.fileserver.config;

import com.krushna.fileserver.security.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )
                .authorizeHttpRequests(auth -> auth

                        // Public Pages
                        .requestMatchers(
                                "/auth/**",
                                "/login-page",
                                "/signup-page",
                                "/dashboard-page",
                                "/activities-page",
                                "/admin-page",
                                "/shared-files-page",
                                "/css/**",
                                "/js/**"
                        ).permitAll()

                        // Admin APIs
                        .requestMatchers("/admin/**")
                        .hasRole("ADMIN")

                        // Authenticated APIs
                        .requestMatchers(
                                "/file/**",
                                "/shared-file/**"
                        ).authenticated()

                        .anyRequest().authenticated()
                )

                .addFilterBefore(
                        jwtFilter,
                        UsernamePasswordAuthenticationFilter.class
                )

                .httpBasic(basic -> basic.disable())

                .formLogin(form -> form.disable());

        return http.build();
    }
}
