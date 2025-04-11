package com.egnoel.backend.modules.subject.service;

import com.egnoel.backend.modules.institution.entity.Institution;
import com.egnoel.backend.modules.institution.repository.InstitutionRepository;
import com.egnoel.backend.modules.subject.dto.SubjectCreateDTO;
import com.egnoel.backend.modules.subject.dto.SubjectResponseDTO;
import com.egnoel.backend.modules.subject.dto.SubjectUpdateDTO;
import com.egnoel.backend.modules.subject.entity.Subject;
import com.egnoel.backend.modules.subject.repository.SubjectRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SubjectService {

    private final SubjectRepository subjectRepository;
    private final InstitutionRepository institutionRepository;

    @Autowired
    public SubjectService(SubjectRepository subjectRepository, InstitutionRepository institutionRepository) {
        this.subjectRepository = subjectRepository;
        this.institutionRepository = institutionRepository;
    }

    @Transactional
    public SubjectResponseDTO createSubject(SubjectCreateDTO dto) {
        // Verifica se o utilizador é um professor ou admin (ajustar conforme necessário)
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        Institution institution = institutionRepository.findById(dto.getInstitutionId())
                .orElseThrow(() -> new RuntimeException("Instituição não encontrada"));

        Subject subject = new Subject();
        subject.setName(dto.getName());
        subject.setCode(dto.getCode());
        subject.setInstitution(institution);

        subject = subjectRepository.save(subject);

        return new SubjectResponseDTO(
                subject.getId(),
                subject.getName(),
                subject.getCode(),
                institution.getName()
        );
    }

    public List<SubjectResponseDTO> listSubjects() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        // Para simplificar, listar todas as disciplinas (ajustar permissões depois)
        List<Subject> subjects = subjectRepository.findAll();

        return subjects.stream()
                .map(s -> new SubjectResponseDTO(
                        s.getId(),
                        s.getName(),
                        s.getCode(),
                        s.getInstitution().getName()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public SubjectResponseDTO updateSubject(Long id, SubjectUpdateDTO dto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Disciplina não encontrada"));

        // Atualiza apenas os campos fornecidos
        subject.setName(dto.getName());
        if (dto.getCode() != null) { // Permite manter o código atual se não for fornecido
            subject.setCode(dto.getCode());
        }

        subject = subjectRepository.save(subject);

        return new SubjectResponseDTO(
                subject.getId(),
                subject.getName(),
                subject.getCode(),
                subject.getInstitution().getName()
        );
    }

    @Transactional
    public void deleteSubject(Long id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Disciplina não encontrada"));

        // Verifica se há dependências antes de excluir (opcional)
        if (!subject.getClasses().isEmpty() || !subject.getMaterials().isEmpty() || !subject.getQuizzes().isEmpty()) {
            throw new RuntimeException("Não é possível excluir a disciplina: existem turmas, materiais ou questionários associados.");
        }

        subjectRepository.delete(subject);
    }
}

