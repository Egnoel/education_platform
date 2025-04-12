package com.egnoel.backend.modules.quiz.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class QuizCreateDTO {
    @NotBlank(message = "O título é obrigatório")
    private String title;

    private LocalDateTime terminationDate;

    @NotNull(message = "A disciplina é obrigatória")
    private Long subjectId;

    private Long classeId;
}
