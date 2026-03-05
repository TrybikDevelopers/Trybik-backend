package org.pkwmtt.calendar.exams;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pkwmtt.calendar.exams.dto.RequestExamDto;
import org.pkwmtt.calendar.exams.entity.Exam;
import org.pkwmtt.calendar.exams.entity.ExamType;
import org.pkwmtt.calendar.exams.entity.StudentGroup;
import org.pkwmtt.calendar.exams.mapper.ExamDtoMapper;
import org.pkwmtt.calendar.exams.repository.ExamRepository;
import org.pkwmtt.calendar.exams.repository.ExamTypeRepository;
import org.pkwmtt.calendar.exams.repository.GroupRepository;
import org.pkwmtt.calendar.exams.services.ExamService;
import org.pkwmtt.exceptions.*;
import org.pkwmtt.security.authentication.authenticationToken.JwtAuthenticationToken;
import org.pkwmtt.timetable.TimetableService;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class ExamServiceTest {

    @Mock
    private ExamRepository examRepository;

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private ExamTypeRepository examTypeRepository;

    @Mock
    private TimetableService timetableService;

    @InjectMocks
    private ExamService examService;

    @BeforeEach
    void setupSecurityContext() {
        JwtAuthenticationToken auth = new JwtAuthenticationToken(
                UUID.fromString("11111111-2222-3333-4444-555555555555"),
                Collections.emptyList(),
                "12K"
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    //<editor-fold desc="repository don't contain groups, service available">

    /**
     * test specification
     * generalGroup         - 1 item
     * subgroup             - blank
     * timetable service    - available
     * provided groups      - match groups from timetable service
     * groupRepository      - don't contain provided groups
     */
    @Test
    void testBlankSubgroupAndMoreArgumentsThatRequiredReturnedByService() throws JsonProcessingException {
        //        given
        Set<String> g12K2 = Set.of("12K2");

        LocalDateTime date = LocalDateTime.now().plusDays(1);
        RequestExamDto requestExamDto = RequestExamDto.builder()
                .title("title")
                .description("description")
                .date(date)
                .examType("exam")
                .generalGroups(new HashSet<>(g12K2))
                .build();
        ExamType examType = buildExampleExamType();
        List<StudentGroup> studentGroups = buildExampleStudentGroupList(g12K2);
        Exam exam = buildExamWithIdAndGroups(studentGroups);

        when(examTypeRepository.findByName(requestExamDto.getExamType())).thenReturn(Optional.of(examType));
        //        more groups than in set
        when(timetableService.getGeneralGroupList()).thenReturn(new ArrayList<>(List.of("12K1", "12K2", "12K3")));
        when(groupRepository.findAllByNameIn(g12K2)).thenReturn(new HashSet<>(Set.of()));
        when(groupRepository.saveAll(anyList())).thenReturn(studentGroups);
        when(examRepository.save(any(Exam.class))).thenReturn(exam);
        //        when
        int savedId = examService.addExam(requestExamDto);
        //        then
        verify(examTypeRepository, times(1)).findByName(requestExamDto.getExamType());
        verify(timetableService, times(1)).getGeneralGroupList();
        verify(groupRepository, times(1)).findAllByNameIn(g12K2);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<StudentGroup>> groupCaptor = ArgumentCaptor.forClass(List.class);
        verify(groupRepository, times(1)).saveAll(groupCaptor.capture());
        assertEquals("12K2", groupCaptor.getValue().getFirst().getName());

        ArgumentCaptor<Exam> examCaptor = ArgumentCaptor.forClass(Exam.class);
        verify(examRepository, times(1)).save(examCaptor.capture());
        Exam savedExam = examCaptor.getValue();
        assertExam(savedExam, date, savedId, g12K2);
    }

    /**
     * test specification
     * generalGroup         - 3 item
     * subgroup             - 0 items
     * timetable service    - available
     * provided groups      - match groups from timetable service
     * groupRepository      - don't contain provided groups
     */
    @Test
    void addExamForMultipleGeneralGroupsWithEmptySubgroups() throws JsonProcessingException {
        //        given
        Set<String> generalGroups = Set.of("12K1", "12K2", "12K3");
        Set<String> subgroups = Set.of();

        LocalDateTime date = LocalDateTime.now().plusDays(1);
        RequestExamDto requestExamDto = buildExampleExamDto(generalGroups, subgroups, date);
        ExamType examType = buildExampleExamType();
        List<StudentGroup> studentGroups = buildExampleStudentGroupList(generalGroups);
        Exam exam = buildExamWithIdAndGroups(studentGroups);

        when(examTypeRepository.findByName(requestExamDto.getExamType())).thenReturn(Optional.of(examType));
        when(timetableService.getGeneralGroupList()).thenReturn(new ArrayList<>(generalGroups));

        when(groupRepository.findAllByNameIn(generalGroups)).thenReturn(new HashSet<>(Set.of()));
        when(groupRepository.saveAll(anyList())).thenReturn(studentGroups);
        when(examRepository.save(any(Exam.class))).thenReturn(exam);
        //        when
        int savedId = examService.addExam(requestExamDto);
        //        then
        verify(examTypeRepository, times(1)).findByName(requestExamDto.getExamType());
        verify(timetableService, times(1)).getGeneralGroupList();
        verify(groupRepository, times(1)).findAllByNameIn(generalGroups);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<StudentGroup>> groupCaptor = ArgumentCaptor.forClass(List.class);
        verify(groupRepository, times(1)).saveAll(groupCaptor.capture());
        Set<String> capturedGroups = groupCaptor
                .getValue()
                .stream()
                .map(StudentGroup::getName)
                .collect(Collectors.toSet());
        assertEquals(generalGroups, capturedGroups);

        ArgumentCaptor<Exam> examCaptor = ArgumentCaptor.forClass(Exam.class);
        verify(examRepository, times(1)).save(examCaptor.capture());
        Exam savedExam = examCaptor.getValue();
        assertExam(savedExam, date, savedId, generalGroups);
    }


    /**
     * test specification
     * generalGroup         - 1 item
     * subgroup             - 1 items
     * timetable service    - available
     * provided groups      - match groups from timetable service
     * groupRepository      - don't contain provided groups
     */
    @Test
    void addExamForSingleGeneralGroupAndSingleSubgroup() throws JsonProcessingException {
        //        given
        Set<String> generalGroups = Set.of("12K2");
        Set<String> subgroups = Set.of("K04");
        when(timetableService.getAvailableSubGroups(any(String.class))).thenReturn(
                new ArrayList<>(List.of("K03", "K04", "L04")));
        testExamServiceForSubgroups(generalGroups, subgroups);
    }

    /**
     * test specification
     * generalGroup         - 1 item
     * subgroup             - 4 items
     * timetable service    - available
     * provided groups      - match groups from timetable service
     * groupRepository      - don't contain provided groups
     */
    @Test
    void addExamForSingleGeneralGroupAndMultipleSubgroup() throws JsonProcessingException {
        //        given
        Set<String> generalGroups = Set.of("12K2");
        Set<String> subgroups = Set.of("K04", "P04", "L04", "L03");
        when(timetableService.getAvailableSubGroups(any(String.class))).thenReturn(
                new ArrayList<>(List.of("K03", "K04", "P04", "L04", "L03")));
        testExamServiceForSubgroups(generalGroups, subgroups);
    }


    /**
     * test specification
     * generalGroup         - 0 item
     * subgroup             - 1 items
     * timetable service    - available
     * provided groups      - match groups from timetable service
     * groupRepository      - don't contain provided groups
     */
    @Test
    void addExamForEmptyGeneralGroup() {
        //        given
        Set<String> generalGroups = Set.of();
        Set<String> subgroups = Set.of("K04");
        LocalDateTime date = LocalDateTime.now().plusDays(1);
        RequestExamDto requestExamDto = buildExampleExamDto(generalGroups, subgroups, date);
        RuntimeException exception = assertThrows(
                InvalidGroupIdentifierException.class,
                () -> examService.addExam(requestExamDto)
        );
        assertEquals("Invalid group identifier: general group is missing", exception.getMessage());
    }

    @Test
    void addExamThatAlreadyExists() throws JsonProcessingException {
        //        given
        LocalDateTime date = LocalDateTime.now().plusDays(1);
        ExamType examType = buildExampleExamType();
        RequestExamDto requestExamDto = buildExampleExamDto(Set.of("12K2"), Set.of("L04"), date.plusSeconds(34));
        Set<StudentGroup> studentGroups = new HashSet<>(buildExampleStudentGroupList(Set.of("12K2", "L04")));
        Exam exam = ExamDtoMapper.mapToNewExam(requestExamDto, studentGroups, examType);

        when(timetableService.getGeneralGroupList()).thenReturn(new ArrayList<>(List.of("12K1", "12K2", "12K3")));
        when(timetableService.getAvailableSubGroups("12K2")).thenReturn(new ArrayList<>(List.of("L04")));
        //noinspection unchecked
        when(groupRepository.findAllByNameIn(any(Set.class))).thenReturn(studentGroups);
        //

        when(examTypeRepository.findByName(requestExamDto.getExamType())).thenReturn(Optional.of(examType));
        when(examRepository.findAllByTitle(requestExamDto.getTitle())).thenReturn(Set.of(exam));
        //        when
        RuntimeException exception = assertThrows(
                ResourceAlreadyExistsException.class,
                () -> examService.addExam(requestExamDto)
        );
        //        then
        verify(examRepository, times(0)).save(exam);
        assertEquals("Exam already exists", exception.getMessage());

    }

    //</editor-fold>

    //<editor-fold desc="service available, groups don't match service">

    /**
     * test specification
     * generalGroup         - 2 item
     * subgroup             - 0 items
     * timetable service    - available
     * provided groups      - don't match groups from timetable service
     * groupRepository      - don't contain provided groups
     */
    @Test
    void shouldThrowWhenGeneralGroupsDontMatchService() throws JsonProcessingException {
        //        given
        Set<String> generalGroups = Set.of("12K1", "12K2");
        Set<String> subgroups = Set.of();

        LocalDateTime date = LocalDateTime.now().plusDays(1);
        RequestExamDto requestExamDto = buildExampleExamDto(generalGroups, subgroups, date);
        when(timetableService.getGeneralGroupList()).thenReturn(new ArrayList<>(List.of()));
        //        when
        RuntimeException exception = assertThrows(
                InvalidGroupIdentifierException.class, () -> examService.addExam(requestExamDto));
        //        then
        assertEquals("Invalid group identifiers: [12K1, 12K2]", exception.getMessage());
    }

    @Test
    void shouldThrowWhenNotAllGeneralGroupsMatchService() throws JsonProcessingException {
        //        given
        Set<String> generalGroups = Set.of("12K1", "12K2");
        Set<String> subgroups = Set.of();

        LocalDateTime date = LocalDateTime.now().plusDays(1);
        RequestExamDto requestExamDto = buildExampleExamDto(generalGroups, subgroups, date);
        when(timetableService.getGeneralGroupList()).thenReturn(new ArrayList<>(List.of("12K1")));
        //        when
        RuntimeException exception = assertThrows(
                InvalidGroupIdentifierException.class, () -> examService.addExam(requestExamDto));
        //        then
        assertEquals("Invalid group identifiers: [12K2]", exception.getMessage());
    }

    /**
     * test specification
     * generalGroup         - 1 item
     * subgroup             - 3 items
     * timetable service    - available
     * provided groups      - partially match groups from timetable service
     * groupRepository      - don't contain provided groups
     */
    @Test
    void shouldThrowWhenSubgroupsDontMatchService() throws JsonProcessingException {
        //        given
        Set<String> generalGroups = Set.of("12K2");
        Set<String> subgroups = Set.of("K04", "P04", "L04");

        LocalDateTime date = LocalDateTime.now().plusDays(1);
        RequestExamDto requestExamDto = buildExampleExamDto(generalGroups, subgroups, date);
        when(timetableService.getGeneralGroupList()).thenReturn(new ArrayList<>(List.of("12K2")));
        when(timetableService.getAvailableSubGroups("12K2")).thenReturn(List.of("K05"));
        //        when
        RuntimeException exception = assertThrows(
                InvalidGroupIdentifierException.class, () -> examService.addExam(requestExamDto));
        //        then
        String message = exception.getMessage();
        assertTrue(message.startsWith("Invalid group identifiers:"));
        assertFalse(message.contains("12K2"));
        assertTrue(message.contains("K04"));
        assertTrue(message.contains("P04"));
        assertTrue(message.contains("L04"));
        assertFalse(message.contains("K05"));
    }

    @Test
    void shouldThrowWhenNotAllSubgroupsMatchService() throws JsonProcessingException {
        //        given
        Set<String> generalGroups = Set.of("12K2");
        Set<String> subgroups = Set.of("K04", "P04", "L04");

        LocalDateTime date = LocalDateTime.now().plusDays(1);
        RequestExamDto requestExamDto = buildExampleExamDto(generalGroups, subgroups, date);
        when(timetableService.getGeneralGroupList()).thenReturn(new ArrayList<>(List.of("12K2")));
        when(timetableService.getAvailableSubGroups("12K2")).thenReturn(List.of("P04", "L04", "K05"));
        //        when
        RuntimeException exception = assertThrows(
                InvalidGroupIdentifierException.class, () -> examService.addExam(requestExamDto));
        //        then
        String message = exception.getMessage();
        assertTrue(message.startsWith("Invalid group identifiers:"));
        assertFalse(message.contains("12K2"));
        assertTrue(message.contains("K04"));
        assertFalse(message.contains("P04"));
        assertFalse(message.contains("L04"));
        assertFalse(message.contains("K05"));
    }

    //</editor-fold>

    //<editor-fold desc="repository contain groups, service available">

    /**
     * test specification
     * generalGroup         - 1 item
     * subgroup             - 0 items
     * timetable service    - available
     * provided groups      - match groups from timetable service
     * groupRepository      - contain provided groups
     */
    @Test
    void addExamForSingleGeneralGroupWithRepositoryContainingGroup() throws JsonProcessingException {
        //        given
        Set<String> generalGroups = Set.of("12K2");
        Set<String> subgroups = Set.of();

        LocalDateTime date = LocalDateTime.now().plusDays(1);
        RequestExamDto requestExamDto = buildExampleExamDto(generalGroups, subgroups, date);
        ExamType examType = buildExampleExamType();
        List<StudentGroup> studentGroups = buildExampleStudentGroupList(generalGroups);
        Exam exam = buildExamWithIdAndGroups(studentGroups);

        when(examTypeRepository.findByName(requestExamDto.getExamType())).thenReturn(Optional.of(examType));
        when(timetableService.getGeneralGroupList()).thenReturn(new ArrayList<>(generalGroups));

        when(groupRepository.findAllByNameIn(generalGroups)).thenReturn(new HashSet<>(studentGroups));
        when(groupRepository.saveAll(any())).thenReturn(List.of());
        when(examRepository.save(any(Exam.class))).thenReturn(exam);
        //        when
        int savedId = examService.addExam(requestExamDto);
        //        then
        verify(examTypeRepository, times(1)).findByName(requestExamDto.getExamType());
        verify(timetableService, times(1)).getGeneralGroupList();
        verify(groupRepository, times(1)).findAllByNameIn(any());       //???
        verify(groupRepository, times(1)).saveAll(List.of());

        ArgumentCaptor<Exam> examCaptor = ArgumentCaptor.forClass(Exam.class);
        verify(examRepository, times(1)).save(examCaptor.capture());
        Exam savedExam = examCaptor.getValue();
        assertExam(savedExam, date, savedId, generalGroups);
    }

    @Test
    void addExamWithNonUniqueTitle() throws JsonProcessingException {
        //        given
        Set<String> generalGroups = Set.of("12K2");
        Set<String> subgroups = Set.of();

        LocalDateTime date = LocalDateTime.now().plusDays(1);
        RequestExamDto requestExamDto = buildExampleExamDto(generalGroups, subgroups, date);
        ExamType examType = buildExampleExamType();
        List<StudentGroup> studentGroups = buildExampleStudentGroupList(generalGroups);
        Exam newExam = buildExamWithIdAndGroups(studentGroups);
        Exam existingExam = Exam.builder()
                .title("title")
                .description("description")
                .examDate(date.plusHours(4))
                .examType(examType)
                .groups(new HashSet<>(studentGroups))
                .build();

        when(examTypeRepository.findByName(requestExamDto.getExamType())).thenReturn(Optional.of(examType));
        when(timetableService.getGeneralGroupList()).thenReturn(new ArrayList<>(generalGroups));

        when(groupRepository.findAllByNameIn(generalGroups)).thenReturn(new HashSet<>(studentGroups));
        when(groupRepository.saveAll(any())).thenReturn(List.of());
        when(examRepository.findAllByTitle(any())).thenReturn(Set.of(existingExam));
        when(examRepository.save(any(Exam.class))).thenReturn(newExam);
        //        when
        int savedId = examService.addExam(requestExamDto);
        //        then
        verify(examTypeRepository, times(1)).findByName(requestExamDto.getExamType());
        verify(timetableService, times(1)).getGeneralGroupList();
        verify(groupRepository, times(1)).findAllByNameIn(any());       //???
        verify(groupRepository, times(1)).saveAll(List.of());

        ArgumentCaptor<Exam> examCaptor = ArgumentCaptor.forClass(Exam.class);
        verify(examRepository, times(1)).save(examCaptor.capture());
        Exam savedExam = examCaptor.getValue();
        assertExam(savedExam, date, savedId, generalGroups);
    }

    /**
     * test specification
     * generalGroup         - 1 item
     * subgroup             - 4 items
     * timetable service    - available
     * provided groups      - match groups from timetable service
     * groupRepository      - partially contain provided groups
     */
    @Test
    void addExamForSingleGeneralGroupAndSubgroupsWithRepositoryContainingGroups()
            throws JsonProcessingException {
        //        given
        Set<String> generalGroups = Set.of("12K2");
        Set<String> subgroups = Set.of("K04", "P04", "L04", "K05");
        Set<String> combinedGroups = Set.of("12K", "K04", "P04", "L04", "K05");

        LocalDateTime date = LocalDateTime.now().plusDays(1);
        RequestExamDto requestExamDto = buildExampleExamDto(generalGroups, subgroups, date);
        ExamType examType = buildExampleExamType();
        List<StudentGroup> studentGroups = buildExampleStudentGroupList(combinedGroups);
        Exam exam = buildExamWithIdAndGroups(studentGroups);

        when(examTypeRepository.findByName(requestExamDto.getExamType())).thenReturn(Optional.of(examType));
        when(timetableService.getGeneralGroupList()).thenReturn(new ArrayList<>(generalGroups));
        when(timetableService.getAvailableSubGroups("12K2")).thenReturn(List.of("K04", "P04", "L04", "K05"));

        //noinspection unchecked
        when(groupRepository.findAllByNameIn(any(Set.class))).thenReturn(
                new HashSet<>(studentGroups.subList(0, 3)));
        when(groupRepository.saveAll(any())).thenReturn(studentGroups.subList(3, 5));
        when(examRepository.save(any(Exam.class))).thenReturn(exam);
        //        when
        int savedId = examService.addExam(requestExamDto);
        //        then
        verify(examTypeRepository, times(1)).findByName(requestExamDto.getExamType());
        verify(timetableService, times(1)).getGeneralGroupList();
        verify(groupRepository, times(1)).findAllByNameIn(any());
        verify(groupRepository, times(1)).saveAll(any());

        ArgumentCaptor<Exam> examCaptor = ArgumentCaptor.forClass(Exam.class);
        verify(examRepository, times(1)).save(examCaptor.capture());
        Exam savedExam = examCaptor.getValue();
        assertExam(savedExam, date, savedId, combinedGroups);
    }

    //</editor-fold>

    //<editor-fold desc="repository don't contain groups, service unavailable">

    /**
     * test specification
     * generalGroup         - 1 item
     * subgroup             - 0 item
     * timetable service    - unavailable
     * provided groups      - match groups from timetable service
     * groupRepository      - don't contain provided groups
     */
    @Test
    void unavailableServiceAndRepositoryDontMatch() throws JsonProcessingException {
        //        given
        Set<String> generalGroups = Set.of("12K2");
        Set<String> subgroups = Set.of();

        LocalDateTime date = LocalDateTime.now().plusDays(1);
        RequestExamDto requestExamDto = buildExampleExamDto(generalGroups, subgroups, date);

        //        more groups than in set
        when(timetableService.getGeneralGroupList()).thenThrow(new WebPageContentNotAvailableException());
        when(groupRepository.findAllByNameIn(generalGroups)).thenReturn(new HashSet<>(Set.of()));
        //        when
        RuntimeException exception = assertThrows(
                ServiceNotAvailableException.class, () -> examService.addExam(requestExamDto));
        //        then
        assertEquals(
                "Timetable service unavailable, couldn't verify groups using repository", exception.getMessage());
        verify(timetableService, times(1)).getGeneralGroupList();
        verify(groupRepository, times(1)).findAllByNameIn(generalGroups);
    }


    /**
     * test specification
     * generalGroup         - 1 item
     * subgroup             - 3 items
     * timetable service    - unavailable
     * provided groups      - match groups from timetable service
     * groupRepository      - partially contain provided groups
     */
    @Test
    void unavailableServiceAndRepositoryDontMatchForSubgroups() throws JsonProcessingException {
        //        given
        Set<String> generalGroups = Set.of("12K2");
        Set<String> subgroups = Set.of("L04", "K04", "P04");

        LocalDateTime date = LocalDateTime.now().plusDays(1);
        RequestExamDto requestExamDto = buildExampleExamDto(generalGroups, subgroups, date);
        List<StudentGroup> studentGroups = buildExampleStudentGroupList(Set.of("12K2", "L04"));

        //        more groups than in set
        when(timetableService.getGeneralGroupList()).thenThrow(new WebPageContentNotAvailableException());
        when(timetableService.getAvailableSubGroups("12K2")).thenThrow(new WebPageContentNotAvailableException());
        when(groupRepository.findAllByNameIn(any())).thenReturn(new HashSet<>(studentGroups));
        //        when
        RuntimeException exception = assertThrows(
                ServiceNotAvailableException.class, () -> examService.addExam(requestExamDto));
        //        then
        assertEquals(
                "Timetable service unavailable, couldn't verify groups using repository", exception.getMessage());
        verify(timetableService, times(1)).getGeneralGroupList();
        verify(groupRepository, times(1)).findAllByNameIn(generalGroups);
    }

    //</editor-fold>

    //<editor-fold desc="repository contain groups, service unavailable">

    /**
     * test specification
     * generalGroup         - 2 item
     * subgroup             - 0 item
     * timetable service    - unavailable
     * provided groups      - match groups from timetable service
     * groupRepository      - contain provided groups
     */
    @Test
    void addExamWhenServiceIsUnavailableAndRepositoryContainsGeneralGroups() throws JsonProcessingException {
        //        given
        Set<String> generalGroups = Set.of("12K1", "12K2");
        Set<String> subgroups = Set.of();

        LocalDateTime date = LocalDateTime.now().plusDays(1);
        RequestExamDto requestExamDto = buildExampleExamDto(generalGroups, subgroups, date);
        ExamType examType = buildExampleExamType();
        List<StudentGroup> studentGroups = buildExampleStudentGroupList(generalGroups);
        Exam exam = buildExamWithIdAndGroups(studentGroups);

        when(examTypeRepository.findByName(requestExamDto.getExamType())).thenReturn(Optional.of(examType));
        when(timetableService.getGeneralGroupList()).thenThrow(new WebPageContentNotAvailableException());

        when(groupRepository.findAllByNameIn(generalGroups)).thenReturn(new HashSet<>(studentGroups));
        when(groupRepository.saveAll(anyList())).thenReturn(studentGroups);
        when(examRepository.save(any(Exam.class))).thenReturn(exam);
        //        when
        int savedId = examService.addExam(requestExamDto);
        //        then
        verify(examTypeRepository, times(1)).findByName(requestExamDto.getExamType());
        verify(timetableService, times(1)).getGeneralGroupList();
        verify(groupRepository, times(2)).findAllByNameIn(any());
        verify(groupRepository, times(1)).saveAll(any());

        ArgumentCaptor<Exam> examCaptor = ArgumentCaptor.forClass(Exam.class);
        verify(examRepository, times(1)).save(examCaptor.capture());
        Exam savedExam = examCaptor.getValue();
        assertExam(savedExam, date, savedId, generalGroups);
    }

    /**
     * test specification
     * generalGroup         - 1 item
     * subgroup             - 4 items
     * timetable service    - unavailable
     * provided groups      - match groups from timetable service
     * groupRepository      - contain provided groups
     */
    @Test
    void addExamWhenServiceIsUnavailableAndRepositoryContainsGroups() throws JsonProcessingException {
        //        given
        Set<String> generalGroups = Set.of("12K2");
        Set<String> subgroups = Set.of("L04", "K04", "P04", "K05");
        Set<String> combinedGroups = Set.of("12K", "12K2", "L04", "K04", "P04", "K05");

        LocalDateTime date = LocalDateTime.now().plusDays(1);
        RequestExamDto requestExamDto = buildExampleExamDto(generalGroups, subgroups, date);
        ExamType examType = buildExampleExamType();
        List<StudentGroup> studentGroups = buildExampleStudentGroupList(combinedGroups);
        Exam exam = buildExamWithIdAndGroups(studentGroups);

        when(examTypeRepository.findByName(requestExamDto.getExamType())).thenReturn(Optional.of(examType));
        when(timetableService.getGeneralGroupList()).thenThrow(new WebPageContentNotAvailableException());
        when(timetableService.getAvailableSubGroups("12K2")).thenThrow(
                new JsonParseException("parsing subgroups failed"));

        //noinspection unchecked
        when(groupRepository.findAllByNameIn(any(Set.class))).thenReturn(new HashSet<>(studentGroups));

        when(groupRepository.saveAll(anyList())).thenReturn(List.of());
        when(examRepository.save(any(Exam.class))).thenReturn(exam);
        //noinspection unchecked
        when(examRepository.findCommonExamIdsForGroups(any(Set.class), any(Integer.class))).thenReturn(Set.of(1));
        //        when
        int savedId = examService.addExam(requestExamDto);
        //        then
        verify(examTypeRepository, times(1)).findByName(requestExamDto.getExamType());
        verify(timetableService, times(1)).getGeneralGroupList();
        verify(groupRepository, times(2)).findAllByNameIn(any());
        verify(groupRepository, times(1)).saveAll(any());

        ArgumentCaptor<Exam> examCaptor = ArgumentCaptor.forClass(Exam.class);
        verify(examRepository, times(1)).save(examCaptor.capture());
        Exam savedExam = examCaptor.getValue();
        assertExam(savedExam, date, savedId, combinedGroups);
    }

    //</editor-fold>

    //modify exam


    @Test
    @Disabled("move test to controller")
    void shouldThrowExceptionWhenExamIdNotExists() {
        //        given
        int examId = 5;
        when(examRepository.findById(examId)).thenThrow(new NoSuchElementException("Exam not found"));
        //        when
        RuntimeException exception = assertThrows(
                NoSuchElementException.class,
                () -> examService.deleteExam(examId)
        );
        //        then
        verify(examRepository, never()).deleteById(examId);
        assertEquals("Exam not found", exception.getMessage());
    }

    /************************************************************************************/
    //    getExamById
    @Test
    void getExamById() {
        //        given
        int examId = 1;
        when(examRepository.findById(examId)).thenReturn(Optional.of(mock(Exam.class)));
        //        when
        Exam exam = examService.getExamById(examId);
        //        then
        verify(examRepository).findById(examId);
        assertNotNull(exam);
    }

    @Test
    void shouldThrowExceptionWhenExamNotFound() {
        //        given
        int examId = 5;
        when(examRepository.findById(examId)).thenThrow(new NoSuchElementException("Exam not found"));
        //        when
        RuntimeException exception = assertThrows(
                NoSuchElementException.class,
                () -> examService.getExamById(examId)
        );
        //        then
        assertEquals("Exam not found", exception.getMessage());
    }

    //    getExamByGroup

    @Test
    void getExamsForNormalGroups() {
        //    given
        Set<String> generalGroups = Set.of("12K2");
        Set<String> subgroups = Set.of("L04", "K04", "P04");
        //    when
        examService.getExamByGroups(generalGroups, subgroups);
        //    then
        verify(examRepository, never()).findAllByGroups_NameIn(any());
        verify(examRepository, times(1)).findAllBySubgroupsOfSuperiorGroupAndGeneralGroup(
                "12K", Set.of("12K2"), subgroups);
    }

    @Test
    void getExamsForGroupWithoutDigitAsLastCharacter() {
        //    given
        Set<String> generalGroups = Set.of("1Er");
        Set<String> subgroups = Set.of("L01", "K01", "P01");
        //    when
        examService.getExamByGroups(generalGroups, subgroups);
        //    then
        verify(examRepository, never()).findAllByGroups_NameIn(any());
        verify(examRepository, times(1)).findAllBySubgroupsOfSuperiorGroupAndGeneralGroup(
                "1Er", generalGroups, subgroups);
    }

    @Test
    void getExamsWithEmptySubgroups() {
        //    given
        Set<String> generalGroups = Set.of("12K2");
        Set<String> subgroups = Set.of();
        //    when
        examService.getExamByGroups(generalGroups, subgroups);
        //    then
        verify(examRepository, times(1)).findAllByGroups_NameIn(generalGroups);
        verify(examRepository, never()).findAllBySubgroupsOfSuperiorGroupAndGeneralGroup(any(), any(), any());
    }

    @Test
    void getExamsWithBlankSubgroups() {
        //    given
        Set<String> generalGroups = Set.of("12K2");
        Set<String> subgroups = null;
        //    when
        examService.getExamByGroups(generalGroups, subgroups);
        //    then
        verify(examRepository, times(1)).findAllByGroups_NameIn(generalGroups);
        verify(examRepository, never()).findAllBySubgroupsOfSuperiorGroupAndGeneralGroup(any(), any(), any());
    }

    @Test
    void shouldNotThrowWhenGroupsAreFromTheSameYearOfStudy() {
        //    given
        Set<String> generalGroups = Set.of("12K1", "12K2");
        Set<String> subgroups = Set.of("L01", "K01", "P01");
        //    when
        examService.getExamByGroups(generalGroups, subgroups);
        //    then
        verify(examRepository, never()).findAllByGroups_NameIn(any());
        verify(examRepository, times(1)).findAllBySubgroupsOfSuperiorGroupAndGeneralGroup(
                "12K", generalGroups, subgroups);
    }

    @Test
    void shouldThrowWhenSubgroupsAreSwappedWithGeneralGroups() {
        //    given
        Set<String> generalGroups = new HashSet<>(Set.of("L01", "K01", "P01"));
        Set<String> subgroups = new HashSet<>(Set.of("12K1"));
        //    when then
        assertThrows(
                InvalidGroupIdentifierException.class,
                () -> examService.getExamByGroups(generalGroups, subgroups)
        );
    }

    @Test
    void shouldThrowWhenSubgroupsAreTheGeneralGroups() {
        //    given
        Set<String> generalGroups = new HashSet<>(Set.of("12K1"));
        Set<String> subgroups = new HashSet<>(Set.of("12K1", "12K2", "12K3"));
        //    when, then
        assertThrows(
                SpecifiedSubGroupDoesntExistsException.class,
                () -> examService.getExamByGroups(generalGroups, subgroups)
        );
    }

    @Test
    void shouldThrowWhenGeneralGroupsAreFromDifferentYearOfStudy() {
        //    given
        Set<String> generalGroups = Set.of("12K1", "12A2");
        Set<String> subgroups = Set.of("L01", "K01", "P01");
        //    when
        RuntimeException exception = assertThrows(
                InvalidGroupIdentifierException.class, () -> examService.getExamByGroups(generalGroups, subgroups));
        //    then
        assertEquals("Invalid group identifier: ambiguous general groups for subgroups", exception.getMessage());
    }


    // Updated helper methods to match new schema
    private static List<StudentGroup> buildExampleStudentGroupList(Set<String> groupNames) {
        AtomicInteger id = new AtomicInteger(1); // group_id starts from 1
        return groupNames.stream()
                .map(g -> StudentGroup.builder()
                        .groupId(id.getAndIncrement())
                        .name(g)
                        .build())
                .collect(Collectors.toList());
    }

    private static Exam buildExamWithIdAndGroups(List<StudentGroup> groups) {
        return Exam.builder()
                .examId(1)
                .title("title")
                .description("description")
                .examDate(LocalDateTime.now().plusDays(1))
                .examType(buildExampleExamType())
                .groups(new HashSet<>(groups))
                .build();
    }

    private static ExamType buildExampleExamType() {
        return ExamType.builder()
                .examTypeId(1)
                .name("exam")
                .build();
    }

    private static RequestExamDto buildExampleExamDto(Set<String> generalGroups,
                                                      Set<String> subgroups,
                                                      LocalDateTime date) {
        return RequestExamDto.builder()
                .title("title")
                .description("description")
                .date(date)
                .examType("exam")
                .generalGroups(new HashSet<>(generalGroups))
                .subgroups(new HashSet<>(subgroups))
                .build();
    }

    private static void assertExam(Exam savedExam, LocalDateTime date, int savedId, Set<String> groups) {
        assertEquals("title", savedExam.getTitle());
        assertEquals("description", savedExam.getDescription());
        assertEquals(date, savedExam.getExamDate());
        assertEquals("exam", savedExam.getExamType().getName());
        assertEquals(groups.size(), savedExam.getGroups().size());
        assertEquals(
                groups, savedExam.getGroups().stream().map(StudentGroup::getName).collect(Collectors.toSet()));
        assertEquals(1, savedId);
    }

    private void testExamServiceForSubgroups(Set<String> generalGroups, Set<String> subgroups)
            throws JsonProcessingException {
        Set<String> combinedGroups = new HashSet<>(subgroups);
        combinedGroups.addAll(generalGroups.stream()
                .map(g -> g.matches(".*\\d$") ? g.substring(0, g.length() - 1) : g)
                .collect(Collectors.toSet()));

        LocalDateTime date = LocalDateTime.now().plusDays(1);
        RequestExamDto requestExamDto = buildExampleExamDto(generalGroups, subgroups, date);
        ExamType examType = buildExampleExamType();
        List<StudentGroup> studentGroups = buildExampleStudentGroupList(combinedGroups);
        Exam exam = buildExamWithIdAndGroups(studentGroups);

        when(examTypeRepository.findByName(requestExamDto.getExamType())).thenReturn(Optional.of(examType));
        when(timetableService.getGeneralGroupList()).thenReturn(new ArrayList<>(generalGroups));

        when(groupRepository.findAllByNameIn(combinedGroups)).thenReturn(new HashSet<>(Set.of()));
        when(groupRepository.saveAll(anyList())).thenReturn(studentGroups);
        when(examRepository.save(any(Exam.class))).thenReturn(exam);
        //        when
        int savedId = examService.addExam(requestExamDto);
        //        then
        verify(examTypeRepository, times(1)).findByName(requestExamDto.getExamType());
        verify(timetableService, times(1)).getGeneralGroupList();
        verify(groupRepository, times(1)).findAllByNameIn(combinedGroups);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<StudentGroup>> groupCaptor = ArgumentCaptor.forClass(List.class);
        verify(groupRepository, times(1)).saveAll(groupCaptor.capture());
        Set<String> capturedGroups = groupCaptor
                .getValue()
                .stream()
                .map(StudentGroup::getName)
                .collect(Collectors.toSet());
        assertEquals(combinedGroups, capturedGroups);

        ArgumentCaptor<Exam> examCaptor = ArgumentCaptor.forClass(Exam.class);
        verify(examRepository, times(1)).save(examCaptor.capture());
        Exam savedExam = examCaptor.getValue();
        assertExam(savedExam, date, savedId, combinedGroups);
    }
}

