package com.egnoel.backend.modules.quiz.dto;


import com.egnoel.backend.modules.quiz.entity.QuestionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionResponseDTO {
    private Long id;
    private String text;
    private QuestionType type;
    private String options;
    private String correctAnswer;
    private Integer score;
}
