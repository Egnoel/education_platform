package com.egnoel.backend.modules.auth.controller;

import com.egnoel.backend.modules.auth.dto.*;
import com.egnoel.backend.modules.auth.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register/teacher")
    public ResponseEntity<AuthResponseDTO> registerTeacher(@RequestBody TeacherRegisterDTO dto) {
        AuthResponseDTO response = authService.registerTeacher(dto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register/student")
    public ResponseEntity<AuthResponseDTO> registerStudent(@RequestBody StudentRegisterDTO dto) {
        AuthResponseDTO response = authService.registerStudent(dto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register/admin")
    public ResponseEntity<AuthResponseDTO> registerAdmin(@RequestBody AdminRegisterDTO dto) {
        AuthResponseDTO response = authService.registerAdmin(dto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginRequestDTO dto) {
        AuthResponseDTO response = authService.login(dto);
        return ResponseEntity.ok(response);
    }
}
