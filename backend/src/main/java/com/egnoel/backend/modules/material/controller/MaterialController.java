package com.egnoel.backend.modules.material.controller;


import com.egnoel.backend.modules.material.dto.MaterialCreateDTO;
import com.egnoel.backend.modules.material.dto.MaterialResponseDTO;
import com.egnoel.backend.modules.material.dto.MaterialUpdateDTO;
import com.egnoel.backend.modules.material.service.MaterialService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/materials")
public class MaterialController {

    private final MaterialService materialService;

    @Autowired
    public MaterialController(MaterialService materialService) {
        this.materialService = materialService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<MaterialResponseDTO> createMaterial(@Valid @ModelAttribute MaterialCreateDTO dto) {
        return ResponseEntity.ok(materialService.createMaterial(dto));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<MaterialResponseDTO> updateMaterial(@PathVariable Long id, @Valid @ModelAttribute MaterialUpdateDTO dto) {
        return ResponseEntity.ok(materialService.updateMaterial(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<Void> deleteMaterial(@PathVariable Long id) {
        materialService.deleteMaterial(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('TEACHER', 'STUDENT')")
    public ResponseEntity<List<MaterialResponseDTO>> listMaterials() {
        return ResponseEntity.ok(materialService.listMaterials());
    }
}
