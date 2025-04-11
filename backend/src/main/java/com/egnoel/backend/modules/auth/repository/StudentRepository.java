package com.egnoel.backend.modules.auth.repository;

import com.egnoel.backend.modules.auth.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student,Long> {
    Optional<Student> findByEmail(String email);
}
