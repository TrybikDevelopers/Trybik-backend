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
//                  public (require API key)
                  .requestMatchers(HttpMethod.GET , "${apiPrefix}/exams/**").permitAll()
                  .requestMatchers("${apiPrefix}/timetables/**").permitAll()
//                  TODO: require anti-spam validation
                  .requestMatchers("${apiPrefix}/bug-reports").permitAll()

//                  student
                  .requestMatchers(HttpMethod.POST , "${apiPrefix}/exams").hasRole(Role.STUDENT.toString())
                  .requestMatchers(HttpMethod.PUT , "${apiPrefix}/exams").hasRole(Role.STUDENT.toString())
                  .requestMatchers(HttpMethod.DELETE , "${apiPrefix}/exams").hasRole(Role.STUDENT.toString())
                  .requestMatchers("${apiPrefix}/student/authenticate").permitAll()
                  .requestMatchers("${apiPrefix}/student/refresh").permitAll()
                  .requestMatchers("${apiPrefix}/student/logout").permitAll()

//                  moderator
                  .requestMatchers(HttpMethod.POST ,"${apiPrefix}/moderator/authenticate").permitAll()
                  .requestMatchers("${apiPrefix}/moderator/refresh").permitAll()
                  .requestMatchers("${apiPrefix}/moderator/**").hasRole(Role.MODERATOR.toString())

//                  admin
                  .requestMatchers("/admin/**").hasRole(Role.ADMIN.toString())

//                  file
//                  TODO: [BEFORE MERGE] delete after upload to google play or permit
                  .requestMatchers("${apiPrefix}/apk/**").denyAll()

//                  other
//                  TODO: refactor or remove
                  .requestMatchers("/global/metrics").denyAll()
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
