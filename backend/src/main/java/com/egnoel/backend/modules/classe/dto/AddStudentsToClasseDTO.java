package com.egnoel.backend.modules.classe.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class AddStudentsToClasseDTO {
    @NotEmpty(message = "A lista de IDs de alunos não pode estar vazia")
    private List<Long> studentIds;

}
