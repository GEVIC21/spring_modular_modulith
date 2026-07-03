package com.monolith.modularmonolith.grades.internal.service;

import com.monolith.modularmonolith.grades.internal.dto.GradeResponse;
import com.monolith.modularmonolith.grades.internal.dto.ReportCardRequest;
import com.monolith.modularmonolith.grades.internal.dto.ReportCardResponse;
import com.monolith.modularmonolith.grades.internal.model.Grade;
import com.monolith.modularmonolith.grades.internal.model.ReportCard;
import com.monolith.modularmonolith.grades.internal.repository.GradeRepository;
import com.monolith.modularmonolith.grades.internal.repository.ReportCardRepository;
import com.monolith.modularmonolith.users.api.UserLookup;
import com.monolith.modularmonolith.users.api.UserSummary;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportCardService {

    private final ReportCardRepository reportCardRepository;
    private final GradeRepository gradeRepository;
    private final UserLookup userLookup;

    @Transactional
    public ReportCardResponse generateReportCard(ReportCardRequest request, String generatedByEmail) {
        UserSummary student = userLookup.findById(request.studentId())
                .orElseThrow(() -> new IllegalArgumentException("Élève non trouvé"));

        reportCardRepository.findByStudentIdAndAcademicYearAndSemesterAndActiveTrue(
                request.studentId(), request.academicYear(), request.semester()
        ).ifPresent(rc -> {
            throw new IllegalArgumentException("Un bulletin existe déjà pour cet élève et cette période");
        });

        List<Grade> grades;
        if (request.gradeIds() != null && !request.gradeIds().isEmpty()) {
            grades = gradeRepository.findAllById(request.gradeIds()).stream()
                    .filter(Grade::isActive)
                    .filter(g -> g.getStudentId().equals(request.studentId()))
                    .toList();
        } else {
            grades = gradeRepository.findByStudentIdAndAcademicYearAndSemesterAndActiveTrue(
                    request.studentId(), request.academicYear(), request.semester()
            );
        }

        if (grades.isEmpty()) {
            throw new IllegalArgumentException("Aucune note trouvée pour cette période");
        }

        BigDecimal overallAverage = calculateOverallAverage(grades);

        ReportCard reportCard = ReportCard.builder()
                .studentId(request.studentId())
                .academicYear(request.academicYear())
                .semester(request.semester())
                .gradeIds(grades.stream().map(Grade::getId).collect(Collectors.toSet()))
                .overallAverage(overallAverage)
                .classroomId(request.classroomId())
                .principalComment(request.principalComment())
                .generatedAt(LocalDateTime.now())
                .active(true)
                .build();

        reportCardRepository.save(reportCard);

        if (request.classroomId() != null) {
            recalculateRanks(request.classroomId(), request.academicYear(), request.semester());
        }

        return mapToResponse(reportCard, student, grades);
    }

    @Transactional(readOnly = true)
    public ReportCardResponse getReportCardById(Long id) {
        ReportCard reportCard = reportCardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Bulletin non trouvé"));
        UserSummary student = userLookup.findById(reportCard.getStudentId()).orElse(null);
        List<Grade> grades = gradeRepository.findAllById(reportCard.getGradeIds()).stream()
                .filter(Grade::isActive).toList();
        return mapToResponse(reportCard, student, grades);
    }

    @Transactional(readOnly = true)
    public List<ReportCardResponse> listReportCards(Long studentId, String academicYear) {
        List<ReportCard> cards;
        if (studentId != null && academicYear != null) {
            cards = reportCardRepository.findByStudentIdAndAcademicYearAndActiveTrue(studentId, academicYear);
        } else if (studentId != null) {
            cards = reportCardRepository.findByStudentIdAndActiveTrue(studentId);
        } else {
            cards = reportCardRepository.findAll().stream().filter(ReportCard::isActive).toList();
        }

        return cards.stream()
                .map(rc -> {
                    UserSummary s = userLookup.findById(rc.getStudentId()).orElse(null);
                    List<Grade> grades = gradeRepository.findAllById(rc.getGradeIds()).stream()
                            .filter(Grade::isActive).toList();
                    return mapToResponse(rc, s, grades);
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReportCardResponse> getReportCardsByStudent(Long studentId) {
        return listReportCards(studentId, null);
    }

    @Transactional(readOnly = true)
    public List<ReportCardResponse> getMyReportCards(String email) {
        UserSummary user = userLookup.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));
        return listReportCards(user.id(), null);
    }

    @Transactional
    public void deleteReportCard(Long id) {
        ReportCard reportCard = reportCardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Bulletin non trouvé"));
        reportCard.setActive(false);
        reportCardRepository.save(reportCard);

        if (reportCard.getClassroomId() != null) {
            recalculateRanks(reportCard.getClassroomId(), reportCard.getAcademicYear(), reportCard.getSemester());
        }
        log.info("Bulletin supprimé (soft) : id={}", id);
    }

    // --- Helpers ---

    private BigDecimal calculateOverallAverage(List<Grade> grades) {
        if (grades.isEmpty()) return BigDecimal.ZERO;

        BigDecimal sum = grades.stream()
                .map(g -> {
                    if (g.getMaxValue().compareTo(BigDecimal.valueOf(20)) == 0) {
                        return g.getValue();
                    }
                    return g.getValue()
                            .multiply(BigDecimal.valueOf(20))
                            .divide(g.getMaxValue(), 2, RoundingMode.HALF_UP);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return sum.divide(BigDecimal.valueOf(grades.size()), 2, RoundingMode.HALF_UP);
    }

    private void recalculateRanks(Long classroomId, String academicYear, String semester) {
        List<ReportCard> cards = reportCardRepository.findByClassroomIdAndAcademicYearAndSemesterAndActiveTrue(
                classroomId, academicYear, semester
        );

        cards.sort((a, b) -> {
            BigDecimal avgA = a.getOverallAverage() != null ? a.getOverallAverage() : BigDecimal.ZERO;
            BigDecimal avgB = b.getOverallAverage() != null ? b.getOverallAverage() : BigDecimal.ZERO;
            return avgB.compareTo(avgA);
        });

        int total = cards.size();
        for (int i = 0; i < total; i++) {
            ReportCard card = cards.get(i);
            card.setRank(i + 1);
            card.setTotalStudents(total);
            reportCardRepository.save(card);
        }
    }

    private ReportCardResponse mapToResponse(ReportCard rc, UserSummary student, List<Grade> grades) {
        List<GradeResponse> gradeResponses = grades.stream()
                .map(g -> {
                    UserSummary t = userLookup.findById(g.getTeacherId()).orElse(null);
                    return new GradeResponse(
                            g.getId(), g.getStudentId(), student != null ? student.username() : null,
                            g.getCourseId(), g.getTeacherId(), t != null ? t.username() : null,
                            g.getValue(), g.getMaxValue(), g.getType(), g.getGradeDate(),
                            g.getSemester(), g.getAcademicYear(), g.getComment(), g.isActive(), g.getCreatedAt()
                    );
                }).toList();

        return new ReportCardResponse(
                rc.getId(),
                rc.getStudentId(),
                student != null ? student.username() : null,
                rc.getAcademicYear(),
                rc.getSemester(),
                rc.getOverallAverage(),
                rc.getRank(),
                rc.getTotalStudents(),
                rc.getClassroomId(),
                rc.getPrincipalComment(),
                gradeResponses,
                rc.isActive(),
                rc.getGeneratedAt()
        );
    }
}