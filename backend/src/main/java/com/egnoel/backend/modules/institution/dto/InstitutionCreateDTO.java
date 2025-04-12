package com.egnoel.backend.modules.institution.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class InstitutionCreateDTO {
    @NotBlank(message = "O nome é obrigatório")
    @Size(max = 100, message = "O nome da instituição deve ter até 100 caracteres")
    private String name;

    @Size(max = 255, message = "A localização deve ter até 255 caracteres")
    private String location;

    @Size(max = 100, message = "O contacto deve ter até 100 caracteres")
    private String contact;
}
