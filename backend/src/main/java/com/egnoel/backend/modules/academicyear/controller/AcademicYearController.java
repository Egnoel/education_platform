package com.egnoel.backend.modules.academicyear.controller;


import com.egnoel.backend.modules.academicyear.dto.AcademicYearCreateDTO;
import com.egnoel.backend.modules.academicyear.dto.AcademicYearResponseDTO;
import com.egnoel.backend.modules.academicyear.dto.AcademicYearUpdateDTO;
import com.egnoel.backend.modules.academicyear.service.AcademicYearService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/academic-years")
public class AcademicYearController {
    private final AcademicYearService academicYearService;

    @Autowired
    public AcademicYearController(AcademicYearService academicYearService) {
        this.academicYearService = academicYearService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AcademicYearResponseDTO> createAcademicYear(@Valid @RequestBody AcademicYearCreateDTO dto) {
        return ResponseEntity.ok(academicYearService.createAcademicYear(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AcademicYearResponseDTO> updateAcademicYear(@PathVariable Long id,
                                                                      @Valid @RequestBody AcademicYearUpdateDTO dto) {
        return ResponseEntity.ok(academicYearService.updateAcademicYear(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteAcademicYear(@PathVariable Long id) {
        academicYearService.deleteAcademicYear(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ResponseEntity<List<AcademicYearResponseDTO>> listAcademicYears() {
        return ResponseEntity.ok(academicYearService.listAcademicYears());
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ResponseEntity<List<AcademicYearResponseDTO>> listActiveAcademicYears() {
        return ResponseEntity.ok(academicYearService.listActiveAcademicYears());
    }
}
