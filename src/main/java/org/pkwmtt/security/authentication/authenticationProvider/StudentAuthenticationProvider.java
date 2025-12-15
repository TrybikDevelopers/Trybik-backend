package org.pkwmtt.security.authentication.authenticationProvider;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import org.pkwmtt.security.authentication.authenticationToken.JwtAuthenticationToken;
import org.pkwmtt.security.jwt.JwtService;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Objects;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class StudentAuthenticationProvider implements AuthenticationProvider {

    private final JwtService jwtService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        //        get data
        JwtAuthenticationToken auth = (JwtAuthenticationToken) authentication;
        String token = auth.getCredentials();

//        verify token and data
        try {
            if (!Objects.equals(
                    jwtService.extractClaim(token, claims -> claims.get("role", String.class)),
                    "ROLE_STUDENT")
            )
                return null;
        } catch (ExpiredJwtException e) {
            throw new CredentialsExpiredException("Token has expired");
        } catch (SignatureException e) {
            throw new BadCredentialsException("Invalid JWT token");
        } catch (JwtException e) {
            throw new AuthenticationServiceException("Authentication failed");
        }

//        verify user
        UUID subject = UUID.fromString(jwtService.getSubject(token));
        String superiorGroup = jwtService.extractClaim(token, claims -> claims.get("group", String.class));

//        authentication successful
        GrantedAuthority role = new SimpleGrantedAuthority("ROLE_STUDENT");
        return new JwtAuthenticationToken(subject, Collections.singletonList(role), superiorGroup);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
