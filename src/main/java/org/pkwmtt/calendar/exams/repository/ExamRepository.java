package org.pkwmtt.calendar.exams.repository;

import org.pkwmtt.calendar.exams.entity.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface ExamRepository extends JpaRepository<Exam, Integer> {

    Set<Exam> findAllByTitle(String title);

    @Query("SELECT g.name FROM Exam e LEFT JOIN e.groups g WHERE e.examId = :id")
    Set<String> findGroupsByExamId(@Param("id") Integer examId);

    /**
     * @param groups set of generalGroups
     * @return set of exams for generalGroups
     */
    Set<Exam> findAllByGroups_NameIn(Set<String> groups);


    /**
     * @param superiorGroup group that identifies whole year of study e.g. 12K
     * @param generalGroup set of exercise groups e.g. 12K2
     * @param subgroup set of subgroups of provided superior group e.g. L04
     * @return set of exams containing generalGroups or superiorGroup with at least one provided subgroup
     */
    @Query("""
                SELECT DISTINCT e FROM Exam e
                JOIN e.groups g1
                JOIN FETCH e.groups g2
                WHERE (g1.name = :superior AND g2.name IN :sub)
                OR g2.name IN :general
            """)
    Set<Exam> findAllBySubgroupsOfSuperiorGroupAndGeneralGroup(
            @Param("superior") String superiorGroup,
            @Param("general") Set<String> generalGroup,
            @Param("sub") Set<String> subgroup);


    /**
     * Method could be used to check if provided subgroups belong to superior group by finding existing exam
     * related to provided groups in repository
     * @param groups set of subgroups with one superior group of provided subgroups e.g. 12K2, K04, K05
     * @param expectedSize size of provided groups set
     * @return set of ids of exams that contains all provided groups
     */
    @Query(value = """
            SELECT exam_id FROM exams_groups
            INNER JOIN student_groups ON exams_groups.group_id = student_groups.group_id
            WHERE student_groups.name IN (:groups)
            GROUP BY exam_id
            HAVING COUNT(DISTINCT exams_groups.group_id) = :expectedSize
            """, nativeQuery = true)
    Set<Integer> findCommonExamIdsForGroups(@Param("groups") Set<String> groups, @Param("expectedSize") int expectedSize);
}