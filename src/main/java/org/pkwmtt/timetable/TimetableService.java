package org.pkwmtt.timetable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.InternalException;
import org.pkwmtt.exceptions.SpecifiedGeneralGroupDoesntExistsException;
import org.pkwmtt.exceptions.SpecifiedSubGroupDoesntExistsException;
import org.pkwmtt.exceptions.WebPageContentNotAvailableException;
import org.pkwmtt.timetable.dto.CustomSubjectFilterDTO;
import org.pkwmtt.timetable.dto.DayOfWeekDTO;
import org.pkwmtt.timetable.dto.SubjectDTO;
import org.pkwmtt.timetable.dto.TimetableDTO;
import org.pkwmtt.timetable.enums.TypeOfWeek;
import org.pkwmtt.timetable.objects.CustomSubjectDetails;
import org.pkwmtt.timetable.parser.TimetableParserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Service responsible for timetable operations:
 * - retrieving and caching group schedules via {@link TimetableCacheService}
 * - parsing subgroup identifiers from schedule content
 * - applying filters to schedules (by subgroup and custom subject filters)
 * <p>
 * This service delegates parsing-specific logic to {@link TimetableParserService}
 * and uses {@link TimetableCacheService} to fetch cached schedule data.
 */
@Slf4j
@Service
public class TimetableService {
    /**
     * Cache-backed service providing group schedules and general group listings.
     */
    private final TimetableCacheService cachedService;
    
    /**
     * Construct a TimetableService with a {@link TimetableCacheService} dependency.
     *
     * @param cachedService service used to retrieve cached timetable data
     */
    @Autowired
    TimetableService (TimetableCacheService cachedService) {
        this.cachedService = cachedService;
    }
    
    /**
     * Parses the timetable JSON to extract subgroup identifiers like K01, P03, GL04 using regex.
     *
     * @param generalGroupName group to analyze
     * @return sorted list of subgroup names found in the timetable
     * @throws JsonProcessingException if timetable conversion to JSON fails
     */
    public List<String> getAvailableSubGroups (String generalGroupName)
      throws JsonProcessingException, SpecifiedGeneralGroupDoesntExistsException, WebPageContentNotAvailableException {
        
        generalGroupName = generalGroupName.toUpperCase();
        TimetableDTO timetable = cachedService.getGeneralGroupSchedule(generalGroupName);
        
        ObjectMapper mapper = new ObjectMapper();
        String timeTableAsJson = mapper.writeValueAsString(timetable);
        
        // Regex pattern for group codes like K01, GP03, L04, etc.
        String regex = "\\bG?[KPL]0[0-9]\\b";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(timeTableAsJson);
        
        Set<String> matchedGroups = new HashSet<>();
        
        //Check if text starts with 'G' and delete it
        // to match frontend requirements
        String text;
        while (matcher.find()) {
            text = matcher.group();
            if (text.startsWith("G")) {
                text = text.substring(1);
            }
            matchedGroups.add(text);
        }
        
        return matchedGroups.stream().sorted().toList();
    }
    
    /**
     * Search the timetable of a general group for subgroup tokens related to a specific subject name.
     * Tokens include group codes and single-letter types (W, Ć, S) using a unicode-aware regex.
     *
     * @param generalGroupName uppercase or lowercase allowed; will be normalized
     * @param subjectName      name (or fragment) of the subject to search for
     * @return unique list of subgroup tokens associated with the subject
     */
    public List<String> getAvailableSubGroupsForSubject (String generalGroupName, String subjectName)
      throws JsonProcessingException {
        
        generalGroupName = generalGroupName.toUpperCase();
        List<String> result = new ArrayList<>();
        
        TimetableDTO timetable = cachedService.getGeneralGroupSchedule(generalGroupName);
        String regex = "(?<!\\S)(?:G?[KPL]0\\d|[WĆS])(?!\\S)";
        Pattern pattern = Pattern.compile(regex, Pattern.UNICODE_CHARACTER_CLASS);
        
        timetable.getData().forEach(day -> {
            day
              .getEven()
              .forEach(subjectDTO -> addMatchingSubjectGroups(subjectDTO, subjectName, pattern, result));
            day
              .getOdd()
              .forEach(subjectDTO -> addMatchingSubjectGroups(subjectDTO, subjectName, pattern, result));
        });
        return new HashSet<>(result.stream().toList()).stream().toList();
        
    }
    
