package com.monolith.modularmonolith.courses.internal.service;

import com.monolith.modularmonolith.courses.internal.dto.request.ScheduleRequest;
import com.monolith.modularmonolith.courses.internal.dto.response.ScheduleResponse;
import com.monolith.modularmonolith.courses.internal.model.Classroom;
import com.monolith.modularmonolith.courses.internal.model.Course;
import com.monolith.modularmonolith.courses.internal.model.Schedule;
import com.monolith.modularmonolith.courses.internal.repository.ClassroomRepository;
import com.monolith.modularmonolith.courses.internal.repository.CourseRepository;
import com.monolith.modularmonolith.courses.internal.repository.ScheduleRepository;
import com.monolith.modularmonolith.users.internal.model.User;
import com.monolith.modularmonolith.users.internal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final CourseRepository courseRepository;
    private final ClassroomRepository classroomRepository;
    private final UserRepository userRepository;

    @Transactional
    public ScheduleResponse createSchedule(ScheduleRequest request) {
        validateTimes(request);
        var course = findCourse(request.courseId());
        var classroom = findClassroom(request.classroomId());
        var teacher = findTeacher(request.teacherId());

        var schedule = Schedule.builder()
                .course(course)
                .classroom(classroom)
                .teacher(teacher)
                .dayOfWeek(request.dayOfWeek())
                .startTime(request.startTime())
                .endTime(request.endTime())
                .room(request.room())
                .academicYear(request.academicYear())
                .semester(request.semester())
                .active(true)
                .build();

        validateNoConflicts(schedule, null);
        return toResponse(scheduleRepository.save(schedule));
    }

    public List<ScheduleResponse> listAll() {
        return scheduleRepository.findByActiveTrue().stream()
                .map(this::toResponse)
                .toList();
    }

    public ScheduleResponse getById(Long id) {
        return toResponse(findById(id));
    }

    @Transactional
    public ScheduleResponse updateSchedule(Long id, ScheduleRequest request) {
        validateTimes(request);
        var schedule = findById(id);
        var course = findCourse(request.courseId());
        var classroom = findClassroom(request.classroomId());
        var teacher = findTeacher(request.teacherId());

        schedule.setCourse(course);
        schedule.setClassroom(classroom);
        schedule.setTeacher(teacher);
        schedule.setDayOfWeek(request.dayOfWeek());
        schedule.setStartTime(request.startTime());
        schedule.setEndTime(request.endTime());
        schedule.setRoom(request.room());
        schedule.setAcademicYear(request.academicYear());
        schedule.setSemester(request.semester());

        validateNoConflicts(schedule, id);
        return toResponse(scheduleRepository.save(schedule));
    }

    @Transactional
    public void deleteSchedule(Long id) {
        var schedule = findById(id);
        schedule.setActive(false);
        scheduleRepository.save(schedule);
    }

    public List<ScheduleResponse> getByClassroom(Long classroomId) {
        var classroom = findClassroom(classroomId);
        return scheduleRepository.findByClassroom(classroom).stream()
                .filter(Schedule::getActive)
                .map(this::toResponse)
                .toList();
    }

    public List<ScheduleResponse> getByTeacher(Long teacherId) {
        var teacher = findTeacher(teacherId);
        return scheduleRepository.findByTeacher(teacher).stream()
                .filter(Schedule::getActive)
                .map(this::toResponse)
                .toList();
    }

    public List<ScheduleResponse> getMySchedule(String email) {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));

        boolean isTeacher = user.getRoleNames().contains("ROLE_ENSEIGNANT");
        boolean isStudent = user.getRoleNames().contains("ROLE_ELEVE");

        if (isTeacher) {
            return scheduleRepository.findByTeacherAndActiveTrue(user).stream()
                    .map(this::toResponse)
                    .toList();
        } else if (isStudent) {
            return classroomRepository.findByStudent(user).stream()
                    .flatMap(c -> c.getSchedules().stream())
                    .filter(Schedule::getActive)
                    .map(this::toResponse)
                    .toList();
        } else {
            // Admin / SuperAdmin → toutes les séances actives
            return listAll();
        }
    }

    private void validateTimes(ScheduleRequest request) {
        if (!request.startTime().isBefore(request.endTime())) {
            throw new IllegalArgumentException("L'heure de début doit être strictement avant l'heure de fin");
        }
    }

    private void validateNoConflicts(Schedule schedule, Long excludeId) {
        Long exId = excludeId != null ? excludeId : -1L;

        var teacherConflicts = scheduleRepository.findTeacherConflicts(
                schedule.getDayOfWeek(), schedule.getTeacher().getId(),
                schedule.getStartTime(), schedule.getEndTime(), exId);
        if (!teacherConflicts.isEmpty()) {
            throw new IllegalArgumentException("Conflit d'emploi du temps : l'enseignant est déjà occupé sur ce créneau");
        }

        var classroomConflicts = scheduleRepository.findClassroomConflicts(
                schedule.getDayOfWeek(), schedule.getClassroom().getId(),
                schedule.getStartTime(), schedule.getEndTime(), exId);
        if (!classroomConflicts.isEmpty()) {
            throw new IllegalArgumentException("Conflit d'emploi du temps : la classe a déjà un cours sur ce créneau");
        }

        var roomConflicts = scheduleRepository.findRoomConflicts(
                schedule.getDayOfWeek(), schedule.getRoom(),
                schedule.getStartTime(), schedule.getEndTime(), exId);
        if (!roomConflicts.isEmpty()) {
            throw new IllegalArgumentException("Conflit d'emploi du temps : la salle est déjà occupée sur ce créneau");
        }
    }

    private Schedule findById(Long id) {
        return scheduleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Séance non trouvée"));
    }

    private Course findCourse(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cours non trouvé"));
    }

    private Classroom findClassroom(Long id) {
        return classroomRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Classe non trouvée"));
    }

    private User findTeacher(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Enseignant non trouvé"));
    }

    private ScheduleResponse toResponse(Schedule s) {
        return new ScheduleResponse(
                s.getId(),
                new ScheduleResponse.CourseSummary(s.getCourse().getId(), s.getCourse().getCode(), s.getCourse().getName()),
                new ScheduleResponse.ClassroomSummary(s.getClassroom().getId(), s.getClassroom().getName(), s.getClassroom().getGradeLevel(), s.getClassroom().getSection()),
                new ScheduleResponse.TeacherSummary(s.getTeacher().getId(), s.getTeacher().getUsername(), s.getTeacher().getEmail()),
                s.getDayOfWeek(), s.getStartTime(), s.getEndTime(),
                s.getRoom(), s.getAcademicYear(), s.getSemester(), s.getActive()
        );
    }
}