package com.monolith.modularmonolith.courses.internal.service;


import com.monolith.modularmonolith.courses.internal.dto.request.AssignCoursesRequest;
import com.monolith.modularmonolith.courses.internal.dto.request.ClassroomRequest;
import com.monolith.modularmonolith.courses.internal.dto.request.EnrollStudentsRequest;
import com.monolith.modularmonolith.courses.internal.dto.response.ClassroomResponse;
import com.monolith.modularmonolith.courses.internal.model.Classroom;
import com.monolith.modularmonolith.courses.internal.model.Course;
import com.monolith.modularmonolith.courses.internal.repository.ClassroomRepository;
import com.monolith.modularmonolith.courses.internal.repository.CourseRepository;
import com.monolith.modularmonolith.users.internal.model.User;
import com.monolith.modularmonolith.users.internal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClassroomService {

    private final ClassroomRepository classroomRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    @Transactional
    public ClassroomResponse createClassroom(ClassroomRequest request) {
        if (classroomRepository.existsByName(request.name())) {
            throw new IllegalArgumentException("Une classe avec ce nom existe déjà");
        }
        var classroom = Classroom.builder()
                .name(request.name())
                .gradeLevel(request.gradeLevel())
                .section(request.section())
                .academicYear(request.academicYear())
                .roomNumber(request.roomNumber())
                .capacity(request.capacity())
                .active(true)
                .build();
        return toResponse(classroomRepository.save(classroom));
    }

    public List<ClassroomResponse> listAll() {
        return classroomRepository.findByActiveTrue().stream()
                .map(this::toResponse)
                .toList();
    }

    public ClassroomResponse getById(Long id) {
        return toResponse(findById(id));
    }

    @Transactional
    public ClassroomResponse updateClassroom(Long id, ClassroomRequest request) {
        var classroom = findById(id);
        classroom.setName(request.name());
        classroom.setGradeLevel(request.gradeLevel());
        classroom.setSection(request.section());
        classroom.setAcademicYear(request.academicYear());
        classroom.setRoomNumber(request.roomNumber());
        classroom.setCapacity(request.capacity());
        return toResponse(classroomRepository.save(classroom));
    }

    @Transactional
    public void deleteClassroom(Long id) {
        var classroom = findById(id);
        classroom.setActive(false);
        classroomRepository.save(classroom);
    }

    @Transactional
    public ClassroomResponse setHomeroomTeacher(Long classroomId, Long teacherId) {
        var classroom = findById(classroomId);
        var teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new IllegalArgumentException("Enseignant non trouvé"));
        classroom.setHomeroomTeacher(teacher);
        return toResponse(classroomRepository.save(classroom));
    }

    @Transactional
    public ClassroomResponse enrollStudents(Long classroomId, EnrollStudentsRequest request) {
        var classroom = findById(classroomId);
        List<User> students = userRepository.findAllById(request.studentIds());
        if (students.size() != request.studentIds().size()) {
            throw new IllegalArgumentException("Certains élèves sont introuvables");
        }
        if (classroom.getStudents().size() + students.size() > classroom.getCapacity()) {
            throw new IllegalArgumentException("Capacité de la classe dépassée");
        }
        classroom.getStudents().addAll(students);
        return toResponse(classroomRepository.save(classroom));
    }

    @Transactional
    public ClassroomResponse assignCourses(Long classroomId, AssignCoursesRequest request) {
        var classroom = findById(classroomId);
        List<Course> courses = courseRepository.findAllById(request.courseIds());
        if (courses.size() != request.courseIds().size()) {
            throw new IllegalArgumentException("Certains cours sont introuvables");
        }
        classroom.getCourses().addAll(courses);
        return toResponse(classroomRepository.save(classroom));
    }

    private Classroom findById(Long id) {
        return classroomRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Classe non trouvée"));
    }

    private ClassroomResponse toResponse(Classroom classroom) {
        var homeroom = classroom.getHomeroomTeacher() != null
                ? new ClassroomResponse.TeacherSummary(
                classroom.getHomeroomTeacher().getId(),
                classroom.getHomeroomTeacher().getUsername(),
                classroom.getHomeroomTeacher().getEmail())
                : null;
        return new ClassroomResponse(
                classroom.getId(), classroom.getName(), classroom.getGradeLevel(),
                classroom.getSection(), classroom.getAcademicYear(),
                classroom.getRoomNumber(), classroom.getCapacity(),
                classroom.getActive(), homeroom,
                classroom.getStudents().size(), classroom.getCourses().size()
        );
    }
}