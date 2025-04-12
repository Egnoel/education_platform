package com.egnoel.backend.modules.assessment.service;

import com.egnoel.backend.modules.assessment.dto.AssessmentCreateDTO;
import com.egnoel.backend.modules.assessment.dto.AssessmentResponseDTO;
import com.egnoel.backend.modules.assessment.dto.AssessmentUpdateDTO;
import com.egnoel.backend.modules.assessment.entity.Assessment;
import com.egnoel.backend.modules.assessment.repository.AssessmentRepository;
import com.egnoel.backend.modules.auth.entity.Student;
import com.egnoel.backend.modules.auth.entity.Teacher;
import com.egnoel.backend.modules.auth.entity.User;
import com.egnoel.backend.modules.auth.repository.UserRepository;
import com.egnoel.backend.modules.classe.entity.Classe;
import com.egnoel.backend.modules.classe.repository.ClasseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AssessmentService {
    private final AssessmentRepository assessmentRepository;
    private final UserRepository userRepository;
    private final ClasseRepository classeRepository;

    @Autowired
    public AssessmentService(AssessmentRepository assessmentRepository,
                             UserRepository userRepository,
                             ClasseRepository classeRepository) {
        this.assessmentRepository = assessmentRepository;
        this.userRepository = userRepository;
        this.classeRepository = classeRepository;
    }

    @Transactional
    public AssessmentResponseDTO createAssessment(AssessmentCreateDTO dto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User teacher =  userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilizador não encontrado"));

        if (!(teacher instanceof Teacher)) {
            throw new RuntimeException("Apenas professores podem criar avaliações");
        }

        Student student = (Student) userRepository.findById(dto.getStudentId())
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado"));

        Classe classe = classeRepository.findById(dto.getClasseId())
                .orElseThrow(() -> new RuntimeException("Turma não encontrada"));

        // Validar se o aluno pertence à turma (supondo que existe uma relação em Classe)
        // Exemplo: classe.getStudents().contains(student)
        // Como não temos essa relação explícita, assumimos válida por enquanto

        // Validar se o professor leciona a turma
        if (!classe.getTeacher().getId().equals(teacher.getId())) {
            throw new RuntimeException("O professor não leciona esta turma");
        }

        // Validar se a instituição do professor, aluno e turma coincidem
        if (!teacher.getInstitution().getId().equals(student.getInstitution().getId()) ||
                !teacher.getInstitution().getId().equals(classe.getAcademicYear().getInstitution().getId())) {
            throw new RuntimeException("Instituições não coincidem");
        }

        Assessment assessment = new Assessment();
        assessment.setTitle(dto.getTitle());
        assessment.setGrade(dto.getGrade());
        assessment.setDate(dto.getDate());
        assessment.setStudent(student);
        assessment.setClasse(classe);

        assessment = assessmentRepository.save(assessment);

        return new AssessmentResponseDTO(
                assessment.getId(),
                assessment.getTitle(),
                assessment.getGrade(),
                assessment.getDate(),
                assessment.getCreationDate(),
                student.getFirstName() + " " + student.getLastName(),
                classe.getName()
        );
    }

    @Transactional
    public AssessmentResponseDTO updateAssessment(Long id, AssessmentUpdateDTO dto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User teacher =  userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilizador não encontrado"));

        if (!(teacher instanceof Teacher)) {
            throw new RuntimeException("Apenas professores podem atualizar avaliações");
        }

        Assessment assessment = assessmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Avaliação não encontrada"));

        // Validar se o professor leciona a turma associada
        if (!assessment.getClasse().getTeacher().getId().equals(teacher.getId())) {
            throw new RuntimeException("O professor não leciona esta turma");
        }

        assessment.setTitle(dto.getTitle());
        assessment.setGrade(dto.getGrade());
        assessment.setDate(dto.getDate());

        assessment = assessmentRepository.save(assessment);

        return new AssessmentResponseDTO(
                assessment.getId(),
                assessment.getTitle(),
                assessment.getGrade(),
                assessment.getDate(),
                assessment.getCreationDate(),
                assessment.getStudent().getFirstName() + " " + assessment.getStudent().getLastName(),
                assessment.getClasse().getName()
        );
    }

    @Transactional
    public void deleteAssessment(Long id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User teacher =  userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilizador não encontrado"));

        if (!(teacher instanceof Teacher)) {
            throw new RuntimeException("Apenas professores podem excluir avaliações");
        }

        Assessment assessment = assessmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Avaliação não encontrada"));

        // Validar se o professor leciona a turma associada
        if (!assessment.getClasse().getTeacher().getId().equals(teacher.getId())) {
            throw new RuntimeException("O professor não leciona esta turma");
        }

        assessmentRepository.delete(assessment);
    }

    public List<AssessmentResponseDTO> listAssessmentsByStudent(Long studentId) {
        Student student = (Student) userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado"));

        return assessmentRepository.findByStudentId(studentId).stream()
                .map(a -> new AssessmentResponseDTO(
                        a.getId(),
                        a.getTitle(),
                        a.getGrade(),
                        a.getDate(),
                        a.getCreationDate(),
                        student.getFirstName() + " " + student.getLastName(),
                        a.getClasse().getName()
                ))
                .collect(Collectors.toList());
    }

    public List<AssessmentResponseDTO> listAssessmentsByClasse(Long classeId) {
        Classe classe = classeRepository.findById(classeId)
                .orElseThrow(() -> new RuntimeException("Turma não encontrada"));

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user =  userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilizador não encontrado"));

        // Apenas professores da turma ou alunos inscritos podem listar
        if (user instanceof Teacher) {
            if (!classe.getTeacher().getId().equals(user.getId())) {
                throw new RuntimeException("O professor não leciona esta turma");
            }
        } else if (user instanceof Student) {
            // Supondo validação de inscrição na turma, a implementar conforme necessário
        } else {
            throw new RuntimeException("Acesso não autorizado");
        }

        return assessmentRepository.findByClasseId(classeId).stream()
                .map(a -> new AssessmentResponseDTO(
                        a.getId(),
                        a.getTitle(),
                        a.getGrade(),
                        a.getDate(),
                        a.getCreationDate(),
                        a.getStudent().getFirstName() + " " + a.getStudent().getLastName(),
                        classe.getName()
                ))
                .collect(Collectors.toList());
    }
}
