package com.smartattendance.repo;

import com.smartattendance.model.Attendance;
import com.smartattendance.model.Course;
import com.smartattendance.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findByStudent(User student);
    List<Attendance> findByCourse(Course course);
    List<Attendance> findByCourseAndTimestampBetween(Course course, LocalDateTime start, LocalDateTime end);
}


