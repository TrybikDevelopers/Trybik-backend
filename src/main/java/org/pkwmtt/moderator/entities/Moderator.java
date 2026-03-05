package org.pkwmtt.moderator.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "moderators")
public class Moderator {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "moderator_id")
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID moderatorId;

    @Column(nullable = false)
    private String password;

    @Column(length = 50)
    private String role;
    
    public Moderator(String password) {
        this.password = password;
        this.role = "MODERATOR";
    }
}

