package org.pkwmtt.calendar.events.controllers;

import lombok.RequiredArgsConstructor;
import org.pkwmtt.calendar.events.dto.EventDTO;
import org.pkwmtt.calendar.events.services.EventsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller that exposes endpoints for working with calendar events.
 * <p>
 * Requests are prefixed with the configurable property {@code apiPrefix}.
 * Delegates business logic to {@link EventsService}.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("${apiPrefix}/events")
public class EventsController {
    /**
     * Service providing event-related operations. Injected via Lombok's
     * {@code @RequiredArgsConstructor}.
     */
    final EventsService service;
    
    /**
     * Retrieve events optionally filtered by a superior group identifier.
     *
     * @param superiorGroup optional query parameter (name = "g") representing the superior group id.
     *                      If {@code null}, the service is called with {@code null} and is expected
     *                      to return all events or the appropriate unfiltered result.
     * @return HTTP 200 with a list of {@link EventDTO} matching the filter (or all events when no filter provided).
     */
    @GetMapping
    public ResponseEntity<List<EventDTO>> getAllEvents (@RequestParam(required = false, name = "g") String superiorGroup) {
        var response = superiorGroup != null
          ? service.getEventsForSuperiorGroup(superiorGroup)
          : service.getAllEvents();
        
        return ResponseEntity.ok().body(response);
    }
    
    /**
     * Retrieve all distinct event types.
     *
     * @return HTTP 200 with a list of event type names.
     */
    @GetMapping("/types")
    public ResponseEntity<List<String>> getAllEventTypes () {
        return ResponseEntity.ok().body(service.getAllEventTypes());
    }
}