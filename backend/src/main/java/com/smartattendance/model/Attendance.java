package com.smartattendance.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Attendance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private User student;

    @ManyToOne(optional = false)
    private Course course;

    @Column(nullable = false)
    private boolean present;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column
    private String method; // QR, FACE, MANUAL

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getStudent() { return student; }
    public void setStudent(User student) { this.student = student; }
    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }
    public boolean isPresent() { return present; }
    public void setPresent(boolean present) { this.present = present; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }
}


