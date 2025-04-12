package com.egnoel.backend.modules.assessment.controller;

import com.egnoel.backend.modules.assessment.dto.AssessmentCreateDTO;
import com.egnoel.backend.modules.assessment.dto.AssessmentResponseDTO;
import com.egnoel.backend.modules.assessment.dto.AssessmentUpdateDTO;
import com.egnoel.backend.modules.assessment.service.AssessmentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assessments")
public class AssessmentController {
    private final AssessmentService assessmentService;

    @Autowired
    public AssessmentController(AssessmentService assessmentService) {
        this.assessmentService = assessmentService;
    }

    @PostMapping
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<AssessmentResponseDTO> createAssessment(@Valid @RequestBody AssessmentCreateDTO dto) {
        return ResponseEntity.ok(assessmentService.createAssessment(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<AssessmentResponseDTO> updateAssessment(@PathVariable Long id,
                                                                  @Valid @RequestBody AssessmentUpdateDTO dto) {
        return ResponseEntity.ok(assessmentService.updateAssessment(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<Void> deleteAssessment(@PathVariable Long id) {
        assessmentService.deleteAssessment(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'STUDENT')")
    public ResponseEntity<List<AssessmentResponseDTO>> listAssessmentsByStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(assessmentService.listAssessmentsByStudent(studentId));
    }

    @GetMapping("/classe/{classeId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'STUDENT')")
    public ResponseEntity<List<AssessmentResponseDTO>> listAssessmentsByClasse(@PathVariable Long classeId) {
        return ResponseEntity.ok(assessmentService.listAssessmentsByClasse(classeId));
    }
}
