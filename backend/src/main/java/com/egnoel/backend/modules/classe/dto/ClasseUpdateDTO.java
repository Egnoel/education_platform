package com.egnoel.backend.modules.classe.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ClasseUpdateDTO {
    @NotBlank(message = "O nome é obrigatório")
    @Size(max = 100, message = "O nome da turma deve ter até 100 caracteres")
    private String name;
}
