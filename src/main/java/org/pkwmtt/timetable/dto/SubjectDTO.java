package org.pkwmtt.timetable.dto;

import lombok.*;
import lombok.experimental.Accessors;
import org.pkwmtt.calendar.exams.enums.SubjectType;

import java.util.regex.Pattern;

/**
 * Data transfer object representing a subject in the timetable.
 * <p>
 * Contains basic display and import-related fields and utility methods for
 * cleaning up subject names imported from external sources.
 */
@Data
@Accessors(chain = true)
public class SubjectDTO {
    /**
     * Subject name (may contain type suffixes or extraneous characters).
     */
    private String name;
    /**
     * Classroom identifier where the subject is held.
     */
    private String classroom;
    /**
     * Row id from the source (e.g., spreadsheet or CSV) used for tracking.
     */
    private int rowId;
    /**
     * Type of the subject.
     */
    private SubjectType type;
    /**
     * Flag indicating whether the subject is a custom entry (not from the standard set).
     */
    private Boolean custom = false;
    
    /**
     * Cleans the {@code name} field by:
     * - Trimming to the first token (text before the first space).
     * - Replacing underscores with spaces.
     * - Removing opening and closing parentheses.
     * <p>
     * Examples:
     * - "Math (Lecture)" -> "Math"
     * - "Computer_Science (Lab)" -> "Computer Science"
     * <p>
     * This method mutates the {@code name} field. It does not perform a null
     * check on {@code name}; callers should ensure {@code name} is not null
     * before invoking this method.
     */
    public void deleteTypeAndUnnecessaryCharactersFromName () {
        if (name.contains(" ")) {
            this.name = name.substring(0, name.indexOf(' '));
        }
        
        name = name.replaceAll("_", " ").replaceAll(Pattern.quote("("), "").replaceAll(Pattern.quote(")"), "");
    }
}
