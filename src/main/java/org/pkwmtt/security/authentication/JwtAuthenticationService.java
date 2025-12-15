package org.pkwmtt.security.authentication;

import io.jsonwebtoken.JwtException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.pkwmtt.calendar.exams.entity.Representative;
import org.pkwmtt.exceptions.InvalidRefreshTokenException;
import org.pkwmtt.security.authentication.dto.JwtAuthenticationDto;
import org.pkwmtt.security.authentication.dto.RefreshRequestDto;
import org.pkwmtt.security.jwt.JwtService;
import org.pkwmtt.security.jwt.refreshToken.entity.RefreshToken;
import org.pkwmtt.security.jwt.refreshToken.entity.UserRefreshToken;
import org.pkwmtt.security.jwt.refreshToken.repository.UserRefreshTokenRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class JwtAuthenticationService {
    private final JwtService jwtService;
    private final UserRefreshTokenRepository userRefreshTokenRepository;
    private final PasswordEncoder passwordEncoder;


    public JwtAuthenticationDto refresh(RefreshRequestDto requestDto) throws JwtException {

        UserRefreshToken userRefreshToken = findRefreshToken(requestDto.getRefreshToken());
        JwtService.validateRefreshToken(userRefreshToken);

        String tokenHash = JwtService.generateRefreshToken();

        userRefreshToken.updateToken(passwordEncoder.encode(tokenHash));
        userRefreshTokenRepository.save(userRefreshToken);

        Representative representative = userRefreshToken.getRepresentative();

        return JwtAuthenticationDto.builder()
                .refreshToken(tokenHash)
                .accessToken(jwtService.generateAccessToken(representative))
                .build();
    }

    public void logout(RefreshRequestDto requestDto) {
        RefreshToken refreshToken = findRefreshToken(requestDto.getRefreshToken());
        if(!userRefreshTokenRepository.deleteTokenAsBoolean(refreshToken.getToken()))
            throw new InvalidRefreshTokenException();
    }

    public String getNewUserRefreshToken(Representative representative) {
        String token = JwtService.generateRefreshToken();
        userRefreshTokenRepository.save(new UserRefreshToken(passwordEncoder.encode(token), representative));
        return token;
    }

    private UserRefreshToken findRefreshToken(String token)
            throws InvalidRefreshTokenException {
        List<UserRefreshToken> refreshTokens = userRefreshTokenRepository.findAll();
        return refreshTokens.stream()
                .filter(rt -> passwordEncoder.matches(token, rt.getToken()))
                .findFirst().orElseThrow(InvalidRefreshTokenException::new);
    }

}
