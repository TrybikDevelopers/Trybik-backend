package org.pkwmtt.security.jwt.refreshToken.entity;

import jakarta.persistence.*;
import lombok.*;
import org.pkwmtt.moderator.entities.Moderator;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "moderator_refresh_tokens")
public class ModeratorRefreshToken implements RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer token_id;

    private String token;

    @ManyToOne
    @JoinColumn(name = "moderator_id")
    private Moderator moderator;

    private LocalDateTime created;

    private LocalDateTime expires;

    public ModeratorRefreshToken(String token, Moderator moderator) {
        this.token = token;
        this.moderator = moderator;
        this.created = LocalDateTime.now();
        this.expires = LocalDateTime.now().plusMonths(6);
    }

    public void updateToken(String token) {
        this.token = token;
        this.created = LocalDateTime.now();
    }


}