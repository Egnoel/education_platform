package com.egnoel.backend.core.config;


import com.egnoel.backend.core.util.JwtRequestFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    private final JwtRequestFilter jwtRequestFilter;

    public SecurityConfig(JwtRequestFilter jwtRequestFilter) {
        this.jwtRequestFilter = jwtRequestFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // Desativa CSRF usando lambda
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Sessão stateless
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/swagger-ui/**", "/swagger-ui/index.html", "/api-docs/**").permitAll()
                        .requestMatchers("/api/auth/**", "/api/auth/register/**", "/api/auth/login").permitAll()
                        .requestMatchers("/materials/**").hasRole("TEACHER")
                        .requestMatchers("/materials").hasAnyRole("TEACHER", "STUDENT")
                        .requestMatchers("/subjects/**").hasRole("TEACHER") // Ajustado de PROFESSOR para TEACHER
                        .requestMatchers("/subjects").hasAnyRole("TEACHER", "STUDENT")
                        .requestMatchers("/quizzes/*/questions/**").hasRole("TEACHER") // Apenas professores criam/editam/excluem perguntas
                        .requestMatchers("/quizzes/*/questions").hasAnyRole("TEACHER", "STUDENT") // Listagem de perguntas
                        .requestMatchers("/quizzes/*/answers").hasRole("TEACHER") // Professores listam respostas
                        .requestMatchers("/quizzes/*/answers/**").hasRole("STUDENT") // Alunos submetem respostas
                        .requestMatchers("/quizzes/**").hasRole("TEACHER") // Criação/edição/exclusão de quizzes
                        .requestMatchers("/quizzes").hasAnyRole("TEACHER", "STUDENT") // Listagem de quizzes
                        .requestMatchers("/classes/**").hasRole("TEACHER")
                        .requestMatchers("/classes").hasAnyRole("TEACHER", "STUDENT")
                        .requestMatchers("/academic-years/**").hasRole("ADMIN")
                        .requestMatchers("/academic-years", "/academic-years/active").hasAnyRole("ADMIN", "TEACHER", "STUDENT")
                        .requestMatchers("/dashboard").hasAnyRole("TEACHER", "STUDENT")
                        .requestMatchers("/institutions/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class); // Adiciona filtro JWT

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}
