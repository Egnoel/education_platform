package com.egnoel.backend.modules.quiz.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class QuizUpdateDTO {

    @NotBlank(message = "O título é obrigatório")
    private String title;

    private LocalDateTime terminationDate;
}
