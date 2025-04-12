package com.egnoel.backend.modules.quiz.service;

import com.egnoel.backend.modules.auth.entity.Teacher;
import com.egnoel.backend.modules.auth.repository.TeacherRepository;
import com.egnoel.backend.modules.quiz.dto.QuestionCreateDTO;
import com.egnoel.backend.modules.quiz.dto.QuestionResponseDTO;
import com.egnoel.backend.modules.quiz.dto.QuestionUpdateDTO;
import com.egnoel.backend.modules.quiz.entity.Question;
import com.egnoel.backend.modules.quiz.entity.Quiz;
import com.egnoel.backend.modules.quiz.repository.QuestionRepository;
import com.egnoel.backend.modules.quiz.repository.QuizRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionService {
    private final QuestionRepository questionRepository;
    private final QuizRepository quizRepository;
    private final TeacherRepository teacherRepository;

    @Autowired
    public QuestionService(QuestionRepository questionRepository, QuizRepository quizRepository,
                           TeacherRepository teacherRepository) {
        this.questionRepository = questionRepository;
        this.quizRepository = quizRepository;
        this.teacherRepository = teacherRepository;
    }

    @Transactional
    public QuestionResponseDTO createQuestion(Long quizId, QuestionCreateDTO dto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Teacher teacher = teacherRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Professor não encontrado"));

        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Questionário não encontrado"));

        if (!quiz.getTeacher().getId().equals(teacher.getId())) {
            throw new RuntimeException("Apenas o autor pode adicionar perguntas a este questionário");
        }

        Question question = new Question();
        question.setText(dto.getText());
        question.setType(dto.getType());
        question.setOptions(dto.getOptions());
        question.setCorrectAnswer(dto.getCorrectAnswer());
        question.setScore(dto.getScore());
        question.setQuiz(quiz);

        question = questionRepository.save(question);

        return new QuestionResponseDTO(
                question.getId(),
                question.getText(),
                question.getType(),
                question.getOptions(),
                question.getCorrectAnswer(),
                question.getScore()
        );
    }

    @Transactional
    public QuestionResponseDTO updateQuestion(Long quizId, Long questionId, QuestionUpdateDTO dto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Teacher teacher = teacherRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Professor não encontrado"));

        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Questionário não encontrado"));

        if (!quiz.getTeacher().getId().equals(teacher.getId())) {
            throw new RuntimeException("Apenas o autor pode editar perguntas deste questionário");
        }

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Pergunta não encontrada"));

        if (!question.getQuiz().getId().equals(quizId)) {
            throw new RuntimeException("A pergunta não pertence a este questionário");
        }

        question.setText(dto.getText());
        question.setType(dto.getType());
        question.setOptions(dto.getOptions());
        question.setCorrectAnswer(dto.getCorrectAnswer());
        question.setScore(dto.getScore());

        question = questionRepository.save(question);

        return new QuestionResponseDTO(
                question.getId(),
                question.getText(),
                question.getType(),
                question.getOptions(),
                question.getCorrectAnswer(),
                question.getScore()
        );
    }

    @Transactional
    public void deleteQuestion(Long quizId, Long questionId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Teacher teacher = teacherRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Professor não encontrado"));

        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Questionário não encontrado"));

        if (!quiz.getTeacher().getId().equals(teacher.getId())) {
            throw new RuntimeException("Apenas o autor pode excluir perguntas deste questionário");
        }

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Pergunta não encontrada"));

        if (!question.getQuiz().getId().equals(quizId)) {
            throw new RuntimeException("A pergunta não pertence a este questionário");
        }

        questionRepository.delete(question);
    }

    public List<QuestionResponseDTO> listQuestions(Long quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Questionário não encontrado"));

        List<Question> questions = questionRepository.findByQuizId(quizId);

        return questions.stream()
                .map(q -> new QuestionResponseDTO(
                        q.getId(),
                        q.getText(),
                        q.getType(),
                        q.getOptions(),
                        q.getCorrectAnswer(),
                        q.getScore()
                ))
                .collect(Collectors.toList());
    }
}
