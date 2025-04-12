package com.egnoel.backend.modules.dashboard.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuizDTO {
    private Long id;
    private String title;
    private String classeName;
    private LocalDateTime creationDate;
    private boolean hasPendingAnswers;
}
