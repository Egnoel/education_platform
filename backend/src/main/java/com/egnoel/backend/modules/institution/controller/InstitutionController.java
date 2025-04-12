package com.egnoel.backend.modules.institution.controller;

import com.egnoel.backend.modules.institution.dto.InstitutionCreateDTO;
import com.egnoel.backend.modules.institution.dto.InstitutionResponseDTO;
import com.egnoel.backend.modules.institution.dto.InstitutionUpdateDTO;
import com.egnoel.backend.modules.institution.service.InstitutionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/institutions")
public class InstitutionController {
    private final InstitutionService institutionService;

    @Autowired
    public InstitutionController(InstitutionService institutionService) {
        this.institutionService = institutionService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InstitutionResponseDTO> createInstitution(@Valid @RequestBody InstitutionCreateDTO dto) {
        return ResponseEntity.ok(institutionService.createInstitution(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InstitutionResponseDTO> updateInstitution(@PathVariable Long id,
                                                                    @Valid @RequestBody InstitutionUpdateDTO dto) {
        return ResponseEntity.ok(institutionService.updateInstitution(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteInstitution(@PathVariable Long id) {
        institutionService.deleteInstitution(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<InstitutionResponseDTO>> listInstitutions() {
        return ResponseEntity.ok(institutionService.listInstitutions());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InstitutionResponseDTO> getInstitution(@PathVariable Long id) {
        return ResponseEntity.ok(institutionService.getInstitution(id));
    }
}
