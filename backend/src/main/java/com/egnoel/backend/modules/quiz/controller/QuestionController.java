package com.egnoel.backend.modules.quiz.controller;

import com.egnoel.backend.modules.quiz.dto.QuestionCreateDTO;
import com.egnoel.backend.modules.quiz.dto.QuestionResponseDTO;
import com.egnoel.backend.modules.quiz.dto.QuestionUpdateDTO;
import com.egnoel.backend.modules.quiz.service.QuestionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/api/quizzes/{quizId}/questions")
public class QuestionController {
    private final QuestionService questionService;

    @Autowired
    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @PostMapping
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<QuestionResponseDTO> createQuestion(@PathVariable Long quizId,
                                                              @Valid @RequestBody QuestionCreateDTO dto) {
        return ResponseEntity.ok(questionService.createQuestion(quizId, dto));
    }

    @PutMapping("/{questionId}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<QuestionResponseDTO> updateQuestion(@PathVariable Long quizId,
                                                              @PathVariable Long questionId,
                                                              @Valid @RequestBody QuestionUpdateDTO dto) {
        return ResponseEntity.ok(questionService.updateQuestion(quizId, questionId, dto));
    }

    @DeleteMapping("/{questionId}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Long quizId, @PathVariable Long questionId) {
        questionService.deleteQuestion(quizId, questionId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('TEACHER', 'STUDENT')")
    public ResponseEntity<List<QuestionResponseDTO>> listQuestions(@PathVariable Long quizId) {
        return ResponseEntity.ok(questionService.listQuestions(quizId));
    }
}
