package com.egnoel.backend.modules.assessment.repository;

import com.egnoel.backend.modules.assessment.entity.Assessment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssessmentRepository extends JpaRepository<Assessment, Long> {
}
