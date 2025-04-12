package com.egnoel.backend.modules.academicyear.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class AcademicYearCreateDTO {
    @NotBlank(message = "O nome é obrigatório")
    @Size(max = 20, message = "O nome do ano letivo deve ter até 20 caracteres")
    private String name;

    @NotNull(message = "A data de início é obrigatória")
    private LocalDate startDate;

    @NotNull(message = "A data de fim é obrigatória")
    private LocalDate endDate;

    @NotNull(message = "A instituição é obrigatória")
    private Long institutionId;
}
