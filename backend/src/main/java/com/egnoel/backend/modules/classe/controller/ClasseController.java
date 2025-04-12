package com.egnoel.backend.modules.classe.controller;

import com.egnoel.backend.modules.classe.dto.AddStudentsDTO;
import com.egnoel.backend.modules.classe.dto.ClasseCreateDTO;
import com.egnoel.backend.modules.classe.dto.ClasseResponseDTO;
import com.egnoel.backend.modules.classe.dto.ClasseUpdateDTO;
import com.egnoel.backend.modules.classe.service.ClasseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/classes")
public class ClasseController {
    private final ClasseService classeService;

    @Autowired
    public ClasseController(ClasseService classeService) {
        this.classeService = classeService;
    }

    @PostMapping
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ClasseResponseDTO> createClasse(@Valid @RequestBody ClasseCreateDTO dto) {
        return ResponseEntity.ok(classeService.createClasse(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ClasseResponseDTO> updateClasse(@PathVariable Long id, @Valid @RequestBody ClasseUpdateDTO dto) {
        return ResponseEntity.ok(classeService.updateClasse(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<Void> deleteClasse(@PathVariable Long id) {
        classeService.deleteClasse(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('TEACHER', 'STUDENT')")
    public ResponseEntity<List<ClasseResponseDTO>> listClasses() {
        return ResponseEntity.ok(classeService.listClasses());
    }

    @PostMapping("/{id}/students")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ClasseResponseDTO> addStudents(@PathVariable Long id, @Valid @RequestBody AddStudentsDTO dto) {
        return ResponseEntity.ok(classeService.addStudents(id, dto));
    }

    @DeleteMapping("/{id}/students")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ClasseResponseDTO> removeStudents(@PathVariable Long id, @Valid @RequestBody AddStudentsDTO dto) {
        return ResponseEntity.ok(classeService.removeStudents(id, dto));
    }
}
