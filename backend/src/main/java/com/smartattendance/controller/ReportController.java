package com.smartattendance.controller;

import com.smartattendance.model.Attendance;
import com.smartattendance.model.Course;
import com.smartattendance.model.User;
import com.smartattendance.repo.AttendanceRepository;
import com.smartattendance.repo.CourseRepository;
import com.smartattendance.repo.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reports")
public class ReportController {
    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    public ReportController(AttendanceRepository attendanceRepository, UserRepository userRepository, CourseRepository courseRepository) {
        this.attendanceRepository = attendanceRepository;
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
    }

    @GetMapping("/student/{studentId}")
    public Map<String, Object> student(@PathVariable Long studentId) {
        User s = userRepository.findById(studentId).orElseThrow();
        List<Attendance> list = attendanceRepository.findByStudent(s);
        long present = list.stream().filter(Attendance::isPresent).count();
        return Map.of(
                "count", list.size(),
                "present", present,
                "absent", list.size() - present,
                "records", list
        );
    }

    @GetMapping("/course/{courseId}")
    public Map<String, Object> course(@PathVariable Long courseId) {
        Course c = courseRepository.findById(courseId).orElseThrow();
        List<Attendance> list = attendanceRepository.findByCourse(c);
        var grouped = list.stream().collect(Collectors.groupingBy(a -> a.getStudent().getId(), Collectors.counting()));
        long present = list.stream().filter(Attendance::isPresent).count();
        return Map.of(
                "count", list.size(),
                "present", present,
                "absent", list.size() - present,
                "perStudent", grouped,
                "records", list
        );
    }
}


