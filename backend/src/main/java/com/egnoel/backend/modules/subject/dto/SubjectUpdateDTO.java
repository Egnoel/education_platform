package com.egnoel.backend.modules.subject.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SubjectUpdateDTO {
    @NotBlank(message = "O nome da disciplina é obrigatório")
    private String name;

    private String code;
}
