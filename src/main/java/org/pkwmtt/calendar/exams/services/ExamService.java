package org.pkwmtt.calendar.exams.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.InternalException;
import org.pkwmtt.calendar.exams.dto.RequestExamDto;
import org.pkwmtt.calendar.exams.entity.Exam;
import org.pkwmtt.calendar.exams.entity.ExamType;
import org.pkwmtt.calendar.exams.entity.StudentGroup;
import org.pkwmtt.calendar.exams.mapper.ExamDtoMapper;
import org.pkwmtt.calendar.exams.repository.ExamRepository;
import org.pkwmtt.calendar.exams.repository.ExamTypeRepository;
import org.pkwmtt.calendar.exams.repository.GroupRepository;
import org.pkwmtt.exceptions.*;
import org.pkwmtt.timetable.TimetableService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.pkwmtt.calendar.exams.mapper.GroupMapper.extractSuperiorGroup;
import static org.pkwmtt.calendar.exams.mapper.GroupMapper.trimLastDigit;

@Service
@RequiredArgsConstructor
@Transactional
public class ExamService {
    
    private final ExamRepository examRepository;
    private final ExamTypeRepository examTypeRepository;
    private final GroupRepository groupRepository;
    private final TimetableService timetableService;
    
    /**
     * @param requestExamDto details of exam
     * @return id of exam added to database
     */
    @PreAuthorize("@preAuthorizationService.verifyGroupPermissionsForNewResource(#requestExamDto.generalGroups)")
    public int addExam (RequestExamDto requestExamDto) {
        
        Set<StudentGroup> groups = verifyAndUpdateExamGroups(requestExamDto);
        
        ExamType examType = examTypeRepository.findByName(requestExamDto.getExamType())
          .orElseThrow(() -> new ExamTypeNotExistsException(requestExamDto.getExamType()));
        
        Exam exam = ExamDtoMapper.mapToNewExam(requestExamDto, groups, examType);
        Set<Exam> existingExam = examRepository.findAllByTitle(exam.getTitle());
        
        if (existingExam.contains(exam)) {
            throw new ResourceAlreadyExistsException("Exam already exists");
        }
        return examRepository.save(exam).getExamId();
    }
    
    /**
     * @param requestExamDto new details of exam that overwrite old ones
     * @param id             of exam that need to be modified
     */
    @PreAuthorize("@preAuthorizationService.verifyGroupPermissionsForModifiedResource(#requestExamDto.generalGroups, #id)")
    public void modifyExam (RequestExamDto requestExamDto, int id) {
        
        Set<StudentGroup> groups = verifyAndUpdateExamGroups(requestExamDto);
        
        ExamType examType = examTypeRepository.findByName(requestExamDto.getExamType())
          .orElseThrow(() -> new ExamTypeNotExistsException(requestExamDto.getExamType()));
        
        examRepository.save(ExamDtoMapper.mapToExistingExam(requestExamDto, groups, examType, id));
    }
    
    /**
     * @param id of exam
     */
    @PreAuthorize("@preAuthorizationService.verifyGroupPermissionsForExistingResource(#id)")
    public void deleteExam (int id) {
        examRepository.deleteById(id);
    }
    
    /**
     * @param id of exam
     * @return exam
     */
    public Exam getExamById (int id) {
        return examRepository.findById(id).orElseThrow(() -> new NoSuchElementWithProvidedIdException(id));
    }
    
    /**
     * @param generalGroups set of general groups from the same year of study
     *                      e.g. 12K1, 12K2 are from 12K year of study,
     *                      but 12A1 and 12B1 or 11A1 and 12A1 aren't from the same year
     * @param subgroups     subgroups that belong to provided general groups
     * @return set of exams containing provided groups
     */
    public Set<Exam> getExamByGroups (Set<String> generalGroups, Set<String> subgroups) {
        
        String superiorGroup = extractSuperiorGroup(generalGroups);
        verifyGeneralGroupsFormat(generalGroups);
        
        if (subgroups == null || subgroups.isEmpty()) {
            return examRepository.findAllByGroups_NameIn(generalGroups);
        }
        
        verifySubgroupsFormat(subgroups);
        return examRepository.findAllBySubgroupsOfSuperiorGroupAndGeneralGroup(
          superiorGroup, generalGroups, subgroups);
    }
    
    /**
     * @return list of examTypes
     */
    public List<ExamType> getExamTypes () {
        return examTypeRepository.findAll();
    }
    
    
    /**
     * verify if groups exists and updates database when it exists, but repository doesn't contain it.
     * When timetable service is unavailable verifies groups using groupsRepository
     *
     * @param requestExamDto containing groups for verification
     * @return single set of all kinds of provided groups as StudentGroup entities
     * that are in database and could be safely attach to Exam entity
     */
    private Set<StudentGroup> verifyAndUpdateExamGroups (RequestExamDto requestExamDto) {
        Set<String> generalGroups = requestExamDto.getGeneralGroups();
        Set<String> subgroups = requestExamDto.getSubgroups();
        
        if (generalGroups == null || generalGroups.isEmpty()) {
            throw new InvalidGroupIdentifierException("general group is missing");
        }
        
        verifyGeneralGroups(generalGroups);
        
        if (subgroups == null || subgroups.isEmpty()) {
            return saveNewStudentGroups(generalGroups);
        }
        
        String superiorGroup = extractSuperiorGroup(generalGroups);
        
        
        verifySubgroups(generalGroups, subgroups);
        
        subgroups.add(trimLastDigit(superiorGroup));
        return saveNewStudentGroups(subgroups);
    }
    