    /**
     * Checks the given subject name against the provided pattern and appends found subgroup tokens
     * to the result list. Removes leading 'G' from tokens to match frontend format.
     *
     * @param subjectDTO  subject entry to inspect
     * @param subjectName subject name fragment to match against
     * @param pattern     compiled regex pattern for subgroup tokens
     * @param result      mutable list where matched tokens are appended
     */
    private void addMatchingSubjectGroups (SubjectDTO subjectDTO,
                                           String subjectName,
                                           Pattern pattern,
                                           List<String> result) {
        if (subjectDTO.getName().contains(subjectName)) {
            Matcher matcher = pattern.matcher(subjectDTO.getName());
            while (matcher.find()) {
                String text = matcher.group();
                if (text.startsWith("G")) {
                    text = text.substring(1);
                }
                result.add(text);
            }
        }
    }
    
    /**
     * Retrieves timetable and filters entries based on subgroups parameters and custom subject filters.
     *
     * @param generalGroupName     name of the general group
     * @param subgroup             subgroups list
     * @param customSubjectFilters list of cross-group subject filters to include instead of default entries
     * @return filtered timetable DTO for the requested general group
     * @throws WebPageContentNotAvailableException if source data can't be retrieved
     */
    public TimetableDTO getFilteredGeneralGroupSchedule (String generalGroupName,
                                                         List<String> subgroup,
                                                         List<CustomSubjectFilterDTO> customSubjectFilters)
      throws WebPageContentNotAvailableException, SpecifiedGeneralGroupDoesntExistsException, JsonProcessingException {
        //Uppercase name to assure match
        generalGroupName = generalGroupName.toUpperCase();
        
        //Check if specified subgroup is available for general group or else throw
        checkSubGroupAvailability(generalGroupName, subgroup);
        
        //Get user's schedule
        List<DayOfWeekDTO> schedule = cachedService.getGeneralGroupSchedule(generalGroupName).getData();
        
        //Go through schedule and extract customSubject details
        List<CustomSubjectDetails> customSubjectsDetails = createListOfCustomSchedulesDetails(
          generalGroupName, customSubjectFilters, schedule);
        
        return filterSchedule(schedule, subgroup, generalGroupName, customSubjectsDetails);
    }
    
    /**
     * Build a flattened list of {@link CustomSubjectDetails} for all provided custom filters.
     * Each entry contains the subject instance, subgroup token, day index and week type.
     *
     * @param generalGroupName     name of the main group (used to decide whether to reuse the provided schedule)
     * @param customSubjectFilters filters describing subjects to pull from possibly other groups
     * @param schedule             schedule of the primary general group (reused when applicable)
     * @return list of custom subject details matching the provided filters
     */
    private List<CustomSubjectDetails> createListOfCustomSchedulesDetails (String generalGroupName,
                                                                           List<CustomSubjectFilterDTO> customSubjectFilters,
                                                                           List<DayOfWeekDTO> schedule) {
        List<CustomSubjectDetails> customSubjectsDetails = new ArrayList<>();
        customSubjectFilters.forEach(customFilter -> {
            
            List<DayOfWeekDTO> customSubjectSchedule = schedule;
            if (!customFilter.generalGroup().equals(generalGroupName)) {
                try {
                    customSubjectSchedule = cachedService
                      .getGeneralGroupSchedule(customFilter.generalGroup())
                      .getData();
                } catch (JsonProcessingException e) {
                    throw new InternalException(e);
                }
            }
            
            for (int i = 0; i < customSubjectSchedule.size(); i++) {
                customSubjectsDetails.addAll(
                  searchDayOfWeekAndAddCustomSubjectsDetails(
                    customSubjectSchedule.get(i).getEven(), customFilter, i, TypeOfWeek.EVEN));
                
                customSubjectsDetails.addAll(
                  searchDayOfWeekAndAddCustomSubjectsDetails(
                    customSubjectSchedule.get(i).getOdd(), customFilter, i, TypeOfWeek.ODD));
            }
        });
        return customSubjectsDetails;
    }
    
