package org.pkwmtt.security.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pkwmtt.examCalendar.enums.Role;
import org.pkwmtt.security.filter.AdminKeyFilter;
import org.pkwmtt.security.filter.ApiKeyFilter;
import org.pkwmtt.security.filter.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@EnableWebSecurity
@EnableMethodSecurity
@Slf4j
@Configuration
@RequiredArgsConstructor
public class SpringSecurity {

    private final JwtFilter jwtFilter;
    private final AdminKeyFilter adminKeyFilter;
    private final ApiKeyFilter apiKeyFilter;
    
    @Bean
    public SecurityFilterChain filterChain (HttpSecurity http) throws Exception {
        log.info("Configuring Security Filter Chain...");
        http
          .cors(withDefaults())
          .csrf(AbstractHttpConfigurer::disable)
          .authorizeHttpRequests(auth -> auth
                  .requestMatchers(HttpMethod.GET , "/pkwmtt/api/v1/exams").permitAll()

                  .requestMatchers(HttpMethod.POST , "/pkwmtt/api/v1/exams").hasRole(Role.STUDENT.toString())
                  .requestMatchers(HttpMethod.PUT , "/pkwmtt/api/v1/exams").hasRole(Role.STUDENT.toString())
                  .requestMatchers(HttpMethod.DELETE , "/pkwmtt/api/v1/exams").hasRole(Role.STUDENT.toString())

                  .requestMatchers("/pkwmtt/api/v1/moderator/authenticate").permitAll()
                  .requestMatchers("/pkwmtt/api/v1/moderator/refresh").permitAll()
                  .requestMatchers("/pkwmtt/api/v1/moderator/**").hasRole(Role.MODERATOR.toString())

                  .requestMatchers("/admin").hasRole(Role.ADMIN.toString())

                  .anyRequest().denyAll()
          )
          .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(apiKeyFilter, JwtFilter.class)
                .addFilterBefore(adminKeyFilter, ApiKeyFilter.class);
        log.info("Configuring Success...");
        return http.build();
    }
}
