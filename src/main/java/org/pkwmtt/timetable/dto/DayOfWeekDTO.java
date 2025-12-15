package org.pkwmtt.timetable.dto;

import lombok.Data;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.pkwmtt.timetable.enums.TypeOfWeek;
import org.pkwmtt.timetable.objects.CustomSubjectDetails;
import org.springframework.data.util.Pair;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Data Transfer Object (DTO) representing a day of the week with its associated subjects.
 * This class contains the name of the day and two lists of subjects:
 * one for odd weeks and another for even weeks.
 */
@Slf4j
@Data
public class DayOfWeekDTO {
    /** The name of the day of the week (e.g., "Monday", "Tuesday"). */
    private final String name;
    /** List of subjects scheduled for odd weeks. */
    @Setter
    private List<SubjectDTO> odd;
    /** List of subjects scheduled for even weeks. */
    @Setter
    private List<SubjectDTO> even;
    
    /**
     * Constructs a DayOfWeekDTO with the specified name.
     * Initializes the lists for odd and even week subjects as empty lists.
     *
     * @param name the name of the day of the week
     */
    public DayOfWeekDTO (String name) {
        this.name = name;
        odd = new ArrayList<>();
        even = new ArrayList<>();
    }
    
    /**
     * Adds a subject to the appropriate list (odd, even, or both) based on the specified type of week.
     *
     * @param subjectDTO the subject to be added, represented as a `SubjectDTO` object
     * @param typeOfWeek the type of week (EVEN, ODD, or BOTH) indicating where the subject should be added
     */
    public void add (SubjectDTO subjectDTO, TypeOfWeek typeOfWeek) {
        switch (typeOfWeek) {
            case EVEN -> this.even.add(subjectDTO); // Add to the even-week list
            case ODD -> this.odd.add(subjectDTO);  // Add to the odd-week list
            case BOTH -> {                         // Add to both odd- and even-week lists
                this.even.add(subjectDTO);
                this.odd.add(subjectDTO);
            }
        }
    }
    
    /**
     * Removes unnecessary characters and type information from the names
     * of all subjects in both the odd- and even-week lists.
     * This operation is performed by invoking the `deleteTypeAndUnnecessaryCharactersFromName`
     * method on each `SubjectDTO` in the respective lists.
     */
    public void deleteSubjectTypesFromNames () {
        even.forEach(SubjectDTO::deleteTypeAndUnnecessaryCharactersFromName);
        odd.forEach(SubjectDTO::deleteTypeAndUnnecessaryCharactersFromName);
    }
    
    
    /**
     * Filters the subjects in both the odd- and even-week lists based on the specified group.
     * The filtering is performed by extracting the group character and target number
     * from the provided group string and applying the filter to each list.
     *
     * @param group the group identifier (e.g., "K03") used to filter the subjects
     */
    public void filterByGroup (String group) {
        var groupCharAndTargetNumber = getGroupCharAndTargetNumber(group);
        
        // Apply the filter to both odd- and even-week lists
        odd = filter(odd, groupCharAndTargetNumber.getFirst(), groupCharAndTargetNumber.getSecond());
        even = filter(even, groupCharAndTargetNumber.getFirst(), groupCharAndTargetNumber.getSecond());
    }
    
    /**
     * Filters the subjects in both the odd- and even-week lists based on the specified subgroup
     * and a list of custom subjects. The filtering is performed by extracting the group character
     * and target number from the provided subgroup string and applying the filter to each list.
     * Custom subjects are filtered by their type of week (ODD or EVEN) and included in the respective lists.
     *
     * @param subGroup       the subgroup identifier (e.g., "K03") used to filter the subjects
     * @param customSubjects a list of custom subjects to be included in the filtering process
     */
    public void filterByGroup (String subGroup, List<CustomSubjectDetails> customSubjects) {
        var groupCharAndTargetNumber = getGroupCharAndTargetNumber(subGroup);
        
        // Apply the filter to both odd- and even-week lists
        odd = filter(
          odd, groupCharAndTargetNumber.getFirst(), groupCharAndTargetNumber.getSecond(), customSubjects
            .stream()
            .filter(customSubject -> customSubject.getTypeOfWeek().equals(TypeOfWeek.ODD))
            .toList()
        );
        
        even = filter(
          even, groupCharAndTargetNumber.getFirst(), groupCharAndTargetNumber.getSecond(), customSubjects
            .stream()
            .filter(customSubject -> customSubject.getTypeOfWeek().equals(TypeOfWeek.EVEN))
            .toList()
        );
    }
    