    /**
     * verifies provided generalGroups using timetable service or repository when service is unavailable
     *
     * @param generalGroups that would be verified
     */
    private void verifyGeneralGroups (Set<String> generalGroups) {
        try {
            Set<String> existingGeneralGroups = new HashSet<>(timetableService.getGeneralGroupList());
            if (!existingGeneralGroups.containsAll(generalGroups)) {
                throw new InvalidGroupIdentifierException(existingGeneralGroups, generalGroups);
            }
        } catch (WebPageContentNotAvailableException e) {
            verifyGeneralGroupsUsingRepository(generalGroups);
        } catch (JsonProcessingException e) {
            throw new InternalException(e);
        }
    }
    
    /**
     * @param groups that would be verified using repository
     * @throws ServiceNotAvailableException when verification not succeeded
     */
    private void verifyGeneralGroupsUsingRepository (Set<String> groups) throws ServiceNotAvailableException {
        verifyGeneralGroupsFormat(groups);
        Set<String> groupsFromRepository = groupRepository.findAllByNameIn(groups).stream()
          .map(StudentGroup::getName)
          .collect(Collectors.toSet()
          );
        if (!groupsFromRepository.containsAll(groups)) {
            throw new ServiceNotAvailableException(
              "Timetable service unavailable, couldn't verify groups using repository");
        }
    }
    
    
    private void verifySubgroups (Set<String> generalGroups, Set<String> subgroups) {
        try {
            Set<String> subGroupsFromTimetable = new HashSet<>();
            for (String generalGroup : generalGroups) {
                subGroupsFromTimetable.addAll(timetableService.getAvailableSubGroups(generalGroup));
            }
            if (!subGroupsFromTimetable.containsAll(subgroups)) {
                throw new InvalidGroupIdentifierException(subGroupsFromTimetable, subgroups);
            }
        } catch (JsonProcessingException |
                 SpecifiedGeneralGroupDoesntExistsException |
                 WebPageContentNotAvailableException e) {
            verifySubgroupsUsingRepository(extractSuperiorGroup(generalGroups), subgroups);
        }
    }
    
    /**
     * @param generalGroup of provided subgroups
     * @param groups       subgroups for verification
     * @throws ServiceNotAvailableException when verification not succeeded
     */
    private void verifySubgroupsUsingRepository (String generalGroup, Set<String> groups)
      throws ServiceNotAvailableException {
        groups.add(generalGroup);
        if (examRepository.findCommonExamIdsForGroups(groups, groups.size()).isEmpty()) {
            throw new ServiceNotAvailableException(
              "Timetable service unavailable, couldn't verify groups using repository");
        }
    }
    
    /**
     * saves groups to groupRepository, existing group names are filtered out before saving
     *
     * @param groups groups that would be saved to repository
     * @return set of StudentsGroup Entities with provided names
     */
    private Set<StudentGroup> saveNewStudentGroups (Set<String> groups) {
        //        remove duplicates before saving records
        Set<StudentGroup> existingGroups = groupRepository.findAllByNameIn(groups);
        groups.removeAll(existingGroups.stream()
                           .map(StudentGroup::getName)
                           .collect(Collectors.toSet())
        );
        List<StudentGroup> savedGroups = groupRepository.saveAll(groups.stream()
                                                                   .map(g -> StudentGroup.builder()
                                                                     .name(g)
                                                                     .build()
                                                                   ).collect(Collectors.toList())
        );
        existingGroups.addAll(savedGroups);
        return existingGroups;
    }
    
    /**
     * @param generalGroups general groups for verification
     * @throws SpecifiedGeneralGroupDoesntExistsException when format is invalid
     */
    private static void verifyGeneralGroupsFormat (Set<String> generalGroups)
      throws SpecifiedGeneralGroupDoesntExistsException {
        generalGroups.forEach(group -> {
            if (!group.matches("^\\d.*")) {
                throw new SpecifiedGeneralGroupDoesntExistsException(group);
            }
        });
    }
    
    /**
     * @param subgroups subgroups for verification
     * @throws SpecifiedSubGroupDoesntExistsException when format is invalid
     */
    private static void verifySubgroupsFormat (Set<String> subgroups)
      throws SpecifiedSubGroupDoesntExistsException {
        subgroups.forEach(group -> {
            if (!group.matches("^[A-Z].*")) {
                throw new SpecifiedSubGroupDoesntExistsException(group);
            }
        });
    }
}