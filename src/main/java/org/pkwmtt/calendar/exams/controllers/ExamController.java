package org.pkwmtt.calendar.exams.controllers;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.pkwmtt.calendar.exams.services.ExamService;
import org.pkwmtt.calendar.exams.dto.RequestExamDto;
import org.pkwmtt.calendar.exams.dto.ResponseExamDto;
import org.pkwmtt.calendar.exams.entity.ExamType;
import org.pkwmtt.calendar.exams.mapper.ExamDtoMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Set;

@Validated
@RequiredArgsConstructor
@RequestMapping("${apiPrefix}/exams")
@RestController
public class ExamController {

    private final ExamService examService;

    /**
     * @param requestExamDto details of exam
     * @return 201 created with URI to GET method which returns created resource
     */
    @PostMapping("")
    public ResponseEntity<Void> addExam(@RequestBody @Valid RequestExamDto requestExamDto){
        int id = examService.addExam(requestExamDto);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();
        return ResponseEntity.created(uri).build();
    }

    /**
     * @param id of exam or test
     * @param requestExamDto new details of exam or test
     * @return 204 no content
     */
    @PutMapping("/{id}")
    public ResponseEntity<Void> modifyExam(@PathVariable @Positive int id, @RequestBody @Valid RequestExamDto requestExamDto) {
        examService.modifyExam(requestExamDto, id);
        return ResponseEntity.noContent().build();
    }

    /**
     * @param id of exam or test
     * @return 204 no content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExam(@PathVariable int id) {
        examService.deleteExam(id);
        return ResponseEntity.noContent().build();
    }
    

    /**
     * when subgroups isn't null all generalGroups must be form the same year of study. e.g. 12K2, 12K1 is from 12K
     * @param generalGroups set of general groups e.g. 12K2
     * @param subgroups set of subgroups of general group e.g. L04
     * @return List of RequestExamDto for specific groups
     */
    @GetMapping("/by-groups")
    public ResponseEntity<List<ResponseExamDto>> getExams(
            @RequestParam Set<String> generalGroups,
            @RequestParam(required = false) Set<String> subgroups
    ){
        return ResponseEntity.ok(ExamDtoMapper.mapToExamDto(examService.getExamByGroups(generalGroups, subgroups)));
    }

    /**
     * @return 200 ok with list of available exam types
     */
    @GetMapping("/exam-types")
    public ResponseEntity<List<ExamType>> getExamTypes(){
        return ResponseEntity.ok(examService.getExamTypes());
    }

}