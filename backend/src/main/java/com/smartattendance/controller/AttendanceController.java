package com.smartattendance.controller;

import com.smartattendance.model.Attendance;
import com.smartattendance.model.Course;
import com.smartattendance.model.User;
import com.smartattendance.repo.AttendanceRepository;
import com.smartattendance.repo.CourseRepository;
import com.smartattendance.repo.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {
    private record QRRequest(Long courseId) {}
    private record ScanRequest(Long courseId, Long studentId, String qrToken) {}
    private record FaceRequest(Long courseId, Long studentId, String faceToken) {}
    private record ManualRequest(Long courseId, Long studentId, boolean present) {}

    private final AttendanceRepository attendanceRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    public AttendanceController(AttendanceRepository attendanceRepository, CourseRepository courseRepository, UserRepository userRepository) {
        this.attendanceRepository = attendanceRepository;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/qr")
    @PreAuthorize("hasRole('LECTURER')")
    public ResponseEntity<?> generateQr(@RequestBody QRRequest request) {
        // Simplified: return a pseudo token. In production, make it time-bound and signed.
        String token = "QR-" + request.courseId() + "-" + System.currentTimeMillis();
        return ResponseEntity.ok(Map.of("qrToken", token));
    }

    @PostMapping("/scan")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> scan(@RequestBody ScanRequest request) {
        Course course = courseRepository.findById(request.courseId()).orElseThrow();
        User student = userRepository.findById(request.studentId()).orElseThrow();
        Attendance a = new Attendance();
        a.setCourse(course);
        a.setStudent(student);
        a.setPresent(true);
        a.setTimestamp(LocalDateTime.now());
        a.setMethod("QR");
        attendanceRepository.save(a);
        return ResponseEntity.ok(Map.of("status", "recorded"));
    }

    @PostMapping("/face")
    @PreAuthorize("hasRole('LECTURER')")
    public ResponseEntity<?> face(@RequestBody FaceRequest request) {
        Course course = courseRepository.findById(request.courseId()).orElseThrow();
        User student = userRepository.findById(request.studentId()).orElseThrow();
        Attendance a = new Attendance();
        a.setCourse(course);
        a.setStudent(student);
        a.setPresent(true);
        a.setTimestamp(LocalDateTime.now());
        a.setMethod("FACE");
        attendanceRepository.save(a);
        return ResponseEntity.ok(Map.of("status", "recorded"));
    }

    @PutMapping("/manual")
    @PreAuthorize("hasRole('LECTURER')")
    public ResponseEntity<?> manual(@RequestBody ManualRequest request) {
        Course course = courseRepository.findById(request.courseId()).orElseThrow();
        User student = userRepository.findById(request.studentId()).orElseThrow();
        Attendance a = new Attendance();
        a.setCourse(course);
        a.setStudent(student);
        a.setPresent(request.present());
        a.setTimestamp(LocalDateTime.now());
        a.setMethod("MANUAL");
        attendanceRepository.save(a);
        return ResponseEntity.ok(Map.of("status", "updated"));
    }

    @GetMapping("/course/{courseId}")
    public List<Attendance> byCourse(@PathVariable Long courseId) {
        Course c = courseRepository.findById(courseId).orElseThrow();
        return attendanceRepository.findByCourse(c);
    }
}


