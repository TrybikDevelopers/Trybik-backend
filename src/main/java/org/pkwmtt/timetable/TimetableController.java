package org.pkwmtt.timetable;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.pkwmtt.exceptions.SpecifiedGeneralGroupDoesntExistsException;
import org.pkwmtt.exceptions.SpecifiedSubGroupDoesntExistsException;
import org.pkwmtt.exceptions.WebPageContentNotAvailableException;
import org.pkwmtt.timetable.dto.CustomSubjectFilterDTO;
import org.pkwmtt.timetable.dto.TimetableDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

/**
 * REST controller responsible for timetable-related endpoints.
 *
 * <p>Base request mapping is configured via the {@code apiPrefix} property:
 * <code>@RequestMapping("${apiPrefix}/timetables")</code>.</p>
 *
 * <p>This controller delegates heavy-lifting to two services:
 * - {@link TimetableService} for real-time or filtered timetable generation,
 * - {@link TimetableCacheService} for cached timetable and auxiliary data.</p>
 */
@RestController
@RequestMapping("${apiPrefix}/timetables")
@RequiredArgsConstructor
public class TimetableController {
    /**
     * Primary service used to fetch and filter timetables from the source.
     * Use this when filtering by subgroups or applying custom subject filters.
     */
    private final TimetableService service;
    
    /**
     * Cache-backed service used to return already prepared timetables and other
     * inexpensive reads such as the list of timetable hours.
     *
     * <p>Prefer this service when no additional filtering is requested to reduce
     * network or parsing overhead.</p>
     */
    private final TimetableCacheService cachedService;
    
    /**
     * Provides the schedule for a specified general group, optionally filtered by subgroups.
     *
     * @param generalGroupName name of the general group
     * @param subgroups        optional list of subgroups to filter the schedule (request parameter name: "sub")
     * @return timetable for the specified general group wrapped in {@link ResponseEntity}
     * @throws WebPageContentNotAvailableException        if the timetable page can't be fetched or parsed
     * @throws SpecifiedGeneralGroupDoesntExistsException if the specified general group doesn't exist
     * @throws SpecifiedSubGroupDoesntExistsException     if any of the specified subgroups don't exist
     * @throws JsonProcessingException                    if there is an error processing JSON data
     */
    @GetMapping("/{generalGroupName}")
    public ResponseEntity<TimetableDTO> getGeneralGroupSchedule (@PathVariable String generalGroupName,
                                                                 @RequestParam(required = false, name = "sub") List<String> subgroups)
      throws WebPageContentNotAvailableException, SpecifiedGeneralGroupDoesntExistsException, SpecifiedSubGroupDoesntExistsException, JsonProcessingException {
        var areSubgroupsProvided = !(isNull(subgroups) || subgroups.isEmpty());
        
        return
          areSubgroupsProvided ?
            ResponseEntity.ok(service.getFilteredGeneralGroupSchedule(
              generalGroupName,
              subgroups,
              new ArrayList<>()
            ))
            : ResponseEntity.ok(cachedService.getGeneralGroupSchedule(generalGroupName));
    }
    
    /**
     * Provides the schedule for a specified general group, optionally filtered by subgroups and custom subjects.
     *
     * <p>If subgroups are provided, the request is forwarded to {@link TimetableService#getFilteredGeneralGroupSchedule}
     * with the provided subgroups and any provided {@code customSubjects}. If {@code customSubjects} is omitted or empty
     * but subgroups are present, an empty list is passed to the service to indicate "no custom subject filters".</p>
     *
     * @param generalGroupName name of the general group
     * @param subgroups        optional list of subgroups to filter the schedule (request parameter name: "sub")
     * @param customSubjects   optional list of custom subjects to include in the schedule (request body, may be null)
     * @return timetable for the specified general group wrapped in {@link ResponseEntity}
     * @throws WebPageContentNotAvailableException        if the timetable page can't be fetched or parsed
     * @throws SpecifiedGeneralGroupDoesntExistsException if the specified general group doesn't exist
     * @throws SpecifiedSubGroupDoesntExistsException     if any of the specified subgroups don't exist
     * @throws JsonProcessingException                    if there is an error processing JSON data
     */
    @PostMapping(value = "/{generalGroupName}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<TimetableDTO> getGeneralGroupScheduleWithCustomSubjects (@PathVariable String generalGroupName,
                                                                                   @RequestParam(required = false, name = "sub") List<String> subgroups,
                                                                                   @RequestBody(required = false) List<CustomSubjectFilterDTO> customSubjects)
      throws WebPageContentNotAvailableException, SpecifiedGeneralGroupDoesntExistsException, SpecifiedSubGroupDoesntExistsException, JsonProcessingException {
        boolean hasSubgroups = subgroups != null && !subgroups.isEmpty();
        
        if (!hasSubgroups) {
            return ResponseEntity.ok(cachedService.getGeneralGroupSchedule(generalGroupName));
        }
        
        if (customSubjects == null || customSubjects.isEmpty()) {
            customSubjects = new ArrayList<>();
        }
        
        return ResponseEntity.ok(service.getFilteredGeneralGroupSchedule(
          generalGroupName,
          subgroups,
          customSubjects
        ));
    }
    
