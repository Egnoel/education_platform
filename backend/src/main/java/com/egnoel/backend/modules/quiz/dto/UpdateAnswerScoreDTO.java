package com.egnoel.backend.modules.quiz.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateAnswerScoreDTO {
    @NotNull(message = "A pontuação é obrigatória")
    private Integer score;

}
