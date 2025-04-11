package com.egnoel.backend.modules.material.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/materials")
public class MaterialController {

    @PostMapping
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<String> createMaterial() {
        return ResponseEntity.ok("Material criado com sucesso!");
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('TEACHER', 'STUDENT')")
    public ResponseEntity<String> listMaterials() {
        return ResponseEntity.ok("Lista de materiais!");
    }
}
