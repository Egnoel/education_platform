package com.egnoel.backend.modules.quiz.repository;

import com.egnoel.backend.modules.quiz.entity.Question;
import com.egnoel.backend.modules.quiz.entity.QuestionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByQuizId(Long quizId);
    List<Question> findByQuizIdAndType(Long quizId, QuestionType type);
    List<Question> findByQuizIdAndScore(Long quizId, Integer score);
    List<Question> findByQuizIdAndTextContaining(Long quizId, String text);
    List<Question> findByQuizIdAndOptionsContaining(Long quizId, String options);
}
