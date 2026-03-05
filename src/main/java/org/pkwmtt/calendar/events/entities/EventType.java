package org.pkwmtt.calendar.events.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "event_types")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class EventType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_type_id")
    private int id;
    
    @Column(name = "name")
    private String name;
}
