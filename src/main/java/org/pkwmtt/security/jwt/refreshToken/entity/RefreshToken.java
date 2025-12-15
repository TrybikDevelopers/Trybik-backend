package org.pkwmtt.security.jwt.refreshToken.entity;

import java.time.LocalDateTime;

public interface RefreshToken {
    String getToken();
    LocalDateTime getExpires();
}