    /**
     * Search a day (even/odd) for subjects matching the custom filter and convert matches into
     * {@link CustomSubjectDetails}.
     * <p>
     * The matching behavior depends on the parsed subject type:
     * - For EXERCISES, LECTURE, SEMINAR: match by name and by parsed type
     * - Default: match by name and subgroup token (e.g. K01)
     *
     * @param day          list of subjects for the specific day and parity
     * @param customFilter filter describing the desired subject and subgroup
     * @param dayIndex     index of the day in the week (0-based)
     * @param typeOfWeek   parity of the week (EVEN / ODD)
     * @return list of matched custom subject details for that day segment
     */
    private List<CustomSubjectDetails> searchDayOfWeekAndAddCustomSubjectsDetails (List<SubjectDTO> day,
                                                                                   CustomSubjectFilterDTO customFilter,
                                                                                   int dayIndex,
                                                                                   TypeOfWeek typeOfWeek) {
        List<SubjectDTO> matches = switch (TimetableParserService.extractSubjectTypeFromName(
          
          customFilter.subGroup())) {
            case EXERCISES, LECTURE, SEMINAR -> day
              .stream()
              .filter(item -> (item.getName().contains(customFilter.name()) && TimetableParserService
                .extractSubjectTypeFromName(item.getName())
                .equals(TimetableParserService.extractSubjectTypeFromName(customFilter.subGroup()))))
              .toList();
            
            default -> day
              .stream()
              .filter(item -> (item.getName().contains(customFilter.name()) && item
                .getName()
                .contains(customFilter.subGroup())))
              .toList();
        };
        
        if (!matches.isEmpty()) {
            return matches
              .stream()
              .map((item) -> new CustomSubjectDetails(item, customFilter.subGroup(), dayIndex, typeOfWeek))
              .toList();
        }
        return new ArrayList<>();
    }
    
    /**
     * Apply subgroup and custom subject filters to a week's schedule and return a new {@link TimetableDTO}.
     * <p>
     * Steps:
     * - For each day remove entries that collide with custom filters
     * - Filter remaining entries by requested subgroup tokens (including derived W/Ć/S tokens)
     * - Strip subject type markers from names before returning
     *
     * @param schedule              mutable list representing days of week to filter
     * @param subgroups             requested subgroup tokens to keep
     * @param generalGroupName      name of the group to populate result DTO
     * @param customSubjectsDetails list of custom subject replacements to apply
     * @return new TimetableDTO containing the filtered schedule
     */
    private TimetableDTO filterSchedule (List<DayOfWeekDTO> schedule,
                                         List<String> subgroups,
                                         String generalGroupName,
                                         List<CustomSubjectDetails> customSubjectsDetails) {
        
        for (int i = 0; i < schedule.size(); i++) {
            var day = schedule.get(i);
            deleteSubjectsCollidingWithCustomFilters(customSubjectsDetails, day);
            
            filterDayBySubgroupsWithSeminarsExercisesAndLectures(subgroups, customSubjectsDetails, day, i);
        }
        
        schedule.forEach(DayOfWeekDTO::deleteSubjectTypesFromNames);
        
        return new TimetableDTO(generalGroupName, schedule);
    }
    
