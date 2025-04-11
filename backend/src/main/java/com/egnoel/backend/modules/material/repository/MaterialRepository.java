package com.egnoel.backend.modules.material.repository;

import com.egnoel.backend.modules.material.entity.Material;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MaterialRepository extends JpaRepository<Material, Long> {
    List<Material> findByTeacherId(Long teacherId);
    List<Material> findBySubjectId(Long subjectId);
    List<Material> findByClasseId(Long classeId);
}
