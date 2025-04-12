package com.egnoel.backend.modules.institution.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class AddUsersToInstitutionDTO {
    @NotEmpty(message = "A lista de IDs de utilizadores não pode estar vazia")
    private List<Long> userIds;
}
