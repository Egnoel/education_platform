package com.egnoel.backend.modules.subject.repository;

import com.egnoel.backend.modules.subject.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubjectRepository extends JpaRepository<Subject,Long> {
}
