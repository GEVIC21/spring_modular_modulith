package com.monolith.modularmonolith.courses.internal.controller;


import com.monolith.modularmonolith.courses.internal.dto.request.AssignCoursesRequest;
import com.monolith.modularmonolith.courses.internal.dto.request.ClassroomRequest;
import com.monolith.modularmonolith.courses.internal.dto.request.EnrollStudentsRequest;
import com.monolith.modularmonolith.courses.internal.dto.response.ClassroomResponse;
import com.monolith.modularmonolith.courses.internal.service.ClassroomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/classrooms")
@RequiredArgsConstructor
public class ClassroomController {

    private final ClassroomService classroomService;

    @PostMapping
    @PreAuthorize("hasAuthority('class:manage')")
    public ResponseEntity<ClassroomResponse> createClassroom(@Valid @RequestBody ClassroomRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(classroomService.createClassroom(request));
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('class:manage', 'course:read')")
    public ResponseEntity<List<ClassroomResponse>> listAll() {
        return ResponseEntity.ok(classroomService.listAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('class:manage', 'course:read')")
    public ResponseEntity<ClassroomResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(classroomService.getById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('class:manage')")
    public ResponseEntity<ClassroomResponse> updateClassroom(@PathVariable Long id, @Valid @RequestBody ClassroomRequest request) {
        return ResponseEntity.ok(classroomService.updateClassroom(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('class:manage')")
    public ResponseEntity<Void> deleteClassroom(@PathVariable Long id) {
        classroomService.deleteClassroom(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/students")
    @PreAuthorize("hasAuthority('class:manage')")
    public ResponseEntity<ClassroomResponse> enrollStudents(
            @PathVariable Long id,
            @Valid @RequestBody EnrollStudentsRequest request) {
        return ResponseEntity.ok(classroomService.enrollStudents(id, request));
    }

    @PostMapping("/{id}/courses")
    @PreAuthorize("hasAuthority('class:manage')")
    public ResponseEntity<ClassroomResponse> assignCourses(
            @PathVariable Long id,
            @Valid @RequestBody AssignCoursesRequest request) {
        return ResponseEntity.ok(classroomService.assignCourses(id, request));
    }

    @PutMapping("/{id}/homeroom-teacher")
    @PreAuthorize("hasAuthority('class:manage')")
    public ResponseEntity<ClassroomResponse> setHomeroomTeacher(
            @PathVariable Long id,
            @RequestParam Long teacherId) {
        return ResponseEntity.ok(classroomService.setHomeroomTeacher(id, teacherId));
    }
}