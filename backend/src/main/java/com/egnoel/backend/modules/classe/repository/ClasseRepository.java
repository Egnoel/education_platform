package com.egnoel.backend.modules.classe.repository;

import com.egnoel.backend.modules.classe.entity.Classe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClasseRepository extends JpaRepository<Classe, Long> {
    List<Classe> findByTeacherId(Long teacherId);
    List<Classe> findBySubjectId(Long subjectId);
    List<Classe> findByAcademicYearId(Long academicYearId);
}
