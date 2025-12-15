package org.pkwmtt.timetable.dto;

/**
 * Data Transfer Object (DTO) representing a custom subject filter.
 * This class contains the name of the subject, its general group, and its sub-group.
 */
public record CustomSubjectFilterDTO(String name, String generalGroup, String subGroup) {
}
