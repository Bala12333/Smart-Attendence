package com.smartattendance.repo;

import com.smartattendance.model.Course;
import com.smartattendance.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByLecturer(User lecturer);
}


