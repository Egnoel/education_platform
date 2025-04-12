package com.egnoel.backend.modules.institution.service;

import com.egnoel.backend.modules.academicyear.entity.AcademicYear;
import com.egnoel.backend.modules.auth.entity.User;
import com.egnoel.backend.modules.institution.dto.InstitutionCreateDTO;
import com.egnoel.backend.modules.institution.dto.InstitutionResponseDTO;
import com.egnoel.backend.modules.institution.dto.InstitutionUpdateDTO;
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

    @Autowired
    public InstitutionService(InstitutionRepository institutionRepository) {
        this.institutionRepository = institutionRepository;
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
                institution.getUsers().stream().map(User::getFirstName).collect(Collectors.toList()),
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
                        i.getUsers().stream().map(User::getFirstName).collect(Collectors.toList()),
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
                institution.getUsers().stream().map(User::getFirstName).collect(Collectors.toList()),
                institution.getAcademicYears().stream().map(AcademicYear::getName).collect(Collectors.toList())
        );
    }
}