    /**
     * Filters a single day by provided subgroup tokens while respecting custom subject details.
     * <p>
     * If no custom subjects are present the day is filtered directly by each subgroup.
     * Otherwise a per-subgroup filtered view is produced considering only custom subjects that
     * match the day and subgroup token.
     *
     * @param subgroups             list of subgroup tokens (e.g. K01, P02, W, Ć, S)
     * @param customSubjectsDetails precomputed custom subject details to consider during filtering
     * @param day                   day-of-week DTO to mutate
     * @param dayIndex              index of the day within the week (0-based)
     */
    private void filterDayByUsersSubgroups (List<String> subgroups,
                                            List<CustomSubjectDetails> customSubjectsDetails,
                                            DayOfWeekDTO day,
                                            int dayIndex) {
        subgroups.forEach(subgroup -> {
            if (customSubjectsDetails.isEmpty()) {
                day.filterByGroup(subgroup);
                return;
            }
            
            var customSubjectsByDay = customSubjectsDetails.stream()
              //Compare day of week and subgroup
              .filter(subject -> subject.getSubGroup().charAt(0) == subgroup.charAt(0)) // match subgroup
              .filter(subject -> subject.getDayOfWeekNumber() == dayIndex) // match day of week
              .toList();
            
            
            day.filterByGroup(subgroup, customSubjectsByDay);
        });
    }
    
    /**
     * Extends provided subgroup list with single-letter subject-type tokens derived from custom subjects
     * (W for lecture, Ć for exercises, S for seminar) then delegates to {@link #filterDayByUsersSubgroups(List, List, DayOfWeekDTO, int)}.
     *
     * @param subgroups             base subgroup tokens requested by the user
     * @param customSubjectsDetails list of custom subject details used to derive W/Ć/S tokens
     * @param day                   day DTO to filter
     * @param dayIndex              index of the day
     */
    private void filterDayBySubgroupsWithSeminarsExercisesAndLectures (List<String> subgroups,
                                                                       List<CustomSubjectDetails> customSubjectsDetails,
                                                                       DayOfWeekDTO day,
                                                                       int dayIndex) {
        Set<String> SCWgroups = new HashSet<>(customSubjectsDetails
                                                .stream()
                                                .map(CustomSubjectDetails::getSubGroup)
                                                .map(
                                                  item -> switch (TimetableParserService.extractSubjectTypeFromName(
                                                    item)) {
                                                      case SEMINAR -> "S";
                                                      case EXERCISES -> "Ć";
                                                      case LECTURE -> "W";
                                                      default -> null;
                                                  })
                                                .filter(Objects::nonNull)
                                                .toList());
        
        List<String> effectiveSubgroups = new ArrayList<>(subgroups);
        effectiveSubgroups.addAll(SCWgroups);
        
        filterDayByUsersSubgroups(effectiveSubgroups, customSubjectsDetails, day, dayIndex);
    }
    
    
    /**
     * Remove subjects from the provided day that conflict with any custom subject detail.
     * <p>
     * A subject is considered colliding when:
     * - the base name (after deleting type markers) matches the custom subject's name AND
     * - both have the same parsed subject type (lecture/exercises/seminar)
     *
     * @param customSubjectsDetails list of custom subjects to consider (these are used to remove original entries)
     * @param day                   day DTO to mutate by removing colliding subjects
     */
    private void deleteSubjectsCollidingWithCustomFilters (List<CustomSubjectDetails> customSubjectsDetails,
                                                           DayOfWeekDTO day) {
        for (CustomSubjectDetails customSubjectDetail : customSubjectsDetails) {
            customSubjectDetail.getSubject().deleteTypeAndUnnecessaryCharactersFromName();
            day.setEven(day
                          .getEven()
                          .stream()
                          .filter(subject -> !(subject
                            .getName()
                            .contains(customSubjectDetail.getSubject().getName()) && subjectsAreSameType(
                            subject, customSubjectDetail)))
                          .toList());
            
            day.setOdd(day
                         .getOdd()
                         .stream()
                         .filter(subject -> !(subject
                           .getName()
                           .contains(customSubjectDetail.getSubject().getName()) && subjectsAreSameType(
                           subject, customSubjectDetail)))
                         .toList());
            
        }
    }
    
