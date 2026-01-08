package com.project69.project.model;
import lombok.*;
import java.util.List;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizResult {
    private String quizTitle;
    private int totalQuestions;
    private int correctCount;
    private int incorrectCount;
    private int skippedCount;
    private double scorePercentage;
    private boolean passed;
    private List<QuestionFeedback> feedbackList;
}
