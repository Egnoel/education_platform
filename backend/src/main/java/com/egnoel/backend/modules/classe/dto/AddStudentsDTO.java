package com.egnoel.backend.modules.classe.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

import lombok.Data;

@Data
public class AddStudentsDTO {
    @NotEmpty(message = "A lista de IDs de alunos não pode estar vazia")
    private List<Long> studentIds;

}
