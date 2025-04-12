package com.egnoel.backend.modules.dashboard.service;


import com.egnoel.backend.modules.assessment.entity.Assessment;
import com.egnoel.backend.modules.assessment.repository.AssessmentRepository;
import com.egnoel.backend.modules.auth.entity.Student;
import com.egnoel.backend.modules.auth.entity.Teacher;
import com.egnoel.backend.modules.auth.entity.User;
import com.egnoel.backend.modules.auth.repository.UserRepository;
import com.egnoel.backend.modules.classe.entity.Classe;
import com.egnoel.backend.modules.classe.repository.ClasseRepository;
import com.egnoel.backend.modules.dashboard.dto.AssessmentDTO;
import com.egnoel.backend.modules.dashboard.dto.ClasseDTO;
import com.egnoel.backend.modules.dashboard.dto.DashboardResponseDTO;
import com.egnoel.backend.modules.dashboard.dto.MaterialDTO;
import com.egnoel.backend.modules.dashboard.dto.QuizDTO;
import com.egnoel.backend.modules.material.entity.Material;
import com.egnoel.backend.modules.material.repository.MaterialRepository;
import com.egnoel.backend.modules.quiz.entity.Quiz;
import com.egnoel.backend.modules.quiz.repository.QuizRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DashboardService {
    private final UserRepository userRepository;
    private final ClasseRepository classeRepository;
    private final MaterialRepository materialRepository;
    private final QuizRepository quizRepository;
    private final AssessmentRepository assessmentRepository;

    @Autowired
    public DashboardService(UserRepository userRepository,
                            ClasseRepository classeRepository,
                            MaterialRepository materialRepository,
                            QuizRepository quizRepository,
                            AssessmentRepository assessmentRepository) {
        this.userRepository = userRepository;
        this.classeRepository = classeRepository;
        this.materialRepository = materialRepository;
        this.quizRepository = quizRepository;
        this.assessmentRepository = assessmentRepository;
    }

    public DashboardResponseDTO getDashboard() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilizador não encontrado"));

        String role = user instanceof Teacher ? "TEACHER" : user instanceof Student ? "STUDENT" : "UNKNOWN";
        String userName = user.getFirstName() + " " + user.getLastName();

        if (role.equals("TEACHER")) {
            return getTeacherDashboard((Teacher) user, userName);
        } else if (role.equals("STUDENT")) {
            return getStudentDashboard((Student) user, userName);
        } else {
            throw new RuntimeException("Papel de utilizador não suportado");
        }
    }

    private DashboardResponseDTO getTeacherDashboard(Teacher teacher, String userName) {
        // Turmas lecionadas
        List<Classe> classes = classeRepository.findByTeacherId(teacher.getId());
        List<ClasseDTO> classeDTOs = classes.stream()
                .map(c -> new ClasseDTO(
                        c.getId(),
                        c.getName(),
                        c.getSubject().getName(),
                        c.getAcademicYear().getName(),
                        c.getCreationDate()
                ))
                .limit(5) // Limitar para evitar respostas pesadas
                .collect(Collectors.toList());

        // Materiais criados
        List<Material> materials = materialRepository.findByTeacherId(teacher.getId());
        List<MaterialDTO> materialDTOs = materials.stream()
                .map(m -> new MaterialDTO(
                        m.getId(),
                        m.getTitle(),
                        m.getClasse() != null ? m.getClasse().getName() : null,
                        m.getSubject().getName(),
                        m.getUploadDate()
                ))
                .limit(5)
                .collect(Collectors.toList());

        // Quizzes criados
        List<Quiz> quizzes = quizRepository.findByTeacherId(teacher.getId());
        List<QuizDTO> quizDTOs = quizzes.stream()
                .map(q -> new QuizDTO(
                        q.getId(),
                        q.getTitle(),
                        q.getClasse().getName(),
                        q.getCreationDate(),
                        false // Simplificação: lógica de respostas pendentes a implementar
                ))
                .limit(5)
                .collect(Collectors.toList());

        // Avaliações recentes
        List<Assessment> assessments = assessmentRepository.findByClasseIdIn(
                classes.stream().map(Classe::getId).collect(Collectors.toList())
        );
        List<AssessmentDTO> assessmentDTOs = assessments.stream()
                .map(a -> new AssessmentDTO(
                        a.getId(),
                        a.getTitle(),
                        a.getGrade(),
                        a.getClasse().getName(),
                        a.getStudent().getFirstName() + " " + a.getStudent().getLastName(),
                        a.getDate()
                ))
                .limit(5)
                .collect(Collectors.toList());

        return new DashboardResponseDTO(
                userName,
                "TEACHER",
                classeDTOs,
                materialDTOs,
                quizDTOs,
                assessmentDTOs
        );
    }

    private DashboardResponseDTO getStudentDashboard(Student student, String userName) {
        // Turmas em que o aluno está inscrito
        List<Classe> classes = classeRepository.findByStudentId(student.getId());
        List<ClasseDTO> classeDTOs = classes.stream()
                .map(c -> new ClasseDTO(
                        c.getId(),
                        c.getName(),
                        c.getSubject().getName(),
                        c.getAcademicYear().getName(),
                        c.getCreationDate()
                ))
                .limit(5)
                .collect(Collectors.toList());

        // IDs das turmas
        List<Long> classeIds = classes.stream().map(Classe::getId).collect(Collectors.toList());

        // Materiais disponíveis nas turmas
        List<Material> materials = materialRepository.findByClasseIdIn(classeIds);
        List<MaterialDTO> materialDTOs = materials.stream()
                .map(m -> new MaterialDTO(
                        m.getId(),
                        m.getTitle(),
                        m.getClasse() != null ? m.getClasse().getName() : null,
                        m.getSubject().getName(),
                        m.getUploadDate()
                ))
                .limit(5)
                .collect(Collectors.toList());

        // Quizzes pendentes
        List<Quiz> quizzes = quizRepository.findByClasseIdIn(classeIds);
        List<QuizDTO> quizDTOs = quizzes.stream()
                .map(q -> new QuizDTO(
                        q.getId(),
                        q.getTitle(),
                        q.getClasse().getName(),
                        q.getCreationDate(),
                        true // Simplificação: assumir pendente até lógica de respostas
                ))
                .limit(5)
                .collect(Collectors.toList());

        // Avaliações do aluno
        List<Assessment> assessments = assessmentRepository.findByStudentId(student.getId());
        List<AssessmentDTO> assessmentDTOs = assessments.stream()
                .map(a -> new AssessmentDTO(
                        a.getId(),
                        a.getTitle(),
                        a.getGrade(),
                        a.getClasse().getName(),
                        null, // Não incluir studentName para aluno
                        a.getDate()
                ))
                .limit(5)
                .collect(Collectors.toList());

        return new DashboardResponseDTO(
                userName,
                "STUDENT",
                classeDTOs,
                materialDTOs,
                quizDTOs,
                assessmentDTOs
        );
    }
}
