package com.egnoel.backend.modules.dashboard.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardResponseDTO {
    private String userName;
    private String role;
    private List<ClasseDTO> classes;
    private List<MaterialDTO> materials;
    private List<QuizDTO> quizzes;
    private List<AssessmentDTO> assessments;
}
