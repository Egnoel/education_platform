package com.egnoel.backend.modules.material.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MaterialResponseDTO {
    private Long id;
    private String title;
    private String filePath;
    private LocalDateTime uploadDate;
    private String teacherName;
    private String subjectName;
    private String classeName;
}
