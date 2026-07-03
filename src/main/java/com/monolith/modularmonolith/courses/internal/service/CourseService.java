package com.monolith.modularmonolith.courses.internal.service;

import com.monolith.modularmonolith.courses.internal.dto.request.AssignTeachersRequest;
import com.monolith.modularmonolith.courses.internal.dto.request.CourseRequest;
import com.monolith.modularmonolith.courses.internal.dto.response.CourseResponse;
import com.monolith.modularmonolith.courses.internal.model.Course;
import com.monolith.modularmonolith.courses.internal.repository.CourseRepository;
import com.monolith.modularmonolith.users.api.UserLookup;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final UserLookup userLookup;

    @Transactional
    public CourseResponse createCourse(CourseRequest request) {
        if (courseRepository.existsByCode(request.code())) {
            throw new IllegalArgumentException("Un cours avec ce code existe déjà");
        }
        var course = Course.builder()
                .code(request.code())
                .name(request.name())
                .description(request.description())
                .credits(request.credits())
                .department(request.department())
                .gradeLevel(request.gradeLevel())
                .active(true)
                .build();
        return toResponse(courseRepository.save(course));
    }

    public List<CourseResponse> listAll() {
        return courseRepository.findByActiveTrue().stream()
                .map(this::toResponse)
                .toList();
    }

    public CourseResponse getById(Long id) {
        return toResponse(findById(id));
    }

    @Transactional
    public CourseResponse updateCourse(Long id, CourseRequest request) {
        var course = findById(id);
        course.setName(request.name());
        course.setDescription(request.description());
        course.setCredits(request.credits());
        course.setDepartment(request.department());
        course.setGradeLevel(request.gradeLevel());
        return toResponse(courseRepository.save(course));
    }

    @Transactional
    public void deleteCourse(Long id) {
        var course = findById(id);
        course.setActive(false);
        courseRepository.save(course);
    }

    @Transactional
    public CourseResponse assignTeachers(Long courseId, AssignTeachersRequest request) {
        var course = findById(courseId);
        var teachers = userLookup.findAllById(request.teacherIds());
        if (teachers.size() != request.teacherIds().size()) {
            throw new IllegalArgumentException("Certains enseignants sont introuvables");
        }
        if (teachers.stream().anyMatch(t -> !t.roles().contains("ROLE_ENSEIGNANT"))) {
            throw new IllegalArgumentException("Tous les IDs doivent correspondre à des enseignants");
        }
        course.getTeacherIds().clear();
        course.getTeacherIds().addAll(request.teacherIds());
        return toResponse(courseRepository.save(course));
    }

    private Course findById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cours non trouvé"));
    }

    private CourseResponse toResponse(Course course) {
        var teachers = userLookup.findAllById(course.getTeacherIds()).stream()
                .map(t -> new CourseResponse.TeacherSummary(t.id(), t.username(), t.email()))
                .toList();
        return new CourseResponse(
                course.getId(), course.getCode(), course.getName(),
                course.getDescription(), course.getCredits(),
                course.getDepartment(), course.getGradeLevel(),
                course.getActive(), teachers
        );
    }
}