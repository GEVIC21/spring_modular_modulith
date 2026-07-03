package com.monolith.modularmonolith.courses.internal.service;


import com.monolith.modularmonolith.courses.internal.dto.request.AssignCoursesRequest;
import com.monolith.modularmonolith.courses.internal.dto.request.ClassroomRequest;
import com.monolith.modularmonolith.courses.internal.dto.request.EnrollStudentsRequest;
import com.monolith.modularmonolith.courses.internal.dto.response.ClassroomResponse;
import com.monolith.modularmonolith.courses.internal.model.Classroom;
import com.monolith.modularmonolith.courses.internal.repository.ClassroomRepository;
import com.monolith.modularmonolith.courses.internal.repository.CourseRepository;
import com.monolith.modularmonolith.users.api.UserLookup;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClassroomService {

    private final ClassroomRepository classroomRepository;
    private final CourseRepository courseRepository;
    private final UserLookup userLookup;

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
        var teacher = userLookup.findById(teacherId)
                .orElseThrow(() -> new IllegalArgumentException("Enseignant non trouvé"));
        if (!teacher.roles().contains("ROLE_ENSEIGNANT")) {
            throw new IllegalArgumentException("L'utilisateur doit être un enseignant");
        }
        classroom.setHomeroomTeacherId(teacherId);
        return toResponse(classroomRepository.save(classroom));
    }

    @Transactional
    public ClassroomResponse enrollStudents(Long classroomId, EnrollStudentsRequest request) {
        var classroom = findById(classroomId);
        var students = userLookup.findAllById(request.studentIds());
        if (students.size() != request.studentIds().size()) {
            throw new IllegalArgumentException("Certains élèves sont introuvables");
        }
        if (students.stream().anyMatch(s -> !s.roles().contains("ROLE_ELEVE"))) {
            throw new IllegalArgumentException("Tous les IDs doivent correspondre à des élèves");
        }
        if (classroom.getStudentIds().size() + request.studentIds().size() > classroom.getCapacity()) {
            throw new IllegalArgumentException("Capacité de la classe dépassée");
        }
        classroom.getStudentIds().addAll(request.studentIds());
        return toResponse(classroomRepository.save(classroom));
    }

    @Transactional
    public ClassroomResponse assignCourses(Long classroomId, AssignCoursesRequest request) {
        var classroom = findById(classroomId);
        var courses = courseRepository.findAllById(request.courseIds());
        if (courses.size() != request.courseIds().size()) {
            throw new IllegalArgumentException("Certains cours sont introuvables");
        }
        classroom.getCourseIds().addAll(request.courseIds());
        return toResponse(classroomRepository.save(classroom));
    }

    private Classroom findById(Long id) {
        return classroomRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Classe non trouvée"));
    }

    private ClassroomResponse toResponse(Classroom classroom) {
        var homeroom = classroom.getHomeroomTeacherId() != null
                ? userLookup.findById(classroom.getHomeroomTeacherId())
                .map(t -> new ClassroomResponse.TeacherSummary(t.id(), t.username(), t.email()))
                .orElse(null)
                : null;
        return new ClassroomResponse(
                classroom.getId(), classroom.getName(), classroom.getGradeLevel(),
                classroom.getSection(), classroom.getAcademicYear(),
                classroom.getRoomNumber(), classroom.getCapacity(),
                classroom.getActive(), homeroom,
                classroom.getStudentIds().size(), classroom.getCourseIds().size()
        );
    }
}