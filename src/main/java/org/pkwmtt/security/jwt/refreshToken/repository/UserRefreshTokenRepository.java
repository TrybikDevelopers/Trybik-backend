package org.pkwmtt.security.jwt.refreshToken.repository;

import org.pkwmtt.security.jwt.refreshToken.entity.UserRefreshToken;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRefreshTokenRepository extends RefreshTokenRepository<UserRefreshToken, Integer>{


}
