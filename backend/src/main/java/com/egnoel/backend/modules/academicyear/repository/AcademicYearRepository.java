package com.egnoel.backend.modules.academicyear.repository;

import com.egnoel.backend.modules.academicyear.entity.AcademicYear;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AcademicYearRepository extends JpaRepository<AcademicYear, Long> {
    List<AcademicYear> findByActiveTrue();
    List<AcademicYear> findByInstitutionId(Long institutionId);
}
