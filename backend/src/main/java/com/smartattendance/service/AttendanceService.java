package com.smartattendance.service;

import com.smartattendance.model.Attendance;
import com.smartattendance.model.Course;
import com.smartattendance.model.User;
import com.smartattendance.repo.AttendanceRepository;
import com.smartattendance.repo.CourseRepository;
import com.smartattendance.repo.UserRepository;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.Map;

@Service
public class AttendanceService {
    private final AttendanceRepository attendanceRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    public AttendanceService(AttendanceRepository attendanceRepository, CourseRepository courseRepository, UserRepository userRepository) {
        this.attendanceRepository = attendanceRepository;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
    }

    public Map<String, String> startSession(Long courseId) {
        // Generate temporary Wi-Fi password (stub)
        String password = generatePassword();
        String ssid = "Classroom_" + courseId + "_WiFi";
        // TODO: Call network controller to set temp password and start monitoring
        return Map.of("ssid", ssid, "password", password);
    }

    public boolean handleDeviceConnected(String macAddress, Long courseId) {
        return userRepository.findByMacAddress(macAddress)
                .map(user -> {
                    Course course = courseRepository.findById(courseId).orElseThrow();
                    Attendance a = new Attendance();
                    a.setCourse(course);
                    a.setStudent(user);
                    a.setPresent(true);
                    a.setTimestamp(LocalDateTime.now());
                    a.setMethod("WIFI");
                    attendanceRepository.save(a);
                    return true;
                }).orElse(false);
    }

    private String generatePassword() {
        byte[] bytes = new byte[6];
        new SecureRandom().nextBytes(bytes);
        return HexFormat.of().formatHex(bytes);
    }
}


