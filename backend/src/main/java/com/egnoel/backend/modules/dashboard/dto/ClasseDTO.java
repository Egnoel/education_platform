package com.egnoel.backend.modules.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClasseDTO {
    private Long id;
    private String name;
    private String subjectName;
    private String academicYearName;
    private LocalDateTime creationDate;
}
