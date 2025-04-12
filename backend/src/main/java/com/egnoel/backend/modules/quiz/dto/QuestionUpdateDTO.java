package com.egnoel.backend.modules.quiz.dto;

import com.egnoel.backend.modules.quiz.entity.QuestionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class QuestionUpdateDTO {

    @NotBlank(message = "O texto da pergunta é obrigatório")
    private String text;

    @NotNull(message = "O tipo da pergunta é obrigatório")
    private QuestionType type;

    private String options;

    private String correctAnswer;

    @NotNull(message = "A pontuação é obrigatória")
    private Integer score;
}
