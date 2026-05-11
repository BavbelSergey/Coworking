package com.example.coworking.config;

import com.example.coworking.model.UserRole;
import com.example.coworking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

  private final UserRepository userRepository;

  @Bean
  public SecurityFilterChain securityFilterChain(
      HttpSecurity http,
      JwtAuthenticationFilter jwtAuthenticationFilter
  ) throws Exception {
    return http
        .csrf(AbstractHttpConfigurer::disable)
        .sessionManagement(session ->
            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(
                "/api/auth/**",
                "/swagger-ui/**",
                "/v3/api-docs/**",
                "/swagger-ui.html",
                "/error"
            ).permitAll()
            .requestMatchers(HttpMethod.POST, "/api/users").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/workspaces/**").hasAnyRole("ADMIN", "USER")
            .requestMatchers(HttpMethod.GET, "/api/amenities/**").hasAnyRole("ADMIN", "USER")
            .requestMatchers(HttpMethod.POST, "/api/bookings").hasAnyRole("ADMIN", "USER")
            .requestMatchers(HttpMethod.POST, "/api/bookings/*/cancel").hasAnyRole("ADMIN", "USER")
            .requestMatchers(HttpMethod.POST, "/api/payments").hasAnyRole("ADMIN", "USER")
            .requestMatchers(HttpMethod.GET, "/api/payments/check/booking/**")
            .hasAnyRole("ADMIN", "USER")
            .requestMatchers("/api/users/**").permitAll() //hasRole("ADMIN")
            .requestMatchers("/api/workspaces/**").hasRole("ADMIN")
            .requestMatchers("/api/amenities/**").hasRole("ADMIN")
            .requestMatchers("/api/bookings/**").hasRole("ADMIN")
            .requestMatchers("/api/payments/**").hasRole("ADMIN")
            .requestMatchers("/tasks/**", "/api/concurrency/race").permitAll()
            .anyRequest().authenticated()
        )
        .authenticationProvider(authenticationProvider())
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .build();
  }

  @Bean
  public AuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService());
    provider.setPasswordEncoder(passwordEncoder());
    return provider;
  }

  @Bean
  public UserDetailsService userDetailsService() {
    return email -> userRepository.findByEmail(email)
        .map(user -> org.springframework.security.core.userdetails.User
            .withUsername(user.getEmail())
            .password(user.getPassword())
            .authorities((user.getRole() == null ? UserRole.USER : user.getRole()).getAuthority())
            .build())
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
      throws Exception {
    return configuration.getAuthenticationManager();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
