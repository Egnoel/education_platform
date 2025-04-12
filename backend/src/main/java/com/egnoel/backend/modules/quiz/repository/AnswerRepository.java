package com.egnoel.backend.modules.quiz.repository;

import com.egnoel.backend.modules.quiz.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
    List<Answer> findByQuizId(Long quizId);
    List<Answer> findByStudentId(Long studentId);
}
