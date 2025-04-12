package com.egnoel.backend.modules.quiz.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuizResponseDTO {

    private Long id;
    private String title;
    private LocalDateTime creationDate;
    private LocalDateTime terminationDate;
    private String teacherName;
    private String subjectName;
    private String classeName;
}