    /**
     * Compare parsed subject types for two sources: an existing subject and a custom subject detail.
     *
     * @param subject              subject from the day schedule
     * @param customSubjectDetails custom subject descriptor containing subgroup token for type extraction
     * @return true when both parsed types are equal
     */
    private boolean subjectsAreSameType (SubjectDTO subject, CustomSubjectDetails customSubjectDetails) {
        var subjectType = TimetableParserService.extractSubjectTypeFromName(subject.getName());
        var customSubjectType = TimetableParserService.extractSubjectTypeFromName(
          customSubjectDetails.getSubGroup());
        return subjectType.equals(customSubjectType);
        
    }
    
    /**
     * Validate that all requested subgroup tokens exist for the given general group.
     *
     * @param generalGroupName name of a general group
     * @param subgroup         list of subgroup tokens to validate
     * @throws JsonProcessingException                when available subgroup extraction fails
     * @throws SpecifiedSubGroupDoesntExistsException when any requested subgroup is not present
     */
    private void checkSubGroupAvailability (String generalGroupName, List<String> subgroup)
      throws JsonProcessingException {
        //Check if specified subgroup is available for this generalGroup
        var subgroups = getAvailableSubGroups(generalGroupName);
        for (var group : subgroup) {
            if (!subgroups.contains(group)) {
                throw new SpecifiedSubGroupDoesntExistsException(group);
            }
        }
    }
    
    /**
     * Return an alphabetically sorted list of all known general groups.
     *
     * @return sorted list of general group names
     * @throws WebPageContentNotAvailableException when the underlying cache cannot provide the map
     */
    public List<String> getGeneralGroupList ()
      throws WebPageContentNotAvailableException, JsonProcessingException {
        return cachedService.getGeneralGroupsMap().keySet().stream().sorted().collect(Collectors.toList());
    }
    
    /**
     * Collect list of distinct subject names found in the schedule for a given general group.
     * Subject names are normalized by deleting type markers and unnecessary characters.
     *
     * @param generalGroupName group whose schedule will be scanned
     * @return unique list of normalized subject names
     */
    public List<String> getListOfSubjects (String generalGroupName) throws JsonProcessingException {
        var subjectSet = new HashSet<String>();
        var schedule = cachedService.getGeneralGroupSchedule(generalGroupName);
        
        schedule.getData().forEach(day -> {
            day.getEven().forEach(subject -> addToSet(subjectSet, subject));
            day.getOdd().forEach(subject -> addToSet(subjectSet, subject));
        });
        
        return subjectSet.stream().toList();
    }
    
    /**
     * Return a filtered list of custom subject names for the specified general group.
     *
     * <p>This method collects normalized subject names from the group's schedule and
     * returns all except those that match a small set of predefined cross-group/custom subjects
     * (currently: "niemiecki", "J ang", "WF hala"). The exclusion is case-sensitive
     * and relies on the normalization performed by {@link #getListOfSubjects(String)}.
     *
     * @param generalGroupName group whose schedule will be scanned
     * @return list of matching custom subject names
     * @throws JsonProcessingException when timetable parsing or retrieval fails
     */
    public List<String> getListOfCustomSubjects (String generalGroupName) throws JsonProcessingException {
        return getListOfSubjects(generalGroupName).stream().filter(
          subject -> !(
            subject.contains("niemiecki") ||
              subject.contains("J ang") ||
              subject.contains("WF hala")
          )
        ).toList();
    }
    
    /**
     * Normalize a subject by removing type markers and add its name to the provided set.
     *
     * @param subjectSet destination set collecting subject names
     * @param subject    subject instance to normalize and add
     */
    private void addToSet (Set<String> subjectSet, SubjectDTO subject) {
        subject.deleteTypeAndUnnecessaryCharactersFromName();
        subjectSet.add(subject.getName());
    }
    
}