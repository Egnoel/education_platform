package com.egnoel.backend.modules.assessment.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AssessmentCreateDTO {
    @NotBlank(message = "O título é obrigatório")
    @Size(max = 100, message = "O título deve ter até 100 caracteres")
    private String title;

    @NotNull(message = "A nota é obrigatória")
    @Min(value = 0, message = "A nota não pode ser inferior a 0")
    @Max(value = 20, message = "A nota não pode ser superior a 20")
    private Double grade;

    @NotNull(message = "A data é obrigatória")
    private LocalDateTime date;

    @NotNull(message = "O ID do aluno é obrigatório")
    private Long studentId;

    @NotNull(message = "O ID da turma é obrigatório")
    private Long classeId;
}
