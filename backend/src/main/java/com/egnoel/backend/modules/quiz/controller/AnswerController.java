package com.egnoel.backend.modules.quiz.controller;

import com.egnoel.backend.modules.quiz.dto.AnswerCreateDTO;
import com.egnoel.backend.modules.quiz.dto.AnswerResponseDTO;
import com.egnoel.backend.modules.quiz.dto.UpdateAnswerScoreDTO;
import com.egnoel.backend.modules.quiz.service.AnswerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quizzes/{quizId}/answers")
public class AnswerController {
    private final AnswerService answerService;

    @Autowired
    public AnswerController(AnswerService answerService) {
        this.answerService = answerService;
    }

    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<AnswerResponseDTO> submitAnswer(@PathVariable Long quizId,
                                                          @Valid @RequestBody AnswerCreateDTO dto) {
        return ResponseEntity.ok(answerService.submitAnswer(quizId, dto));
    }

    @PutMapping("/{answerId}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<AnswerResponseDTO> updateAnswerScore(@PathVariable Long quizId,
                                                               @PathVariable Long answerId,
                                                               @RequestBody UpdateAnswerScoreDTO dto) {
        return ResponseEntity.ok(answerService.updateAnswerScore(quizId, answerId, dto));
    }

    @GetMapping
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<List<AnswerResponseDTO>> listAnswers(@PathVariable Long quizId) {
        return ResponseEntity.ok(answerService.listAnswers(quizId));
    }
}
