package com.egnoel.backend.modules.subject.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SubjectCreateDTO {
    @NotBlank(message = "O nome da disciplina é obrigatório")
    private String name;

    private String code; // Opcional, sem @NotBlank

    @NotNull(message = "A instituição é obrigatória")
    private Long institutionId;
}
