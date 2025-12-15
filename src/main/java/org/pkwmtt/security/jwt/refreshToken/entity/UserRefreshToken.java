package org.pkwmtt.security.jwt.refreshToken.entity;

import jakarta.persistence.*;
import lombok.*;
import org.pkwmtt.calendar.exams.entity.Representative;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "user_refresh_tokens")
public class UserRefreshToken implements RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer token_id;
    
    private String token;
    
    @ManyToOne
    @JoinColumn(name = "representative_id")
    private Representative representative;
    
    @Column(name = "created_at")
    private LocalDateTime created;
    
    @Column(name = "expires_at")
    private LocalDateTime expires;
    
    public UserRefreshToken (String token, Representative representative) {
        this.token = token;
        this.representative = representative;
        this.created = LocalDateTime.now();
        this.expires = LocalDateTime.now().plusMonths(6);
    }
    
    public void updateToken (String token) {
        this.token = token;
        this.created = LocalDateTime.now();
    }
}
