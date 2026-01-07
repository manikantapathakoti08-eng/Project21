package com.project69.project.model;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionFeedback {
    private String questionText;
    private String userResponse;
    private boolean isCorrect;
    private String correctAnswer;
}