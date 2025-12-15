package org.pkwmtt.security.jwt.refreshToken.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface RefreshTokenRepository<RT, ID> extends JpaRepository<RT, ID> {

    long deleteByToken(String token);

    default Boolean deleteTokenAsBoolean(String token) {
        return deleteByToken(token) > 0;
    }
}
