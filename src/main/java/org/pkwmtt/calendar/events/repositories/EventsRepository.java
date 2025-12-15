package org.pkwmtt.calendar.events.repositories;

import org.pkwmtt.calendar.events.entities.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventsRepository extends JpaRepository<Event, Integer> {
}
