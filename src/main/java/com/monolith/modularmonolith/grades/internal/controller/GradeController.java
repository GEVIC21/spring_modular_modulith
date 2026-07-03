package com.monolith.modularmonolith.grades.internal.controller;

import com.monolith.modularmonolith.grades.internal.dto.GradeRequest;
import com.monolith.modularmonolith.grades.internal.dto.GradeResponse;
import com.monolith.modularmonolith.grades.internal.dto.GradeStatisticsResponse;
import com.monolith.modularmonolith.grades.internal.service.GradeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/grades")
@RequiredArgsConstructor
public class GradeController {

    private final GradeService gradeService;

    @PostMapping
    @PreAuthorize("hasAuthority('student:grades:write')")
    public ResponseEntity<GradeResponse> createGrade(@RequestBody @Valid GradeRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return ResponseEntity.ok(gradeService.createGrade(request, auth.getName()));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('student:grades:read')")
    public ResponseEntity<List<GradeResponse>> listGrades(
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) String academicYear) {
        return ResponseEntity.ok(gradeService.listGrades(studentId, courseId, academicYear));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('student:grades:read')")
    public ResponseEntity<GradeResponse> getGrade(@PathVariable Long id) {
        return ResponseEntity.ok(gradeService.getGradeById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('student:grades:write')")
    public ResponseEntity<GradeResponse> updateGrade(
            @PathVariable Long id,
            @RequestBody @Valid GradeRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return ResponseEntity.ok(gradeService.updateGrade(id, request, auth.getName()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('student:grades:write')")
    public ResponseEntity<Void> deleteGrade(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        gradeService.deleteGrade(id, auth.getName());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my-grades")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<GradeResponse>> getMyGrades() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return ResponseEntity.ok(gradeService.getMyGrades(auth.getName()));
    }

    @GetMapping("/statistics/course/{courseId}")
    @PreAuthorize("hasAnyAuthority('student:grades:read', 'teacher:grades:manage')")
    public ResponseEntity<GradeStatisticsResponse> getCourseStatistics(@PathVariable Long courseId) {
        return ResponseEntity.ok(gradeService.getCourseStatistics(courseId));
    }
}