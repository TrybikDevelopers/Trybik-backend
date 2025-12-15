package org.pkwmtt.calendar.events.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pkwmtt.calendar.enities.SuperiorGroup;
import org.pkwmtt.calendar.events.dto.EventDTO;
import org.pkwmtt.calendar.events.entities.Event;
import org.pkwmtt.calendar.events.entities.EventType;
import org.pkwmtt.calendar.events.repositories.EventsRepository;
import org.pkwmtt.calendar.events.repositories.EventTypeRepository;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventsServiceTest {
    
    @Mock
    private EventsRepository eventsRepository;
    
    @Mock
    private EventTypeRepository eventTypeRepository;
    
    @InjectMocks
    private EventsService eventsService;
    
    @Test
    void getAllEventsReturnsMappedDTOs () {
        // given
        Date start = new Date(System.currentTimeMillis() + 1_000_000);
        Date end = new Date(System.currentTimeMillis() + 2_000_000);
        EventType type = new EventType(1, "Meeting");
        SuperiorGroup g1 = SuperiorGroup.builder().name("12K").build();
        Event e1 = new Event(11, "t1", "d1", start, end, type, List.of(g1));
        Event e2 = new Event(12, "t2", "d2", start, end, type, List.of(g1));
        
        when(eventsRepository.findAll()).thenReturn(List.of(e1, e2));
        
        // when
        var dtos = eventsService.getAllEvents();
        
        // then
        assertNotNull(dtos);
        assertEquals(2, dtos.size());
        assertEquals("t1", dtos.getFirst().getTitle());
        assertEquals("Meeting", dtos.getFirst().getType());
        assertEquals(List.of("12K"), dtos.getFirst().getSuperiorGroups());
    }
    
    @Test
    void getEventsForSuperiorGroupIsCaseInsensitiveAndFiltersCorrectly () {
        // given
        Date start = new Date(System.currentTimeMillis() + 1_000_000);
        Date end = new Date(System.currentTimeMillis() + 2_000_000);
        EventType type = new EventType(1, "TypeA");
        SuperiorGroup g1 = SuperiorGroup.builder().name("12K").build();
        SuperiorGroup g2 = SuperiorGroup.builder().name("34B").build();
        Event match = new Event(21, "match", "d", start, end, type, List.of(g1));
        Event other = new Event(22, "other", "d", start, end, type, List.of(g2));
        
        when(eventsRepository.findAll()).thenReturn(List.of(match, other));
        
        // when
        var result = eventsService.getEventsForSuperiorGroup("12k"); // lower-case on purpose
        
        // then
        assertEquals(1, result.size());
        assertEquals("match", result.getFirst().getTitle());
        assertEquals(List.of("12K"), result.getFirst().getSuperiorGroups());
    }
    
    @Test
    void getEventsForSuperiorGroupReturnsEmptyWhenNoMatches () {
        // given
        when(eventsRepository.findAll()).thenReturn(List.of());
        
        // when
        var result = eventsService.getEventsForSuperiorGroup("none");
        
        // then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
    
    @Test
    void addEventSavesMappedEntityAndReturnsGeneratedId () {
        // given
        Date start = new Date(System.currentTimeMillis() + 1_000_000);
        Date end = new Date(System.currentTimeMillis() + 2_000_000);
        EventDTO dto = new EventDTO()
          .setTitle("title")
          .setDescription("desc")
          .setStartDate(start)
          .setEndDate(end)
          .setType("Meeting")
          .setSuperiorGroups(List.of("12K"));
        
        // mock event type lookup used by the service
        EventType meetingType = new EventType(1, "Meeting");
        when(eventTypeRepository.findByName("Meeting")).thenReturn(java.util.Optional.of(meetingType));
        
        // mock save to set id on the passed event instance (service returns event.getId())
        when(eventsRepository.save(any(Event.class))).thenAnswer(invocation -> {
            Event e = invocation.getArgument(0);
            Field idField = Event.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.setInt(e, 1);
            return e;
        });
        
        // when
        int generatedId = eventsService.addEvent(dto);
        
        // then
        assertEquals(1, generatedId);
        
        ArgumentCaptor<Event> captor = ArgumentCaptor.forClass(Event.class);
        verify(eventsRepository, times(1)).save(captor.capture());
        Event saved = captor.getValue();
        assertEquals("title", saved.getTitle());
        assertEquals("desc", saved.getDescription());
        assertEquals(start, saved.getStartDate());
        assertEquals(end, saved.getEndDate());
    }
    
    @Test
    void getAllEventTypesReturnsNamesList () {
        // given
        EventType a = new EventType(1, "Meeting");
        EventType b = new EventType(2, "Exam");
        when(eventTypeRepository.findAll()).thenReturn(List.of(a, b));
        
        // when
        var types = eventsService.getAllEventTypes();
        
        // then
        assertEquals(2, types.size());
        assertEquals(List.of("Meeting", "Exam"), types);
    }
}