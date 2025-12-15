package org.pkwmtt.security.jwt.refreshToken.repository;

import org.pkwmtt.security.jwt.refreshToken.entity.ModeratorRefreshToken;
import org.springframework.stereotype.Repository;

@Repository
public interface ModeratorRefreshTokenRepository extends RefreshTokenRepository<ModeratorRefreshToken, Integer> {


}
