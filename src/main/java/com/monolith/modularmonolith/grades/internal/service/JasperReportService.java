package com.monolith.modularmonolith.grades.internal.service;

import com.monolith.modularmonolith.grades.internal.dto.GradeResponse;
import com.monolith.modularmonolith.grades.internal.dto.ReportCardResponse;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class JasperReportService {

    private static final String TEMPLATE_PATH = "templates/jasper/bulletin_template.jrxml";
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public byte[] generateBulletinPdf(ReportCardResponse reportCard, String schoolName, String schoolLogoPath) {
        try {
            // 1. Charger & compiler le template
            ClassPathResource resource = new ClassPathResource(TEMPLATE_PATH);
            InputStream templateStream = resource.getInputStream();
            JasperReport jasperReport = JasperCompileManager.compileReport(templateStream);

            // 2. Paramètres
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("schoolName", schoolName != null ? schoolName : "École Secondaire");
            parameters.put("bulletinTitle", "BULLETIN DE NOTES");
            parameters.put("academicYear", reportCard.academicYear());
            parameters.put("semester", reportCard.semester() != null ? reportCard.semester() : "Année complète");
            parameters.put("studentName", reportCard.studentName() != null ? reportCard.studentName() : "—");
            parameters.put("studentId", String.valueOf(reportCard.studentId()));
            parameters.put("classroomId", reportCard.classroomId() != null ? String.valueOf(reportCard.classroomId()) : "—");
            parameters.put("overallAverage", reportCard.overallAverage() != null
                    ? reportCard.overallAverage().toString() : "—");
            parameters.put("rank", reportCard.rank() != null
                    ? reportCard.rank() + " / " + reportCard.totalStudents() : "—");
            parameters.put("principalComment", reportCard.principalComment() != null
                    ? reportCard.principalComment() : "");
            parameters.put("generatedAt", java.time.LocalDate.now().format(DATE_FMT));

            // Logo école (optionnel)
            if (schoolLogoPath != null) {
                ClassPathResource logoResource = new ClassPathResource(schoolLogoPath);
                if (logoResource.exists()) {
                    parameters.put("schoolLogo", logoResource.getInputStream());
                }
            }

            // 3. DataSource = liste des notes
            List<GradeResponse> grades = reportCard.grades();
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(grades);

            // 4. Remplir le rapport
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

            // 5. Export PDF — JR 7.x : JasperExportManager uniquement
            byte[] pdfBytes = JasperExportManager.exportReportToPdf(jasperPrint);

            log.info("PDF bulletin généré : studentId={}, size={} bytes",
                    reportCard.studentId(), pdfBytes.length);
            return pdfBytes;

        } catch (Exception e) {
            log.error("Erreur génération PDF bulletin", e);
            throw new RuntimeException("Impossible de générer le PDF du bulletin", e);
        }
    }
}