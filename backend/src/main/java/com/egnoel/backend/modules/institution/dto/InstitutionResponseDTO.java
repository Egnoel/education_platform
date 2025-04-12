package com.egnoel.backend.modules.institution.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InstitutionResponseDTO {
    private Long id;
    private String name;
    private String location;
    private String contact;
    private LocalDateTime creationDate;
    private List<String> userNames;
    private List<String> academicYearNames;
}
