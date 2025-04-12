package com.egnoel.backend.modules.assessment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssessmentResponseDTO {
    private Long id;
    private String title;
    private Double grade;
    private LocalDateTime date;
    private LocalDateTime creationDate;
    private String studentName;
    private String classeName;
}
