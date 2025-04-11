package com.egnoel.backend.modules.subject.controller;


import com.egnoel.backend.modules.subject.dto.SubjectCreateDTO;
import com.egnoel.backend.modules.subject.dto.SubjectResponseDTO;
import com.egnoel.backend.modules.subject.dto.SubjectUpdateDTO;
import com.egnoel.backend.modules.subject.service.SubjectService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subjects")
public class SubjectController {

    private final SubjectService subjectService;

    @Autowired
    public SubjectController(SubjectService subjectService) {
        this.subjectService = subjectService;
    }

    @PostMapping
    @PreAuthorize("hasRole('TEACHER')") // Apenas professores criam disciplinas por agora
    public ResponseEntity<SubjectResponseDTO> createSubject(@Valid @RequestBody SubjectCreateDTO dto) {
        return ResponseEntity.ok(subjectService.createSubject(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<SubjectResponseDTO> updateSubject(@PathVariable Long id, @Valid @RequestBody SubjectUpdateDTO dto) {
        return ResponseEntity.ok(subjectService.updateSubject(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<Void> deleteSubject(@PathVariable Long id) {
        subjectService.deleteSubject(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('TEACHER', 'STUDENT')")
    public ResponseEntity<List<SubjectResponseDTO>> listSubjects() {
        return ResponseEntity.ok(subjectService.listSubjects());
    }
}