    /**
     * Returns the canonical list of timetable hour strings (e.g. "08:00-09:30").
     *
     * <p>Data is returned from the cache-backed service to avoid repeated page fetches.</p>
     *
     * @return list of timetable hours wrapped in {@link ResponseEntity}
     * @throws WebPageContentNotAvailableException if the underlying source is not available
     */
    @GetMapping("/hours")
    public ResponseEntity<List<String>> getListOfHours () throws WebPageContentNotAvailableException {
        return ResponseEntity.ok(cachedService.getListOfHours());
    }
    
    /**
     * Returns the list of known general groups (top-level groups).
     *
     * <p>This endpoint may trigger parsing of the timetable index if cache is not available.</p>
     *
     * @return list of general group names wrapped in {@link ResponseEntity}
     * @throws WebPageContentNotAvailableException if the underlying source is not available
     */
    @GetMapping("/groups/general")
    public ResponseEntity<List<String>> getListOfGeneralGroups ()
      throws WebPageContentNotAvailableException, JsonProcessingException {
        return ResponseEntity.ok(service.getGeneralGroupList());
    }
    
    /**
     * Provides the list of available subgroups for the specified general group.
     *
     * @param generalGroupName name of general group
     * @return list of available subgroup names wrapped in {@link ResponseEntity}
     * @throws JsonProcessingException                    if there is an error processing JSON data
     * @throws SpecifiedGeneralGroupDoesntExistsException if the specified general group doesn't exist
     * @throws WebPageContentNotAvailableException        if the timetable page can't be fetched
     */
    @GetMapping("/groups/{generalGroupName}")
    public ResponseEntity<List<String>> getListOfAvailableGroups (@PathVariable String generalGroupName)
      throws JsonProcessingException, SpecifiedGeneralGroupDoesntExistsException, WebPageContentNotAvailableException {
        return ResponseEntity.ok(service.getAvailableSubGroups(generalGroupName));
    }
    
    
    /**
     * Returns available subgroups for a specific subject within a general group.
     *
     * <p>Useful when a subject is taught only in a subset of subgroups and callers need to
     * discover which subgroups contain that subject.</p>
     *
     * @param generalGroupName general group to search in
     * @param subjectName      subject name for which available subgroups should be returned
     * @return list of subgroup names that contain the subject wrapped in {@link ResponseEntity}
     * @throws SpecifiedGeneralGroupDoesntExistsException if the specified general group doesn't exist
     * @throws WebPageContentNotAvailableException        if the timetable page can't be fetched
     */
    @GetMapping("/groups/{generalGroupName}/{subjectName}")
    public ResponseEntity<List<String>> getListOfAvailableGroupsForSubjectName (@PathVariable String generalGroupName,
                                                                                @PathVariable String subjectName)
      throws SpecifiedGeneralGroupDoesntExistsException, WebPageContentNotAvailableException, JsonProcessingException {
        return ResponseEntity.ok(service.getAvailableSubGroupsForSubject(generalGroupName, subjectName));
    }
    
    /**
     * Returns the list of subjects available for a given general group.
     *
     * <p>This is a convenience endpoint that does not perform network I/O if the service has cached data.</p>
     *
     * @param generalGroupName name of the general group
     * @return list of subject names wrapped in {@link ResponseEntity}
     */
    @GetMapping("/{generalGroupName}/list")
    public ResponseEntity<List<String>> getListOfSubjects (@PathVariable String generalGroupName)
      throws JsonProcessingException {
        return ResponseEntity.ok(service.getListOfSubjects(generalGroupName));
    }
    
    @GetMapping("/{generalGroupName}/list/custom")
    public ResponseEntity<List<String>> getListOfCustomSubjects (@PathVariable String generalGroupName)
      throws JsonProcessingException {
        var response = service.getListOfCustomSubjects(generalGroupName);
        return ResponseEntity.ok(response);
    }
    
}