package org.pkwmtt.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.pkwmtt.security.apiKey.ApiKeyService;
import org.pkwmtt.security.authentication.authenticationToken.HeaderAuthenticationToken;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class AdminKeyFilter extends OncePerRequestFilter {

    private final ApiKeyService apiKeyService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        String adminKey = request.getHeader("x-admin-key");

        if(SecurityContextHolder.getContext().getAuthentication() == null && adminKey != null){
            if(!apiKeyService.existsInAdminKeyBase(adminKey))
                throw new BadCredentialsException("Invalid Admin Key");

            GrantedAuthority role = new SimpleGrantedAuthority("ROLE_ADMIN");
            Authentication auth = new HeaderAuthenticationToken(role);

            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        filterChain.doFilter(request, response);
    }

    @Bean
    public FilterRegistrationBean<AdminKeyFilter> registerAdminKeyFilter(AdminKeyFilter filter){
        FilterRegistrationBean<AdminKeyFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }
}
