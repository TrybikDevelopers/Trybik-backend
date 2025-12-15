package org.pkwmtt.calendar.exams.mapper;

import org.pkwmtt.exceptions.InvalidGroupIdentifierException;

import java.util.Set;
import java.util.stream.Collectors;

public class GroupMapper {
    private GroupMapper() {}

    /**
     * extract superior group form general group e.g. 12K2 -> 12K
     * @param generalGroup group for transformation
     * @return superior group
     */
    public static String trimLastDigit(String generalGroup) {
        char lastChar = generalGroup.charAt(generalGroup.length() - 1);
        if (Character.isDigit(lastChar))
            generalGroup = generalGroup.substring(0, generalGroup.length() - 1);
        return generalGroup;
    }

    /**
     * extract common superior group form provided general groups e.g. 12K2 -> 12K
     * @param superiorGroups set of general groups from the same year of study
     * @return single superior group of provided general groups
     * @throws InvalidGroupIdentifierException when not all provided groups belong to the same year of study
     */
    public static String extractSuperiorGroup(Set<String> superiorGroups) throws InvalidGroupIdentifierException {
        if(superiorGroups == null || superiorGroups.isEmpty())
            throw new InvalidGroupIdentifierException("general group is missing");
        Set<String> trimmedGroups = superiorGroups.stream()
                .map(GroupMapper::trimLastDigit)
                .collect(Collectors.toSet());
        if(trimmedGroups.size() > 1)
            throw new InvalidGroupIdentifierException("ambiguous general groups for subgroups");
        return trimmedGroups.iterator().next();
    }
}