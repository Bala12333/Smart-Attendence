package com.smartattendance.config;

import com.smartattendance.model.Course;
import com.smartattendance.model.Role;
import com.smartattendance.model.User;
import com.smartattendance.repo.CourseRepository;
import com.smartattendance.repo.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seed(UserRepository users, CourseRepository courses, PasswordEncoder encoder) {
        return args -> {
            if (users.count() > 0) return;
            User admin = new User();
            admin.setUsername("admin");
            admin.setPasswordHash(encoder.encode("admin123"));
            admin.setRole(Role.ADMIN);
            admin.setFullName("System Admin");
            users.save(admin);

            User lecturer = new User();
            lecturer.setUsername("lecturer");
            lecturer.setPasswordHash(encoder.encode("lect123"));
            lecturer.setRole(Role.LECTURER);
            lecturer.setFullName("Dr. Sharma");
            users.save(lecturer);

            User student = new User();
            student.setUsername("student");
            student.setPasswordHash(encoder.encode("stud123"));
            student.setRole(Role.STUDENT);
            student.setFullName("Priya");
            student.setMacAddress("00:1A:2B:3C:4D:5E");
            users.save(student);

            Course c1 = new Course();
            c1.setCode("CS101");
            c1.setName("Data Structures");
            c1.setLecturer(lecturer);
            courses.save(c1);
        };
    }
}


