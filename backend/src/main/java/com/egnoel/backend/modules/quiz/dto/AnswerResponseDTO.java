package com.egnoel.backend.modules.quiz.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnswerResponseDTO {
    private Long id;
    private String answers;
    private Integer score;
    private LocalDateTime submittedAt;
    private String studentName;
}
