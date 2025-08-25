package com.smartattendance.controller;

import com.smartattendance.model.Course;
import com.smartattendance.model.User;
import com.smartattendance.repo.CourseRepository;
import com.smartattendance.repo.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/courses")
public class CourseController {
    private record CreateCourseRequest(String code, String name, Long lecturerId) {}

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    public CourseController(CourseRepository courseRepository, UserRepository userRepository) {
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<Course> list(Principal principal) {
        // If lecturer, show their courses; otherwise show all
        return courseRepository.findAll();
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','LECTURER')")
    public ResponseEntity<?> create(@RequestBody CreateCourseRequest body) {
        User lecturer = userRepository.findById(body.lecturerId()).orElseThrow();
        Course c = new Course();
        c.setCode(body.code());
        c.setName(body.name());
        c.setLecturer(lecturer);
        courseRepository.save(c);
        return ResponseEntity.ok(Map.of("id", c.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        return courseRepository.findById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}


