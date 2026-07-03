package com.monolith.modularmonolith.grades.internal.repository;

import com.monolith.modularmonolith.grades.internal.model.ReportCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReportCardRepository extends JpaRepository<ReportCard, Long> {
    List<ReportCard> findByStudentIdAndActiveTrue(Long studentId);
    List<ReportCard> findByStudentIdAndAcademicYearAndActiveTrue(Long studentId, String academicYear);
    List<ReportCard> findByClassroomIdAndAcademicYearAndSemesterAndActiveTrue(Long classroomId, String academicYear, String semester);
    Optional<ReportCard> findByStudentIdAndAcademicYearAndSemesterAndActiveTrue(Long studentId, String academicYear, String semester);
}