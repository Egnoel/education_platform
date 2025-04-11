package com.egnoel.backend.modules.auth.repository;

import com.egnoel.backend.modules.auth.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeacherRepository extends JpaRepository<Teacher,Long> {
    Optional<Teacher> findByEmail(String email);
}
