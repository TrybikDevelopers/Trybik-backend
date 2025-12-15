package org.pkwmtt.security.authentication.authenticationProvider;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pkwmtt.security.authentication.authenticationToken.JwtAuthenticationToken;
import org.pkwmtt.security.jwt.JwtService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentAuthenticationProviderTest {

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private StudentAuthenticationProvider studentAuthenticationProvider;

    @Test
    void shouldAuthenticateWhenTokenIsValid(){
//        given
        Authentication auth = mock(JwtAuthenticationToken.class);

        when(auth.getCredentials()).thenReturn("token");
        doReturn("ROLE_STUDENT")
                .when(jwtService).extractClaim(eq("token"), ArgumentMatchers.<Function<Claims, String>>any());
        when(jwtService.getSubject(any(String.class))).thenReturn("11111111-2222-3333-4444-555555555555");

//        when
        Authentication result = studentAuthenticationProvider.authenticate(auth);

//        then
        assertTrue(result.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_STUDENT")));
        assertTrue(result.isAuthenticated());
        assertEquals("11111111-2222-3333-4444-555555555555", result.getPrincipal().toString());
        assertNull(result.getCredentials());
        assertEquals("ROLE_STUDENT", ((JwtAuthenticationToken) result).getSuperiorGroup());
    }

    @Test
    void shouldReturnNullWhenReceivedModeratorToken(){
//        given
        Authentication auth = mock(JwtAuthenticationToken.class);
        when(auth.getCredentials()).thenReturn("token");
        when(jwtService.extractClaim(any(String.class),any())).thenReturn("ROLE_MODERATOR");

//        when
        Authentication result = studentAuthenticationProvider.authenticate(auth);

//        then
        assertNull(result);
    }

    @Test
    void shouldThrowWhenTokenExpired(){
        //        TODO:
    }

    @Test
    void shouldThrowWhenSignatureIsInvalid(){
        //        TODO:
    }

    @Test
    void shouldThrowWhenUnableToParseToken(){
        //        TODO:
    }

}