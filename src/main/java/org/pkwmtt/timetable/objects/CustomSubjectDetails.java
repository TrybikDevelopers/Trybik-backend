package org.pkwmtt.timetable.objects;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.pkwmtt.timetable.dto.SubjectDTO;
import org.pkwmtt.timetable.enums.TypeOfWeek;

/**
 * Class representing custom details of a subject.
 * It includes the subject information, sub-group, day of the week number, and type of week.
 */
@Getter
@AllArgsConstructor
public class CustomSubjectDetails {
    /**
     * The subject information.
     */
    SubjectDTO subject;
    /**
     * The sub-group of the subject.
     */
    String subGroup;
    /**
     * The day of the week number (e.g., 1 for Monday, 2 for Tuesday).
     */
    int dayOfWeekNumber;
    /**
     * The type of week (ODD, EVEN, or BOTH).
     */
    TypeOfWeek typeOfWeek;
    
    /**
     * Returns a JSON string representation of the CustomSubjectDetails object.
     *
     * @return JSON string representation of the object
     */
    @Override
    public String toString () {
        JsonMapper mapper = new JsonMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
