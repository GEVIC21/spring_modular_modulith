package com.monolith.modularmonolith.grades.internal.controller;

import com.monolith.modularmonolith.grades.internal.dto.ReportCardRequest;
import com.monolith.modularmonolith.grades.internal.dto.ReportCardResponse;
import com.monolith.modularmonolith.grades.internal.service.ReportCardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/report-cards")
@RequiredArgsConstructor
public class ReportCardController {

    private final ReportCardService reportCardService;

    @PostMapping
    @PreAuthorize("hasAuthority('student:grades:write')")
    public ResponseEntity<ReportCardResponse> generateReportCard(@RequestBody @Valid ReportCardRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return ResponseEntity.ok(reportCardService.generateReportCard(request, auth.getName()));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('student:grades:read')")
    public ResponseEntity<List<ReportCardResponse>> listReportCards(
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) String academicYear) {
        return ResponseEntity.ok(reportCardService.listReportCards(studentId, academicYear));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('student:grades:read')")
    public ResponseEntity<ReportCardResponse> getReportCard(@PathVariable Long id) {
        return ResponseEntity.ok(reportCardService.getReportCardById(id));
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAuthority('student:grades:read')")
    public ResponseEntity<List<ReportCardResponse>> getStudentReportCards(@PathVariable Long studentId) {
        return ResponseEntity.ok(reportCardService.getReportCardsByStudent(studentId));
    }

    @GetMapping("/my-report-cards")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ReportCardResponse>> getMyReportCards() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return ResponseEntity.ok(reportCardService.getMyReportCards(auth.getName()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('student:grades:write')")
    public ResponseEntity<Void> deleteReportCard(@PathVariable Long id) {
        reportCardService.deleteReportCard(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/pdf")
    @PreAuthorize("hasAuthority('student:grades:read')")
    public ResponseEntity<byte[]> downloadBulletinPdf(@PathVariable Long id) {
        byte[] pdfBytes = reportCardService.generateBulletinPdf(id, "Lycée International", "static/images/logo.png");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.attachment()
                .filename("bulletin_" + id + ".pdf")
                .build());

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }
}