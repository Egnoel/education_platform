package com.egnoel.backend.modules.quiz.controller;

import com.egnoel.backend.modules.quiz.dto.QuizCreateDTO;
import com.egnoel.backend.modules.quiz.dto.QuizResponseDTO;
import com.egnoel.backend.modules.quiz.dto.QuizUpdateDTO;
import com.egnoel.backend.modules.quiz.service.QuizService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/api/quizzes")
public class QuizController {
    private final QuizService quizService;

    @Autowired
    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @PostMapping
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<QuizResponseDTO> createQuiz(@Valid @RequestBody QuizCreateDTO dto) {
        return ResponseEntity.ok(quizService.createQuiz(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<QuizResponseDTO> updateQuiz(@PathVariable Long id, @Valid @RequestBody QuizUpdateDTO dto) {
        return ResponseEntity.ok(quizService.updateQuiz(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<Void> deleteQuiz(@PathVariable Long id) {
        quizService.deleteQuiz(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('TEACHER', 'STUDENT')")
    public ResponseEntity<List<QuizResponseDTO>> listQuizzes() {
        return ResponseEntity.ok(quizService.listQuizzes());
    }
}
