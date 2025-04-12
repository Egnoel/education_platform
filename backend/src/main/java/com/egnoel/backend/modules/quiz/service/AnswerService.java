package com.egnoel.backend.modules.quiz.service;

import com.egnoel.backend.modules.auth.entity.Student;
import com.egnoel.backend.modules.auth.repository.StudentRepository;
import com.egnoel.backend.modules.quiz.dto.AnswerCreateDTO;
import com.egnoel.backend.modules.quiz.dto.AnswerResponseDTO;
import com.egnoel.backend.modules.quiz.dto.UpdateAnswerScoreDTO;
import com.egnoel.backend.modules.quiz.entity.Answer;
import com.egnoel.backend.modules.quiz.entity.Question;
import com.egnoel.backend.modules.quiz.entity.QuestionType;
import com.egnoel.backend.modules.quiz.entity.Quiz;
import com.egnoel.backend.modules.quiz.repository.AnswerRepository;
import com.egnoel.backend.modules.quiz.repository.QuestionRepository;
import com.egnoel.backend.modules.quiz.repository.QuizRepository;
import com.egnoel.backend.modules.subject.entity.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AnswerService {
    private final AnswerRepository answerRepository;
    private final QuizRepository quizRepository;
    private final StudentRepository studentRepository;
    private final QuestionRepository questionRepository;

    @Autowired
    public AnswerService(AnswerRepository answerRepository, QuizRepository quizRepository,
                         StudentRepository studentRepository, QuestionRepository questionRepository) {
        this.answerRepository = answerRepository;
        this.quizRepository = quizRepository;
        this.studentRepository = studentRepository;
        this.questionRepository = questionRepository;
    }

    @Transactional
    public AnswerResponseDTO submitAnswer(Long quizId, AnswerCreateDTO dto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Student student = studentRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado"));

        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Questionário não encontrado"));

        // Verifica se o prazo expirou
        if (quiz.getTerminationDate() != null && quiz.getTerminationDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("O prazo para submissão deste questionário expirou");
        }

        // Verifica se o aluno está na disciplina ou na turma (se especificada)
        Subject subject = quiz.getSubject();
        boolean isStudentInSubject = student.getClasses().stream()
                .anyMatch(classe -> classe.getSubject().getId().equals(subject.getId()));
        if (!isStudentInSubject) {
            throw new RuntimeException("O aluno não está inscrito na disciplina deste questionário");
        }

        if (quiz.getClasse() != null) {
            boolean isStudentInClasse = student.getClasses().stream()
                    .anyMatch(classe -> classe.getId().equals(quiz.getClasse().getId()));
            if (!isStudentInClasse) {
                throw new RuntimeException("O aluno não está na turma deste questionário");
            }
        }

        // Verifica se o aluno já submeteu
        List<Answer> existingAnswers = answerRepository.findByQuizId(quizId);
        if (existingAnswers.stream().anyMatch(a -> a.getStudent().getId().equals(student.getId()))) {
            throw new RuntimeException("O aluno já submeteu uma resposta para este questionário");
        }

        // Calcular pontuação
        int totalScore = 0;
        List<Question> questions = questionRepository.findByQuizId(quizId);
        StringBuilder answersString = new StringBuilder();

        for (AnswerCreateDTO.QuestionAnswerDTO answerDTO : dto.getAnswers()) {
            Question question = questions.stream()
                    .filter(q -> q.getId().equals(answerDTO.getQuestionId()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Pergunta não encontrada: " + answerDTO.getQuestionId()));

            answersString.append("Q").append(question.getId()).append(": ").append(answerDTO.getAnswer()).append("; ");

            // Correção automática para MULTIPLA_ESCOLHA e VERDADEIRO_FALSO
            if (question.getType() != QuestionType.DISCURSIVA && question.getCorrectAnswer() != null) {
                if (question.getCorrectAnswer().equalsIgnoreCase(answerDTO.getAnswer())) {
                    totalScore += question.getScore();
                }
            }
        }

        Answer answer = new Answer();
        answer.setQuiz(quiz);
        answer.setStudent(student);
        answer.setAnswers(answersString.toString());
        answer.setScore(totalScore); // Define a pontuação calculada

        answer = answerRepository.save(answer);

        return new AnswerResponseDTO(
                answer.getId(),
                answer.getAnswers(),
                answer.getScore(),
                answer.getSubmittedAt(),
                student.getFirstName() + " " + student.getLastName()
        );
    }

    @Transactional
    public AnswerResponseDTO updateAnswerScore(Long quizId, Long answerId, UpdateAnswerScoreDTO dto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Questionário não encontrado"));

        if (!SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_TEACHER"))) {
            throw new RuntimeException("Apenas professores podem atualizar pontuações");
        }

        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new RuntimeException("Resposta não encontrada"));

        if (!answer.getQuiz().getId().equals(quizId)) {
            throw new RuntimeException("A resposta não pertence a este questionário");
        }

        answer.setScore(dto.getScore());
        answer = answerRepository.save(answer);

        return new AnswerResponseDTO(
                answer.getId(),
                answer.getAnswers(),
                answer.getScore(),
                answer.getSubmittedAt(),
                answer.getStudent().getFirstName() + " " + answer.getStudent().getLastName()
        );
    }

    public List<AnswerResponseDTO> listAnswers(Long quizId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Questionário não encontrado"));

        // Apenas professores podem listar respostas
        if (!SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_TEACHER"))) {
            throw new RuntimeException("Apenas professores podem listar respostas");
        }

        List<Answer> answers = answerRepository.findByQuizId(quizId);

        return answers.stream()
                .map(a -> new AnswerResponseDTO(
                        a.getId(),
                        a.getAnswers(),
                        a.getScore(),
                        a.getSubmittedAt(),
                        a.getStudent().getFirstName()
                ))
                .collect(Collectors.toList());
    }
}
