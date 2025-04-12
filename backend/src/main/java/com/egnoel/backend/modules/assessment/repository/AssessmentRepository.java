package com.egnoel.backend.modules.assessment.repository;

import com.egnoel.backend.modules.assessment.entity.Assessment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssessmentRepository extends JpaRepository<Assessment, Long> {
    List<Assessment> findByStudentId(Long studentId);
    List<Assessment> findByClasseId(Long classeId);
    List<Assessment> findByClasseIdIn(List<Long> classeIds);
}
