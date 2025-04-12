package com.egnoel.backend.modules.quiz.repository;

import com.egnoel.backend.modules.quiz.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuizRepository extends JpaRepository<Quiz, Long> {
    List<Quiz> findByTeacherId(Long teacherId);
    List<Quiz> findBySubjectId(Long subjectId);
    List<Quiz> findByClasseId(Long classeId);
}
