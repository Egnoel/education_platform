package com.egnoel.backend.modules.quiz.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnswerCreateDTO {
    @NotBlank(message = "As respostas são obrigatórias")
    private List<QuestionAnswerDTO> answers;

    @Data
    public static class QuestionAnswerDTO {
        private Long questionId;
        private String answer;

    }
}
