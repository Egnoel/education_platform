package com.egnoel.backend.modules.material.service;

import com.egnoel.backend.modules.auth.entity.Teacher;
import com.egnoel.backend.modules.auth.repository.TeacherRepository;
import com.egnoel.backend.modules.classe.entity.Classe;
import com.egnoel.backend.modules.classe.repository.ClasseRepository;
import com.egnoel.backend.modules.material.dto.MaterialCreateDTO;
import com.egnoel.backend.modules.material.dto.MaterialResponseDTO;
import com.egnoel.backend.modules.material.dto.MaterialUpdateDTO;
import com.egnoel.backend.modules.material.entity.Material;
import com.egnoel.backend.modules.material.repository.MaterialRepository;
import com.egnoel.backend.modules.subject.entity.Subject;
import com.egnoel.backend.modules.subject.repository.SubjectRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MaterialService {

    private final MaterialRepository materialRepository;
    private final TeacherRepository teacherRepository;
    private final SubjectRepository subjectRepository;
    private final ClasseRepository classeRepository;

    private static final String UPLOAD_DIR = "uploads/";

    @Autowired
    public MaterialService(MaterialRepository materialRepository, TeacherRepository teacherRepository,
                           SubjectRepository subjectRepository, ClasseRepository classeRepository) {
        this.materialRepository = materialRepository;
        this.teacherRepository = teacherRepository;
        this.subjectRepository = subjectRepository;
        this.classeRepository = classeRepository;
    }

    @Transactional
    public MaterialResponseDTO createMaterial(MaterialCreateDTO dto) {
        // Obtém o email do professor autenticado
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Teacher teacher = teacherRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Professor não encontrado"));

        Subject subject = subjectRepository.findById(dto.getSubjectId())
                .orElseThrow(() -> new RuntimeException("Disciplina não encontrada"));

        Classe classe = null;
        if (dto.getClasseId() != null) {
            classe = classeRepository.findById(dto.getClasseId())
                    .orElseThrow(() -> new RuntimeException("Turma não encontrada"));
        }

        String fileName = dto.getFile().getOriginalFilename();
        String filePath = UPLOAD_DIR + System.currentTimeMillis() + "_" + fileName; // Evita sobrescrita
        try {
            File directory = new File(UPLOAD_DIR);
            if (!directory.exists()) {
                directory.mkdirs(); // Cria o diretório se não existir
            }
            dto.getFile().transferTo(new File(filePath));
        } catch (Exception e) {
            throw new RuntimeException("Erro ao salvar o ficheiro: " + e.getMessage());
        }

        Material material = new Material();
        material.setTitle(dto.getTitle());
        material.setFilePath(filePath);
        material.setTeacher(teacher);
        material.setSubject(subject);
        material.setClasse(classe);

        material = materialRepository.save(material);

        return new MaterialResponseDTO(
                material.getId(),
                material.getTitle(),
                material.getFilePath(),
                material.getUploadDate(),
                teacher.getFirstName(),
                subject.getName(),
                classe != null ? classe.getName() : null
        );
    }

    @Transactional
    public MaterialResponseDTO updateMaterial(Long id, MaterialUpdateDTO dto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Teacher teacher = teacherRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Professor não encontrado"));

        Material material = materialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Material não encontrado"));

        // Verifica se o material pertence ao professor autenticado
        if (!material.getTeacher().getId().equals(teacher.getId())) {
            throw new RuntimeException("Apenas o autor pode editar este material");
        }

        // Atualiza o título
        material.setTitle(dto.getTitle());

        // Atualiza o ficheiro, se fornecido
        if (dto.getFile() != null && !dto.getFile().isEmpty()) {
            // Remove o ficheiro antigo
            File oldFile = new File(material.getFilePath());
            if (oldFile.exists()) {
                oldFile.delete();
            }

            // Salva o novo ficheiro
            String fileName = dto.getFile().getOriginalFilename();
            String newFilePath = UPLOAD_DIR + System.currentTimeMillis() + "_" + fileName;
            try {
                dto.getFile().transferTo(new File(newFilePath));
                material.setFilePath(newFilePath);
            } catch (Exception e) {
                throw new RuntimeException("Erro ao atualizar o ficheiro: " + e.getMessage());
            }
        }

        material = materialRepository.save(material);

        return new MaterialResponseDTO(
                material.getId(),
                material.getTitle(),
                material.getFilePath(),
                material.getUploadDate(),
                teacher.getFirstName(),
                material.getSubject().getName(),
                material.getClasse() != null ? material.getClasse().getName() : null
        );
    }

    @Transactional
    public void deleteMaterial(Long id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Teacher teacher = teacherRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Professor não encontrado"));

        Material material = materialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Material não encontrado"));

        // Verifica se o material pertence ao professor autenticado
        if (!material.getTeacher().getId().equals(teacher.getId())) {
            throw new RuntimeException("Apenas o autor pode excluir este material");
        }

        // Remove o ficheiro do sistema
        File file = new File(material.getFilePath());
        if (file.exists()) {
            file.delete();
        }

        materialRepository.delete(material);
    }


    public List<MaterialResponseDTO> listMaterials() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        // Para professores, listar apenas os seus materiais; para alunos, listar todos os disponíveis
        List<Material> materials;
        if (SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_TEACHER"))) {
            Teacher teacher = teacherRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Professor não encontrado"));
            materials = materialRepository.findByTeacherId(teacher.getId());
        } else {
            materials = materialRepository.findAll();
        }

        return materials.stream()
                .map(m -> new MaterialResponseDTO(
                        m.getId(),
                        m.getTitle(),
                        m.getFilePath(),
                        m.getUploadDate(),
                        m.getTeacher().getFirstName(),
                        m.getSubject().getName(),
                        m.getClasse() != null ? m.getClasse().getName() : null
                ))
                .collect(Collectors.toList());
    }
}
