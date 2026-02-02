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
    private record StartRequest(Long courseId) {}
    private record DeviceEvent(String macAddress, Long courseId) {}

    private final AttendanceRepository attendanceRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    private final com.smartattendance.service.AttendanceService attendanceService;
    private final com.smartattendance.service.QrService qrService;
    private final com.smartattendance.service.WifiService wifiService;
    private final com.smartattendance.security.JwtUtil jwtUtil;

    public AttendanceController(AttendanceRepository attendanceRepository, CourseRepository courseRepository, UserRepository userRepository, com.smartattendance.service.AttendanceService attendanceService, com.smartattendance.security.JwtUtil jwtUtil, com.smartattendance.service.QrService qrService, com.smartattendance.service.WifiService wifiService) {
        this.attendanceRepository = attendanceRepository;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.attendanceService = attendanceService;
        this.jwtUtil = jwtUtil;
        this.qrService = qrService;
        this.wifiService = wifiService;
    }

    @PostMapping("/qr")
    @PreAuthorize("hasRole('LECTURER')")
    public ResponseEntity<?> generateQr(@RequestBody QRRequest request) {
        // Simplified: return a pseudo token. In production, make it time-bound and signed.
        String token = jwtUtil.generateTokenWithTtl("qr",
                java.util.Map.of("courseId", request.courseId()),
                2 * 60 * 1000L);
        return ResponseEntity.ok(Map.of("qrToken", token));
    }

    @PostMapping(value = "/qr/png", produces = "image/png")
    @PreAuthorize("hasRole('LECTURER')")
    public ResponseEntity<byte[]> generateQrPng(@RequestBody QRRequest request) {
        String token = jwtUtil.generateTokenWithTtl("qr",
                java.util.Map.of("courseId", request.courseId()),
                2 * 60 * 1000L);
        byte[] png = qrService.pngBytes(token, 256);
        return ResponseEntity.ok(png);
    }

    @PostMapping("/start")
    @PreAuthorize("hasRole('LECTURER')")
    public ResponseEntity<?> start(@RequestBody StartRequest req) {
        return ResponseEntity.ok(wifiService.startForCourse(req.courseId()));
    }

    @PostMapping("/device-connected")
    public ResponseEntity<?> deviceConnected(@RequestBody DeviceEvent evt) {
        boolean ok = attendanceService.handleDeviceConnected(evt.macAddress(), evt.courseId());
        return ok ? ResponseEntity.ok(Map.of("status", "recorded")) : ResponseEntity.status(404).body(Map.of("error", "MAC not registered"));
    }

    @PostMapping("/scan")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> scan(@RequestBody ScanRequest request) {
        try {
            var claims = jwtUtil.parse(request.qrToken());
            Long courseId = ((Number) claims.get("courseId")).longValue();
            if (!courseId.equals(request.courseId())) {
                return ResponseEntity.status(400).body(Map.of("error", "QR course mismatch"));
            }
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
        } catch (Exception e) {
            return ResponseEntity.status(400).body(Map.of("error", "Invalid or expired QR"));
        }
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


