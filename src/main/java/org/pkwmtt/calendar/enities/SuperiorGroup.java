package org.pkwmtt.calendar.enities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "superior_groups")
public class SuperiorGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "superior_group_id")
    private Integer superiorGroupId;

    @Column(nullable = false)
    private String name;
}
