package com.monolith.modularmonolith.courses.internal.controller;

import com.monolith.modularmonolith.courses.internal.dto.request.AssignTeachersRequest;
import com.monolith.modularmonolith.courses.internal.dto.request.CourseRequest;
import com.monolith.modularmonolith.courses.internal.dto.response.CourseResponse;
import com.monolith.modularmonolith.courses.internal.service.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @PostMapping
    @PreAuthorize("hasAuthority('course:create')")
    public ResponseEntity<CourseResponse> createCourse(@Valid @RequestBody CourseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(courseService.createCourse(request));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('course:read')")
    public ResponseEntity<List<CourseResponse>> listAll() {
        return ResponseEntity.ok(courseService.listAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('course:read')")
    public ResponseEntity<CourseResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.getById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('course:update')")
    public ResponseEntity<CourseResponse> updateCourse(@PathVariable Long id, @Valid @RequestBody CourseRequest request) {
        return ResponseEntity.ok(courseService.updateCourse(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('course:delete')")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/teachers")
    @PreAuthorize("hasAuthority('teacher:courses:manage')")
    public ResponseEntity<CourseResponse> assignTeachers(
            @PathVariable Long id,
            @Valid @RequestBody AssignTeachersRequest request) {
        return ResponseEntity.ok(courseService.assignTeachers(id, request));
    }
}