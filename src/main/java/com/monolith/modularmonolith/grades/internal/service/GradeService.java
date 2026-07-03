package com.monolith.modularmonolith.grades.internal.service;

import com.monolith.modularmonolith.grades.internal.dto.GradeRequest;
import com.monolith.modularmonolith.grades.internal.dto.GradeResponse;
import com.monolith.modularmonolith.grades.internal.dto.GradeStatisticsResponse;
import com.monolith.modularmonolith.grades.internal.model.Grade;
import com.monolith.modularmonolith.grades.internal.repository.GradeRepository;
import com.monolith.modularmonolith.users.api.UserLookup;
import com.monolith.modularmonolith.users.api.UserSummary;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GradeService {

    private final GradeRepository gradeRepository;
    private final UserLookup userLookup;

    private static final String ROLE_ELEVE = "ROLE_ELEVE";
    private static final String ROLE_ENSEIGNANT = "ROLE_ENSEIGNANT";
    private static final String ROLE_ADMIN = "ROLE_ADMIN";
    private static final String ROLE_SUPERADMIN = "ROLE_SUPERADMIN";

    @Transactional
    public GradeResponse createGrade(GradeRequest request, String teacherEmail) {
        UserSummary currentUser = getCurrentUser(teacherEmail);
        validateIsTeacherOrAdmin(currentUser);

        UserSummary student = userLookup.findById(request.studentId())
                .orElseThrow(() -> new IllegalArgumentException("Élève non trouvé : " + request.studentId()));
        if (!student.roles().contains(ROLE_ELEVE)) {
            throw new IllegalArgumentException("L'utilisateur ciblé n'est pas un élève");
        }

        UserSummary teacher = userLookup.findById(request.teacherId())
                .orElseThrow(() -> new IllegalArgumentException("Enseignant non trouvé : " + request.teacherId()));
        if (!teacher.roles().contains(ROLE_ENSEIGNANT)) {
            throw new IllegalArgumentException("L'utilisateur assigné n'est pas un enseignant");
        }

        if (isTeacher(currentUser) && !currentUser.id().equals(request.teacherId())) {
            throw new AccessDeniedException("Vous ne pouvez attribuer des notes que pour vos propres évaluations");
        }

        Grade grade = Grade.builder()
                .studentId(request.studentId())
                .courseId(request.courseId())
                .teacherId(request.teacherId())
                .value(request.value())
                .maxValue(request.maxValue())
                .type(request.type())
                .gradeDate(request.gradeDate())
                .semester(request.semester())
                .academicYear(request.academicYear())
                .comment(request.comment())
                .active(true)
                .build();

        gradeRepository.save(grade);
        log.info("Note créée : id={}, studentId={}, courseId={}, value={}/{}",
                grade.getId(), grade.getStudentId(), grade.getCourseId(), grade.getValue(), grade.getMaxValue());

        return mapToResponse(grade, student, teacher);
    }

    @Transactional(readOnly = true)
    public GradeResponse getGradeById(Long id) {
        Grade grade = gradeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Note non trouvée : " + id));
        UserSummary student = userLookup.findById(grade.getStudentId()).orElse(null);
        UserSummary teacher = userLookup.findById(grade.getTeacherId()).orElse(null);
        return mapToResponse(grade, student, teacher);
    }

    @Transactional(readOnly = true)
    public List<GradeResponse> listGrades(Long studentId, Long courseId, String academicYear) {
        List<Grade> grades;
        if (studentId != null && courseId != null) {
            grades = gradeRepository.findByStudentIdAndCourseIdAndActiveTrue(studentId, courseId);
        } else if (studentId != null) {
            grades = gradeRepository.findByStudentIdAndActiveTrue(studentId);
        } else if (courseId != null) {
            grades = gradeRepository.findByCourseIdAndActiveTrue(courseId);
        } else {
            grades = gradeRepository.findAll().stream().filter(Grade::isActive).toList();
        }

        if (academicYear != null) {
            grades = grades.stream().filter(g -> academicYear.equals(g.getAcademicYear())).toList();
        }

        return grades.stream()
                .map(g -> {
                    UserSummary s = userLookup.findById(g.getStudentId()).orElse(null);
                    UserSummary t = userLookup.findById(g.getTeacherId()).orElse(null);
                    return mapToResponse(g, s, t);
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public GradeResponse updateGrade(Long id, GradeRequest request, String teacherEmail) {
        Grade grade = gradeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Note non trouvée : " + id));

        UserSummary currentUser = getCurrentUser(teacherEmail);
        if (isTeacher(currentUser) && !currentUser.id().equals(grade.getTeacherId())) {
            throw new AccessDeniedException("Vous ne pouvez modifier que vos propres notes");
        }

        grade.setValue(request.value());
        grade.setMaxValue(request.maxValue());
        grade.setType(request.type());
        grade.setGradeDate(request.gradeDate());
        grade.setSemester(request.semester());
        grade.setAcademicYear(request.academicYear());
        grade.setComment(request.comment());

        gradeRepository.save(grade);

        UserSummary student = userLookup.findById(grade.getStudentId()).orElse(null);
        UserSummary teacher = userLookup.findById(grade.getTeacherId()).orElse(null);
        return mapToResponse(grade, student, teacher);
    }

    @Transactional
    public void deleteGrade(Long id, String teacherEmail) {
        Grade grade = gradeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Note non trouvée : " + id));

        UserSummary currentUser = getCurrentUser(teacherEmail);
        if (isTeacher(currentUser) && !currentUser.id().equals(grade.getTeacherId())) {
            throw new AccessDeniedException("Vous ne pouvez supprimer que vos propres notes");
        }

        grade.setActive(false);
        gradeRepository.save(grade);
        log.info("Note supprimée (soft) : id={}", id);
    }

    @Transactional(readOnly = true)
    public List<GradeResponse> getMyGrades(String email) {
        UserSummary user = getCurrentUser(email);

        if (user.roles().contains(ROLE_ELEVE)) {
            return gradeRepository.findByStudentIdAndActiveTrue(user.id()).stream()
                    .map(g -> mapToResponse(g, user, null))
                    .collect(Collectors.toList());
        } else if (user.roles().contains(ROLE_ENSEIGNANT)) {
            return gradeRepository.findByTeacherIdAndActiveTrue(user.id()).stream()
                    .map(g -> {
                        UserSummary s = userLookup.findById(g.getStudentId()).orElse(null);
                        return mapToResponse(g, s, user);
                    })
                    .collect(Collectors.toList());
        } else {
            return gradeRepository.findAll().stream()
                    .filter(Grade::isActive)
                    .map(g -> {
                        UserSummary s = userLookup.findById(g.getStudentId()).orElse(null);
                        UserSummary t = userLookup.findById(g.getTeacherId()).orElse(null);
                        return mapToResponse(g, s, t);
                    })
                    .collect(Collectors.toList());
        }
    }

    @Transactional(readOnly = true)
    public GradeStatisticsResponse getCourseStatistics(Long courseId) {
        BigDecimal avg = gradeRepository.findAverageByCourseId(courseId);
        BigDecimal max = gradeRepository.findMaxByCourseId(courseId);
        BigDecimal min = gradeRepository.findMinByCourseId(courseId);
        Long count = gradeRepository.countByCourseId(courseId);

        return new GradeStatisticsResponse(
                courseId,
                null,
                avg != null ? avg.setScale(2, RoundingMode.HALF_UP) : null,
                max,
                min,
                count
        );
    }

    // --- Helpers ---

    private UserSummary getCurrentUser(String email) {
        return userLookup.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur connecté non trouvé"));
    }

    private void validateIsTeacherOrAdmin(UserSummary user) {
        if (!user.roles().contains(ROLE_ENSEIGNANT) &&
                !user.roles().contains(ROLE_ADMIN) &&
                !user.roles().contains(ROLE_SUPERADMIN)) {
            throw new AccessDeniedException("Action réservée aux enseignants et administrateurs");
        }
    }

    private boolean isTeacher(UserSummary user) {
        return user.roles().contains(ROLE_ENSEIGNANT);
    }

    private GradeResponse mapToResponse(Grade grade, UserSummary student, UserSummary teacher) {
        return new GradeResponse(
                grade.getId(),
                grade.getStudentId(),
                student != null ? student.username() : null,
                grade.getCourseId(),
                grade.getTeacherId(),
                teacher != null ? teacher.username() : null,
                grade.getValue(),
                grade.getMaxValue(),
                grade.getType(),
                grade.getGradeDate(),
                grade.getSemester(),
                grade.getAcademicYear(),
                grade.getComment(),
                grade.isActive(),
                grade.getCreatedAt()
        );
    }
}