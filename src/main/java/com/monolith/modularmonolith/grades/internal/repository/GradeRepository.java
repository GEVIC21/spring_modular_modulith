package com.monolith.modularmonolith.grades.internal.repository;

import com.monolith.modularmonolith.grades.internal.model.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {
    List<Grade> findByStudentIdAndActiveTrue(Long studentId);
    List<Grade> findByCourseIdAndActiveTrue(Long courseId);
    List<Grade> findByTeacherIdAndActiveTrue(Long teacherId);
    List<Grade> findByStudentIdAndAcademicYearAndSemesterAndActiveTrue(Long studentId, String academicYear, String semester);
    List<Grade> findByStudentIdAndCourseIdAndActiveTrue(Long studentId, Long courseId);
    List<Grade> findByGradeDateBetweenAndActiveTrue(LocalDate startDate, LocalDate endDate);

    @Query("SELECT AVG(g.value) FROM Grade g WHERE g.courseId = :courseId AND g.active = true")
    BigDecimal findAverageByCourseId(@Param("courseId") Long courseId);

    @Query("SELECT MAX(g.value) FROM Grade g WHERE g.courseId = :courseId AND g.active = true")
    BigDecimal findMaxByCourseId(@Param("courseId") Long courseId);

    @Query("SELECT MIN(g.value) FROM Grade g WHERE g.courseId = :courseId AND g.active = true")
    BigDecimal findMinByCourseId(@Param("courseId") Long courseId);

    @Query("SELECT COUNT(g) FROM Grade g WHERE g.courseId = :courseId AND g.active = true")
    Long countByCourseId(@Param("courseId") Long courseId);
}