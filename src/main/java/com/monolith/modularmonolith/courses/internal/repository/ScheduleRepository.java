package com.monolith.modularmonolith.courses.internal.repository;

import com.monolith.modularmonolith.courses.internal.model.Classroom;
import com.monolith.modularmonolith.courses.internal.model.Course;
import com.monolith.modularmonolith.courses.internal.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    List<Schedule> findByClassroom(Classroom classroom);
    List<Schedule> findByCourse(Course course);
    List<Schedule> findByDayOfWeek(DayOfWeek dayOfWeek);
    List<Schedule> findByAcademicYear(String academicYear);
    List<Schedule> findByActiveTrue();

    List<Schedule> findByTeacherIdAndActiveTrue(Long teacherId);

    @Query("""
        SELECT s FROM Schedule s
        WHERE s.dayOfWeek = :day
        AND s.teacherId = :teacherId
        AND s.active = true
        AND s.id <> :excludeId
        AND (s.startTime < :endTime AND s.endTime > :startTime)
        """)
    List<Schedule> findTeacherConflicts(
            @Param("day") DayOfWeek day,
            @Param("teacherId") Long teacherId,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime,
            @Param("excludeId") Long excludeId
    );

    @Query("""
        SELECT s FROM Schedule s
        WHERE s.dayOfWeek = :day
        AND s.classroom.id = :classroomId
        AND s.active = true
        AND s.id <> :excludeId
        AND (s.startTime < :endTime AND s.endTime > :startTime)
        """)
    List<Schedule> findClassroomConflicts(
            @Param("day") DayOfWeek day,
            @Param("classroomId") Long classroomId,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime,
            @Param("excludeId") Long excludeId
    );

    @Query("""
        SELECT s FROM Schedule s
        WHERE s.dayOfWeek = :day
        AND s.room = :room
        AND s.active = true
        AND s.id <> :excludeId
        AND (s.startTime < :endTime AND s.endTime > :startTime)
        """)
    List<Schedule> findRoomConflicts(
            @Param("day") DayOfWeek day,
            @Param("room") String room,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime,
            @Param("excludeId") Long excludeId
    );
}