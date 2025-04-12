package com.egnoel.backend.modules.classe.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ClasseCreateDTO {

    @NotBlank(message = "O nome é obrigatório")
    @Size(max = 100, message = "O nome da turma deve ter até 100 caracteres")
    private String name;

    @NotNull(message = "O ano letivo é obrigatório")
    private Long academicYearId;

    @NotNull(message = "A disciplina é obrigatória")
    private Long subjectId;
}