    /**
     * Extracts the group character and target number from the provided group string.
     * If the group string starts with 'G' and its length is greater than 3, the first character is removed.
     * The group character (e.g., "K" from "K03") and the subgroup digit (e.g., "3" from "K03") are then extracted.
     *
     * @param group the group string (e.g., "K03" or "GK03") to process
     * @return a Pair containing the group character as the first element and the subgroup digit as the second element
     */
    private Pair<String, String> getGroupCharAndTargetNumber (String group) {
        // Delete first character if group starts 'G'
        if (group.charAt(0) == 'G' && group.length() > 3) {
            group = group.substring(1);
        }
        
        // Extract the group letter (e.g., "K" from "K03")
        var groupChar = String.valueOf(group.charAt(0));
        
        // Extract the subgroup digit (e.g., "3" from "K03")
        var targetNumber = String.valueOf(group.charAt(group.length() - 1));
        return Pair.of(groupChar, targetNumber);
    }
    
    /**
     * Returns a new list containing only those SubjectDTO items
     * whose type string matches exclusively the target group code or doesn't have group at all.
     *
     * @param list         the original list of subjects (odd or even week)
     * @param groupName    the group letter (e.g., "K")
     * @param targetNumber the subgroup digit to keep (e.g., "3")
     * @return a filtered list of SubjectDTO
     */
    private List<SubjectDTO> filter (List<SubjectDTO> list, String groupName, String targetNumber) {
        return list.stream().filter(item -> hasOnlyTargetGroup(item.getName(), groupName, targetNumber)).toList();
    }
    
    
    /**
     * Filters a list of `SubjectDTO` objects based on the specified group name, target number,
     * and a list of custom subjects. The method first filters the list to include only items
     * that match the target group and subgroup. Then, it adds custom subjects to the list,
     * marks them as custom, and sorts the final list by the row ID.
     *
     * @param list           the original list of `SubjectDTO` objects to be filtered
     * @param groupName      the group name (e.g., "K") used for filtering
     * @param targetNumber   the subgroup number (e.g., "4") used for filtering
     * @param customSubjects a list of `CustomSubjectDetails` to be added to the filtered list
     * @return a filtered and sorted list of `SubjectDTO` objects
     */
    private List<SubjectDTO> filter (List<SubjectDTO> list,
                                     String groupName,
                                     String targetNumber,
                                     List<CustomSubjectDetails> customSubjects) {
        list = list
          .stream()
          .filter(item -> hasOnlyTargetGroup(item.getName(), groupName, targetNumber))
          .collect(Collectors.toList());
        
        for (var customSubject : customSubjects) {
            list.add(customSubject.getSubject().setCustom(true));
        }
        
        list.sort(Comparator.comparingInt(SubjectDTO::getRowId));
        
        return list;
    }
    
    
    /**
     * Checks if the given element matches only the specified target group and subgroup number.
     * The method first verifies if the element does not belong to any group other than the target group.
     * If the element belongs to the target group, it further checks if the subgroup number matches the target number.
     *
     * @param element      the string to be checked, representing the group and subgroup information
     * @param groupName    the name of the target group (e.g., "K")
     * @param targetNumber the target subgroup number (e.g., "3")
     * @return true if the element matches only the target group and subgroup number, false otherwise
     */
    private boolean hasOnlyTargetGroup (String element, String groupName, String targetNumber) {
        var pattern = Pattern.compile(String.format("\\bG?[%s]0[1-9]\\b", Pattern.quote(groupName)));
        var matcher = pattern.matcher(element);
        if (!matcher.find()) {
            return true;
        }
        
        pattern = Pattern.compile(String.format("%s0%s", Pattern.quote(groupName), Pattern.quote(targetNumber)));
        matcher = pattern.matcher(element);
        return matcher.find();
    }
    
}