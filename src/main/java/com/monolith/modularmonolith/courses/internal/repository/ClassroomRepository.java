package com.monolith.modularmonolith.courses.internal.repository;

import com.monolith.modularmonolith.courses.internal.model.Classroom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassroomRepository extends JpaRepository<Classroom, Long> {
    Optional<Classroom> findByName(String name);
    boolean existsByName(String name);
    List<Classroom> findByGradeLevel(String gradeLevel);
    List<Classroom> findByAcademicYear(String academicYear);
    List<Classroom> findByActiveTrue();

    @Query("SELECT c FROM Classroom c JOIN c.studentIds sid WHERE sid = :studentId")
    List<Classroom> findByStudentId(@Param("studentId") Long studentId);
}