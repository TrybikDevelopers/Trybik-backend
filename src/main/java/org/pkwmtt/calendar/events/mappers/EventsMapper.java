package org.pkwmtt.calendar.events.mappers;

import org.pkwmtt.calendar.enities.SuperiorGroup;
import org.pkwmtt.calendar.events.dto.EventDTO;
import org.pkwmtt.calendar.events.entities.Event;
import org.pkwmtt.calendar.events.entities.EventType;

import java.util.List;

/**
 * Utility mapper for converting between Event entities, EventType entities and EventDTOs.
 * <p>
 * All methods are static and stateless; this class provides a centralized place for
 * transformation logic used when exchanging data between persistence/entities and DTOs.
 */
public class EventsMapper {
    
    /**
     * Map an {@link Event} entity to an {@link EventDTO}.
     * <p>
     * The mapping includes:
     * - title
     * - description
     * - type name
     * - start and end dates
     * - superior group names (converted from {@link SuperiorGroup})
     * <p>
     * Note: this method assumes that required nested properties (like {@code event.getType()}
     * and {@code event.getSuperiorGroups()}) are present; callers should handle potential
     * {@code null} values if needed.
     *
     * @param event the source Event entity to map
     * @return a populated EventDTO representing the given Event
     */
    public static EventDTO mapEventToEventDTO (Event event) {
        return new EventDTO()
          .setId(event.getId())
          .setTitle(event.getTitle())
          .setDescription(event.getDescription())
          .setType(event.getType().getName())
          .setStartDate(event.getStartDate())
          .setEndDate(event.getEndDate())
          .setSuperiorGroups(event.getSuperiorGroups().stream().map(SuperiorGroup::getName).toList()
          );
        
    }
    
    /**
     * Map an {@link EventDTO} and a resolved {@link EventType} into a new {@link Event} entity
     * Assumes {@code eventDTO} is non-null. The provided {@code type} is assigned to the
     * created Event. This method does not populate superior groups; callers should set
     * them on the returned Event if required.
     *
     * @param eventDTO DTO containing event fields to map
     * @param type resolved EventType to attach to the created Event
     * @return a new Event populated from the DTO and provided type
     */
    public static Event mapEventDTOToEvent (EventDTO eventDTO, EventType type) {
        
        return new Event(
          eventDTO.getId(),
          eventDTO.getTitle(),
          eventDTO.getDescription(),
          type,
          eventDTO.getStartDate(),
          eventDTO.getEndDate()
        );
    }
    
    /**
     * Convert a list of {@link EventType} entities to a list of their names.
     * <p>
     * Example usage: converting persisted event types to a simple list of strings for DTOs
     * or UI consumption.
     *
     * @param eventTypes list of EventType entities to convert
     * @return list of names extracted from the provided eventTypes
     */
    public static List<String> mapEventTypeListToListOfString (List<EventType> eventTypes) {
        return eventTypes.stream().map(EventType::getName).toList();
    }
}