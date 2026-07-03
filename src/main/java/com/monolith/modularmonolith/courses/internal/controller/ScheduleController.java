package com.monolith.modularmonolith.courses.internal.controller;

import com.monolith.modularmonolith.courses.internal.dto.request.ScheduleRequest;
import com.monolith.modularmonolith.courses.internal.dto.response.ScheduleResponse;
import com.monolith.modularmonolith.courses.internal.service.ScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    @PostMapping
    @PreAuthorize("hasAuthority('schedule:create')")
    public ResponseEntity<ScheduleResponse> createSchedule(@Valid @RequestBody ScheduleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(scheduleService.createSchedule(request));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('schedule:read')")
    public ResponseEntity<List<ScheduleResponse>> listAll() {
        return ResponseEntity.ok(scheduleService.listAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('schedule:read')")
    public ResponseEntity<ScheduleResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(scheduleService.getById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('schedule:update')")
    public ResponseEntity<ScheduleResponse> updateSchedule(@PathVariable Long id, @Valid @RequestBody ScheduleRequest request) {
        return ResponseEntity.ok(scheduleService.updateSchedule(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('schedule:delete')")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Long id) {
        scheduleService.deleteSchedule(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/classroom/{classroomId}")
    @PreAuthorize("hasAnyAuthority('schedule:read', 'class:manage')")
    public ResponseEntity<List<ScheduleResponse>> getByClassroom(@PathVariable Long classroomId) {
        return ResponseEntity.ok(scheduleService.getByClassroom(classroomId));
    }

    @GetMapping("/teacher/{teacherId}")
    @PreAuthorize("hasAnyAuthority('schedule:read', 'teacher:courses:manage')")
    public ResponseEntity<List<ScheduleResponse>> getByTeacher(@PathVariable Long teacherId) {
        return ResponseEntity.ok(scheduleService.getByTeacher(teacherId));
    }

    @GetMapping("/my-schedule")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ScheduleResponse>> getMySchedule(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(scheduleService.getMySchedule(userDetails.getUsername()));
    }
}