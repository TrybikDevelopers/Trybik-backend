package org.pkwmtt.calendar.events.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.pkwmtt.calendar.enities.SuperiorGroup;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "events")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Event {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    int id;
    
    @Column(name = "title")
    String title;
    
    @Column(name = "description")
    String description;
    
    @Column(name = "start_date")
    Date startDate;
    
    @Column(name = "end_date")
    Date endDate;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type", referencedColumnName = "event_type_id")
    EventType type;
    
    @ManyToMany
    @JoinTable(
      name = "events_superior_group",
      joinColumns = @JoinColumn(name = "event_id"),
      inverseJoinColumns = @JoinColumn(name = "superior_group_id")
    )
    List<SuperiorGroup> superiorGroups;
    
    
    public Event (int id, String title, String description, EventType type, Date startDate, Date endDate) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.type = type;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
