package com.egnoel.backend.modules.quiz.service;

import com.egnoel.backend.modules.auth.entity.Teacher;
import com.egnoel.backend.modules.auth.repository.TeacherRepository;
import com.egnoel.backend.modules.classe.entity.Classe;
import com.egnoel.backend.modules.classe.repository.ClasseRepository;
import com.egnoel.backend.modules.quiz.dto.QuizCreateDTO;
import com.egnoel.backend.modules.quiz.dto.QuizResponseDTO;
import com.egnoel.backend.modules.quiz.dto.QuizUpdateDTO;
import com.egnoel.backend.modules.quiz.entity.Quiz;
import com.egnoel.backend.modules.quiz.repository.QuizRepository;
import com.egnoel.backend.modules.subject.entity.Subject;
import com.egnoel.backend.modules.subject.repository.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuizService {
    private final QuizRepository quizRepository;
    private final TeacherRepository teacherRepository;
    private final SubjectRepository subjectRepository;
    private final ClasseRepository classeRepository;

    @Autowired
    public QuizService(QuizRepository quizRepository, TeacherRepository teacherRepository,
                       SubjectRepository subjectRepository, ClasseRepository classeRepository) {
        this.quizRepository = quizRepository;
        this.teacherRepository = teacherRepository;
        this.subjectRepository = subjectRepository;
        this.classeRepository = classeRepository;
    }

    @Transactional
    public QuizResponseDTO createQuiz(QuizCreateDTO dto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Teacher teacher = teacherRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Professor não encontrado"));

        Subject subject = subjectRepository.findById(dto.getSubjectId())
                .orElseThrow(() -> new RuntimeException("Disciplina não encontrada"));

        Classe classe = null;
        if (dto.getClasseId() != null) {
            classe = classeRepository.findById(dto.getClasseId())
                    .orElseThrow(() -> new RuntimeException("Turma não encontrada"));
            if (!classe.getSubject().getId().equals(dto.getSubjectId())) {
                throw new RuntimeException("A turma deve pertencer à disciplina especificada");
            }
        }

        Quiz quiz = new Quiz();
        quiz.setTitle(dto.getTitle());
        quiz.setTerminationDate(dto.getTerminationDate());
        quiz.setTeacher(teacher);
        quiz.setSubject(subject);
        quiz.setClasse(classe);

        quiz = quizRepository.save(quiz);

        return new QuizResponseDTO(
                quiz.getId(),
                quiz.getTitle(),
                quiz.getCreationDate(),
                quiz.getTerminationDate(),
                teacher.getFirstName(),
                subject.getName(),
                classe != null ? classe.getName() : null
        );
    }

    @Transactional
    public QuizResponseDTO updateQuiz(Long id, QuizUpdateDTO dto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Teacher teacher = teacherRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Professor não encontrado"));

        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Questionário não encontrado"));

        if (!quiz.getTeacher().getId().equals(teacher.getId())) {
            throw new RuntimeException("Apenas o autor pode editar este questionário");
        }

        quiz.setTitle(dto.getTitle());
        quiz.setTerminationDate(dto.getTerminationDate());

        quiz = quizRepository.save(quiz);

        return new QuizResponseDTO(
                quiz.getId(),
                quiz.getTitle(),
                quiz.getCreationDate(),
                quiz.getTerminationDate(),
                teacher.getFirstName(),
                quiz.getSubject().getName(),
                quiz.getClasse() != null ? quiz.getClasse().getName() : null
        );
    }

    @Transactional
    public void deleteQuiz(Long id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Teacher teacher = teacherRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Professor não encontrado"));

        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Questionário não encontrado"));

        if (!quiz.getTeacher().getId().equals(teacher.getId())) {
            throw new RuntimeException("Apenas o autor pode excluir este questionário");
        }

        quizRepository.delete(quiz);
    }

    public List<QuizResponseDTO> listQuizzes() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Quiz> quizzes;
        if (SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_TEACHER"))) {
            Teacher teacher = teacherRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Professor não encontrado"));
            quizzes = quizRepository.findByTeacherId(teacher.getId());
        } else {
            quizzes = quizRepository.findAll();
        }

        return quizzes.stream()
                .map(q -> new QuizResponseDTO(
                        q.getId(),
                        q.getTitle(),
                        q.getCreationDate(),
                        q.getTerminationDate(),
                        q.getTeacher().getFirstName(),
                        q.getSubject().getName(),
                        q.getClasse() != null ? q.getClasse().getName() : null
                ))
                .collect(Collectors.toList());
    }
}
