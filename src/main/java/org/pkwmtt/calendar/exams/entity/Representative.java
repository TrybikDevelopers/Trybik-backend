package org.pkwmtt.calendar.exams.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.pkwmtt.calendar.enities.SuperiorGroup;

import java.util.UUID;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "representatives")
public class Representative {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "representative_id")
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID representativeId;

    @ManyToOne
    @JoinColumn(name = "superior_group_id", nullable = false)
    private SuperiorGroup superiorGroup;

    @Column(nullable = false)
    private String email;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;
}

