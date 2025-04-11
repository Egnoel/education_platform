package com.egnoel.backend.modules.subject.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubjectResponseDTO {
    private Long id;
    private String name;
    private String code;
    private String institutionName;
}
