package com.egnoel.backend.modules.institution.service;

import com.egnoel.backend.modules.academicyear.entity.AcademicYear;
import com.egnoel.backend.modules.auth.entity.Student;
import com.egnoel.backend.modules.auth.entity.Teacher;
import com.egnoel.backend.modules.auth.entity.User;
import com.egnoel.backend.modules.auth.repository.UserRepository;
import com.egnoel.backend.modules.classe.entity.Classe;
import com.egnoel.backend.modules.classe.repository.ClasseRepository;
import com.egnoel.backend.modules.institution.dto.*;
import com.egnoel.backend.modules.institution.entity.Institution;
import com.egnoel.backend.modules.institution.repository.InstitutionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class InstitutionService {
    private final InstitutionRepository institutionRepository;
    private final UserRepository userRepository;
    private final ClasseRepository classeRepository;

    @Autowired
    public InstitutionService(InstitutionRepository institutionRepository, UserRepository userRepository,
                              ClasseRepository classeRepository) {
        this.institutionRepository = institutionRepository;
        this.userRepository = userRepository;
        this.classeRepository = classeRepository;
    }

    @Transactional
    public InstitutionResponseDTO createInstitution(InstitutionCreateDTO dto) {
        Institution institution = new Institution();
        institution.setName(dto.getName());
        institution.setLocation(dto.getLocation());
        institution.setContact(dto.getContact());

        institution = institutionRepository.save(institution);

        return new InstitutionResponseDTO(
                institution.getId(),
                institution.getName(),
                institution.getLocation(),
                institution.getContact(),
                institution.getCreationDate(),
                List.of(),
                List.of()
        );
    }

    @Transactional
    public InstitutionResponseDTO updateInstitution(Long id, InstitutionUpdateDTO dto) {
        Institution institution = institutionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Instituição não encontrada"));

        institution.setName(dto.getName());
        institution.setLocation(dto.getLocation());
        institution.setContact(dto.getContact());

        institution = institutionRepository.save(institution);

        return new InstitutionResponseDTO(
                institution.getId(),
                institution.getName(),
                institution.getLocation(),
                institution.getContact(),
                institution.getCreationDate(),
                institution.getUsers().stream().map(u -> u.getFirstName() + " " + u.getLastName()).collect(Collectors.toList()),
                institution.getAcademicYears().stream().map(AcademicYear::getName).collect(Collectors.toList())
        );
    }

    @Transactional
    public void deleteInstitution(Long id) {
        Institution institution = institutionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Instituição não encontrada"));

        if (!institution.getUsers().isEmpty() || !institution.getAcademicYears().isEmpty()) {
            throw new RuntimeException("Não é possível excluir uma instituição com utilizadores ou anos letivos associados");
        }

        institutionRepository.delete(institution);
    }

    public List<InstitutionResponseDTO> listInstitutions() {
        return institutionRepository.findAll().stream()
                .map(i -> new InstitutionResponseDTO(
                        i.getId(),
                        i.getName(),
                        i.getLocation(),
                        i.getContact(),
                        i.getCreationDate(),
                        i.getUsers().stream().map(u -> u.getFirstName() + " " + u.getLastName()).collect(Collectors.toList()),
                        i.getAcademicYears().stream().map(AcademicYear::getName).collect(Collectors.toList())
                ))
                .collect(Collectors.toList());
    }

    public InstitutionResponseDTO getInstitution(Long id) {
        Institution institution = institutionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Instituição não encontrada"));

        return new InstitutionResponseDTO(
                institution.getId(),
                institution.getName(),
                institution.getLocation(),
                institution.getContact(),
                institution.getCreationDate(),
                institution.getUsers().stream().map(u -> u.getFirstName() + " " + u.getLastName()).collect(Collectors.toList()),
                institution.getAcademicYears().stream().map(AcademicYear::getName).collect(Collectors.toList())
        );
    }

    @Transactional
    public InstitutionResponseDTO addUsersToInstitution(Long institutionId, AddUsersToInstitutionDTO dto) {
        Institution institution = institutionRepository.findById(institutionId)
                .orElseThrow(() -> new RuntimeException("Instituição não encontrada"));

        List<User> users = userRepository.findAllById(dto.getUserIds());
        if (users.size() != dto.getUserIds().size()) {
            throw new RuntimeException("Um ou mais utilizadores não foram encontrados");
        }

        for (User user : users) {
            if (user.getInstitution() != null && !user.getInstitution().getId().equals(institutionId)) {
                throw new RuntimeException("O utilizador " + user.getFirstName() + " " + user.getLastName() + " já está associado a outra instituição");
            }
            user.setInstitution(institution);
        }

        userRepository.saveAll(users);

        return new InstitutionResponseDTO(
                institution.getId(),
                institution.getName(),
                institution.getLocation(),
                institution.getContact(),
                institution.getCreationDate(),
                institution.getUsers().stream().map(u -> u.getFirstName() + " " + u.getLastName()).collect(Collectors.toList()),
                institution.getAcademicYears().stream().map(AcademicYear::getName).collect(Collectors.toList())
        );
    }

    @Transactional
    public InstitutionResponseDTO removeUsersFromInstitution(Long institutionId, AddUsersToInstitutionDTO dto) {
        Institution institution = institutionRepository.findById(institutionId)
                .orElseThrow(() -> new RuntimeException("Instituição não encontrada"));

        List<User> users = userRepository.findAllById(dto.getUserIds());
        if (users.size() != dto.getUserIds().size()) {
            throw new RuntimeException("Um ou mais utilizadores não foram encontrados");
        }

        for (User user : users) {
            if (user.getInstitution() == null || !user.getInstitution().getId().equals(institutionId)) {
                throw new RuntimeException("O utilizador " + user.getFirstName() + " " + user.getLastName() + " não está associado a esta instituição");
            }

            // Verificar dependências para professores
            if (user instanceof Teacher) {
                List<Classe> classes = classeRepository.findByTeacherId(user.getId());
                if (!classes.isEmpty()) {
                    throw new RuntimeException("O professor " + user.getFirstName() + " " + user.getLastName() + " tem turmas associadas e não pode ser removido");
                }
            }

            user.setInstitution(null);
        }

        userRepository.saveAll(users);

        return new InstitutionResponseDTO(
                institution.getId(),
                institution.getName(),
                institution.getLocation(),
                institution.getContact(),
                institution.getCreationDate(),
                institution.getUsers().stream().map(u -> u.getFirstName() + " " + u.getLastName()).collect(Collectors.toList()),
                institution.getAcademicYears().stream().map(AcademicYear::getName).collect(Collectors.toList())
        );
    }

    public List<UserResponseDTO> listInstitutionUsers(Long institutionId) {
        Institution institution = institutionRepository.findById(institutionId)
                .orElseThrow(() -> new RuntimeException("Instituição não encontrada"));

        return institution.getUsers().stream()
                .map(u -> new UserResponseDTO(
                        u.getId(),
                        u.getFirstName() + " " + u.getLastName(),
                        u.getEmail(),
                        u instanceof Teacher ? "TEACHER" : u instanceof Student ? "STUDENT" : "UNKNOWN"
                ))
                .collect(Collectors.toList());
    }
}
