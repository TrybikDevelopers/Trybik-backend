package org.pkwmtt.security.authentication.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class JwtAuthenticationDto {
    private String accessToken;
    private String refreshToken;
}
