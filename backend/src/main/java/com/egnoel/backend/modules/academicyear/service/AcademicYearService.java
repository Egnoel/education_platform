package com.egnoel.backend.modules.academicyear.service;

import com.egnoel.backend.modules.academicyear.dto.AcademicYearCreateDTO;
import com.egnoel.backend.modules.academicyear.dto.AcademicYearResponseDTO;
import com.egnoel.backend.modules.academicyear.dto.AcademicYearUpdateDTO;
import com.egnoel.backend.modules.academicyear.entity.AcademicYear;
import com.egnoel.backend.modules.academicyear.repository.AcademicYearRepository;
import com.egnoel.backend.modules.institution.entity.Institution;
import com.egnoel.backend.modules.institution.repository.InstitutionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AcademicYearService {
    private final AcademicYearRepository academicYearRepository;
    private final InstitutionRepository institutionRepository;

    @Autowired
    public AcademicYearService(AcademicYearRepository academicYearRepository,
                               InstitutionRepository institutionRepository) {
        this.academicYearRepository = academicYearRepository;
        this.institutionRepository = institutionRepository;
    }

    @Transactional
    public AcademicYearResponseDTO createAcademicYear(AcademicYearCreateDTO dto) {
        validateDates(dto.getStartDate(), dto.getEndDate());

        Institution institution = institutionRepository.findById(dto.getInstitutionId())
                .orElseThrow(() -> new RuntimeException("Instituição não encontrada"));

        AcademicYear academicYear = new AcademicYear();
        academicYear.setName(dto.getName());
        academicYear.setStartDate(dto.getStartDate());
        academicYear.setEndDate(dto.getEndDate());
        academicYear.setInstitution(institution);

        academicYear = academicYearRepository.save(academicYear);

        return new AcademicYearResponseDTO(
                academicYear.getId(),
                academicYear.getName(),
                academicYear.getStartDate(),
                academicYear.getEndDate(),
                academicYear.isActive(),
                academicYear.getCreationDate(),
                institution.getName()
        );
    }

    @Transactional
    public AcademicYearResponseDTO updateAcademicYear(Long id, AcademicYearUpdateDTO dto) {
        validateDates(dto.getStartDate(), dto.getEndDate());

        AcademicYear academicYear = academicYearRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ano letivo não encontrado"));

        academicYear.setName(dto.getName());
        academicYear.setStartDate(dto.getStartDate());
        academicYear.setEndDate(dto.getEndDate());
        academicYear.setActive(dto.getActive());

        academicYear = academicYearRepository.save(academicYear);

        return new AcademicYearResponseDTO(
                academicYear.getId(),
                academicYear.getName(),
                academicYear.getStartDate(),
                academicYear.getEndDate(),
                academicYear.isActive(),
                academicYear.getCreationDate(),
                academicYear.getInstitution().getName()
        );
    }

    @Transactional
    public void deleteAcademicYear(Long id) {
        AcademicYear academicYear = academicYearRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ano letivo não encontrado"));

        if (!academicYear.getClasses().isEmpty()) {
            throw new RuntimeException("Não é possível excluir um ano letivo com turmas associadas");
        }

        academicYearRepository.delete(academicYear);
    }

    public List<AcademicYearResponseDTO> listAcademicYears() {
        return academicYearRepository.findAll().stream()
                .map(ay -> new AcademicYearResponseDTO(
                        ay.getId(),
                        ay.getName(),
                        ay.getStartDate(),
                        ay.getEndDate(),
                        ay.isActive(),
                        ay.getCreationDate(),
                        ay.getInstitution().getName()
                ))
                .collect(Collectors.toList());
    }

    public List<AcademicYearResponseDTO> listActiveAcademicYears() {
        return academicYearRepository.findByActiveTrue().stream()
                .map(ay -> new AcademicYearResponseDTO(
                        ay.getId(),
                        ay.getName(),
                        ay.getStartDate(),
                        ay.getEndDate(),
                        ay.isActive(),
                        ay.getCreationDate(),
                        ay.getInstitution().getName()
                ))
                .collect(Collectors.toList());
    }

    private void validateDates(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new RuntimeException("A data de início deve ser anterior à data de fim");
        }
    }
}
