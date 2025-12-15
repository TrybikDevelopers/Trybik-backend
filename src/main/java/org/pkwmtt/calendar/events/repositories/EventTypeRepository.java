package org.pkwmtt.calendar.events.repositories;

import org.pkwmtt.calendar.events.entities.EventType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


@SuppressWarnings("unused")
public interface EventTypeRepository extends JpaRepository<EventType, Integer> {
    Optional<EventType> findByName (String name);
}
