package org.pkwmtt.timetable.dto;

import lombok.*;

import java.util.List;


/**
 * Data Transfer Object (DTO) representing a timetable.
 * This class contains the name of the timetable and a list of days of the week,
 * each represented by a DayOfWeekDTO.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TimetableDTO {
    /**
     * The name of the timetable.
     */
    private String name;
    /**
     * List of days of the week in the timetable.
     */
    private List<DayOfWeekDTO> data;
    
    /**
     * Constructs a TimetableDTO with the specified name.
     *
     * @param name the name of the timetable
     */
    public TimetableDTO (String name) {
        this.name = name;
    }
    
    
}
