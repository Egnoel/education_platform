package com.egnoel.backend.modules.classe.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClasseResponseDTO {
    private Long id;
    private String name;
    private LocalDateTime creationDate;
    private String academicYearName;
    private String subjectName;
    private String teacherName;
    private List<String> studentNames;
}
