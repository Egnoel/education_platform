package com.egnoel.backend.modules.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssessmentDTO {
    private Long id;
    private String title;
    private Double grade;
    private String classeName;
    private String studentName; // Apenas para professores
    private LocalDateTime date;
}
